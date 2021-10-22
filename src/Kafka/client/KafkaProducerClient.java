/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.client;

import com.ecw.encryption.Aes128Kafka;
import ereferralemr.Util;
import ereferralemr.kafka.config.EMRAppContext;
import ereferralemr.kafka.model.KafkaConfig;
import ereferralemr.kafka.model.KafkaTopic;
import ereferralemr.kafka.model.Payload;
import ereferralemr.kafka.service.IKafkaService;
import ereferralemr.kafka.service.KafkaService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.config.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static ereferralemr.kafka.client.KafkaAdminUtilityClient.kafkaTopicExists;

public class KafkaProducerClient implements Runnable {
    private String key;
    private String value;
    private String topicName;
    private int partition;
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerClient.class);
    private Payload payload;
    private static final String GUID_SALT= "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int GUID_LENGTH=30;
    private static final String DEFAULT_ENCODING="UTF-8";
    private static final int IVCODE_LENGTH = 16;
    private  int NUMBER_OF_THREADS = 5;
    private  int THREAD_CAPACITY=1000;
    private  int NUMBER_OF_THREADS_MAX_ALLOWED=10;
    private  long THREADS_KEEP_ALIVE_TIME=500;
    private boolean THREADS_ALLOW_CORE_THREAD_TIMEOUT = true;
    public KafkaProducerClient(String value, String topicName,String topicType, int partition) {
        this.payload = new Payload(topicType,value);
        this.value=getEncryptedPayLoad(payload);
        this.topicName = topicName;
        this.partition = partition;
    }

    private String getEncryptedPayLoad(Payload payload){
        String encryptedPayload="";
        try{
            this.key =getEncryptionKey(Util.getAPUID());
            byte[] plainTextBytes = key.getBytes(DEFAULT_ENCODING);
            encryptedPayload =  new Aes128Kafka().encrypt(payload.toString(),plainTextBytes);
        }catch(UnsupportedEncodingException ex){
            logger.error("error encrypting the response payload in the producerclient", ex);
        }
        return encryptedPayload;
    }
    public String getEncryptionKey(String apuId) {
       String apuKey= apuId.concat(RandomStringUtils.random(GUID_LENGTH,0,GUID_SALT.length(), true, true,GUID_SALT.toCharArray()));
        apuKey = apuKey.length() > IVCODE_LENGTH ? apuKey.substring(0, IVCODE_LENGTH) :apuKey;
        return apuKey;
    }

    @Override
    public void run() {
        publishToKafkaBroker();
    }

    private void publishToKafkaBroker() {
        try (Producer<String, String> kafkaProducer = createProducer()) {
            if (null!=kafkaProducer) {
                ProducerRecord<String, String> record = new ProducerRecord<>(topicName, partition, key, value);
                kafkaProducer.send(record);
            }
        } catch (ConfigException | IllegalArgumentException e) {
            logger.error("error loading configuration into producer", e);
            if (Thread.interrupted()) {
                logger.error("interrupted while sending record to producer", e);
            }
        }
    }

    private Producer<String, String> createProducer() {
        KafkaProducer<String, String> kafkaProducer = null;
        boolean kafkaTopicExists = false;
        try {
            IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
            KafkaConfig kafkaConfig = kafkaService.getConfigurations();
            if(null!=kafkaConfig && null!=kafkaConfig.getBootstrapServers() && null!=kafkaConfig.getJaasConfigUsername() && null!=kafkaConfig.getJaasConfigPassword()){
            /*
                Varuna: for the emr producer the guid is not required as the topicname is "healow_response". Hence passing 0.
                pl. check with me or harsh before making any changes to the below.

             */
                Map<Integer, List<KafkaTopic>> kafkaTopicsByPartitionMap = kafkaService.getPerPartitionKafkaTopicList(0, 1,"0");
                List<KafkaTopic> kafkaTopicList = kafkaTopicsByPartitionMap.get(partition);
                for (KafkaTopic kafkaTopic : kafkaTopicList) {
                    if (kafkaTopic.getTopicName().equalsIgnoreCase(topicName) && kafkaTopic.getVerified() == 1) {
                        kafkaTopicExists = true;
                        break;
                    }
                    if (kafkaTopic.getTopicName().equalsIgnoreCase(topicName) && kafkaTopicExists(topicName, kafkaTopic.getId()) > 0) {
                        kafkaTopicExists = true;
                        break;
                    }
                }
                if (kafkaTopicExists) {
                    final Properties props = new Properties();
                    setupBootstrap(props, kafkaConfig);
                    kafkaProducer = new KafkaProducer<String, String>(props);
                }
            }
            NUMBER_OF_THREADS=  Integer.parseInt(kafkaConfig.getProducerThreads());
            THREAD_CAPACITY=Integer.parseInt(kafkaConfig.getProducerThreadCapacity());
            NUMBER_OF_THREADS_MAX_ALLOWED = Integer.parseInt(kafkaConfig.getProducerThreadsMaxlimit());
            THREADS_KEEP_ALIVE_TIME = Long.parseLong(kafkaConfig.getProducerThreadsKeepAlive());
            THREADS_ALLOW_CORE_THREAD_TIMEOUT = Boolean.parseBoolean(kafkaConfig.getProducerThreadsAllowCoreThreadTimeOut());
        } catch (KafkaException |IndexOutOfBoundsException | IllegalArgumentException ex) {
            logger.error("Error while publishing to kafka producer", ex);
        }
        return kafkaProducer;
    }

    private static final void setupBootstrap(Properties props, KafkaConfig kafkaConfig) {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        String jaasCfg = String.format(kafkaConfig.getJaasTemplate(), kafkaConfig.getJaasConfigUsername(), kafkaConfig.getJaasConfigPassword());
        props.put("security.protocol", kafkaConfig.getSecurityProtocol());
        props.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
        props.put("sasl.jaas.config", jaasCfg);
        props.put(ProducerConfig.ACKS_CONFIG, kafkaConfig.getAcks()); //Imp: Low ->  The number of acknowledgments the producer requires the leader to have received before considering a request complete. ack=0/1/all
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, kafkaConfig.getMaxFlightReqPerConnection()); //Imp: Low ->The maximum number of unacknowledged requests the client will send on a single connection before blocking. Only one in-flight messages per Kafka broker connection // - max.in.flight.requests.per.connection (default 5)
        props.put(ProducerConfig.RETRIES_CONFIG, kafkaConfig.getRetries()); //Imp: High -> Set the number of retries - retries
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, Integer.parseInt(kafkaConfig.getRequestTimeoutMs())); //Imp: Medium -> Request timeout - request.timeout.ms. The configuration controls the maximum amount of time the client will wait for the response of a request.
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, Long.parseLong(kafkaConfig.getRetryBackoffMs())); //Imp: low -> Only retry after one second.
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, Integer.parseInt(kafkaConfig.getBatchSize())); //Imp: Med -> Batch up to 64K buffer sizes. This configuration controls the default batch size in bytes.
        props.put(ProducerConfig.LINGER_MS_CONFIG, kafkaConfig.getLingerMs()); //Imp: Med -> Linger up to 1 ms before sending batch if size not met. Adds a small amount of artificial delay.
        props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, kafkaConfig.getBufferMemory()); // Imp High -> The total bytes of memory the producer can use to buffer records waiting to be sent to the server.
        props.put(ProducerConfig.CLIENT_DNS_LOOKUP_CONFIG, kafkaConfig.getDnsLookUpConfig()); // Imp - Medium
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, KafkaProducerInterceptor.class.getCanonicalName());
    }

    public void publishToKafkaTopic() {

        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(NUMBER_OF_THREADS, NUMBER_OF_THREADS_MAX_ALLOWED, THREADS_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(THREAD_CAPACITY), new ThreadPoolExecutor.CallerRunsPolicy());
        if(THREADS_ALLOW_CORE_THREAD_TIMEOUT) {
            threadPoolExecutor.allowCoreThreadTimeOut(true);
        }
        final ExecutorService executorService =threadPoolExecutor;
        executorService.submit((Runnable) this);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(KafkaService.THREAD_TIMEOUT, TimeUnit.MILLISECONDS);
                logger.info("Shutting down executorService for producer workers systematically");
            } catch (InterruptedException e) {
                logger.error("shutting down", e);
                Thread.currentThread().interrupt();
            }
        }));
    }
}