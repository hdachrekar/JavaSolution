/*
 * @author Harshavardhan Achrekar
 *
 * */

package ereferralemr.kafka.client;
import ereferralemr.kafka.config.EMRAppContext;
import ereferralemr.kafka.model.KafkaConfig;
import ereferralemr.kafka.model.KafkaTopic;
import ereferralemr.kafka.service.IKafkaService;
import ereferralemr.kafka.service.KafkaService;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerClient implements Runnable {
    private static KafkaConsumerClient singleInstance;
    private static KafkaConsumer<String, String> consumer = null;
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerClient.class);
    private final Map<TopicPartition, OffsetAndMetadata> partitionToUncommittedOffsetMap = new HashMap<TopicPartition, OffsetAndMetadata>();
    private static OffsetCommitCallback callback;
    private static AtomicBoolean closed = new AtomicBoolean(false);
    private  int NUMBER_OF_THREADS=3;
    private  int THREAD_CAPACITY=1000;
    private  int NUMBER_OF_THREADS_MAX_ALLOWED=5;
    private  long THREADS_KEEP_ALIVE_TIME=500;
    private  int POLL_TIMEOUT_IN_MS=100;

    public static synchronized KafkaConsumerClient getKafkaConsumerInstance() {
        if (null == singleInstance) {
            singleInstance = new KafkaConsumerClient();
        }
        return singleInstance;
    }

    private KafkaConsumerClient() {
        IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
        KafkaConfig kafkaConfig = kafkaService.getConfigurations();
        NUMBER_OF_THREADS=  Integer.parseInt(kafkaConfig.getConsumerThreads());
        THREAD_CAPACITY=Integer.parseInt(kafkaConfig.getConsumerThreadCapacity());
        NUMBER_OF_THREADS_MAX_ALLOWED = Integer.parseInt(kafkaConfig.getConsumerThreadsMaxlimit());
        THREADS_KEEP_ALIVE_TIME = Long.parseLong(kafkaConfig.getConsumerThreadsKeepAlive());
        POLL_TIMEOUT_IN_MS=Integer.parseInt(kafkaConfig.getConsumerThreadsPollTimeoutInterval());
        Map<Integer, List<KafkaTopic>> kafkaTopicsByPartitionMap = kafkaService.getPerPartitionKafkaTopicList(1, 0,kafkaConfig.getGuid());
        setKafkaConsumerClientSubscriptionList(kafkaConfig, kafkaTopicsByPartitionMap);
    }

    public static AtomicBoolean getClosed() {
        return closed;
    }

    private synchronized  void setKafkaConsumerClientSubscriptionList( KafkaConfig kafkaConfig, Map<Integer, List<KafkaTopic>> kafkaTopicsByPartitionMap) {
        try {
            if(null!=kafkaConfig && null!=kafkaConfig.getBootstrapServers() && null!=kafkaConfig.getJaasConfigUsername() && null!=kafkaConfig.getJaasConfigPassword()){
                List<TopicPartition> partitions = getTopicPartitions(kafkaTopicsByPartitionMap);
                if(partitions.size()>0 && !partitions.isEmpty()){
                    final Properties props = new Properties();
                    createConsumerConfig(props, kafkaConfig);
                    consumer = new KafkaConsumer<>(props);
                    consumer.assign(partitions);
                    callback = new KafkaOffsetCommitCallback();
                }
            }
        } catch (IllegalArgumentException  | KafkaException ex) {
            logger.error("Error while publishing to kafka consumer", ex);
        }
    }

    private static List<TopicPartition> getTopicPartitions(Map<Integer, List<KafkaTopic>> kafkaTopicsByPartitionMap) {
        List<TopicPartition> partitions = new ArrayList<>();
        for (Map.Entry<Integer, List<KafkaTopic>> partition : kafkaTopicsByPartitionMap.entrySet()) {
            Integer partitionId = partition.getKey();
            List<KafkaTopic> kafkaTopicList = partition.getValue();
            kafkaTopicList.forEach(kafkaTopic -> {
                String topicName =  kafkaTopic.getTopicName();
                Long kafkaTopicIdUpdated= KafkaAdminUtilityClient.kafkaTopicExists(kafkaTopic.getTopicName(),kafkaTopic.getId());
                if(kafkaTopicIdUpdated>0){
                    TopicPartition topicPartition = new TopicPartition(topicName, partitionId);
                    partitions.add(topicPartition);
                }
            });
        }
        return partitions;
    }

    private static final void createConsumerConfig(Properties props, KafkaConfig kafkaConfig) {
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConfig.getGroupId());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
        props.put(ConsumerConfig.CLIENT_DNS_LOOKUP_CONFIG, kafkaConfig.getDnsLookUpConfig());
        String jaasCfg = String.format(kafkaConfig.getJaasTemplate(), kafkaConfig.getJaasConfigUsername(), kafkaConfig.getJaasConfigPassword());
        props.put("security.protocol", kafkaConfig.getSecurityProtocol());
        props.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
        props.put("auto.offset.reset", kafkaConfig.getAutoOffsetReset());
        props.put("sasl.jaas.config", jaasCfg);
        props.put("fetch.min.bytes", kafkaConfig.getFetchMinBytes());
        props.put("default.api.timeout.ms", kafkaConfig.getDefaultApiTimeoutMs());
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, kafkaConfig.getEnableAutoCommit()); //Imp low -> If true the consumer's offset will be periodically committed in the background.
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, kafkaConfig.getAutoCommitIntervalMs()); // Imp med -> The frequency in milliseconds that the consumer offsets are auto-committed to Kafka if enable.auto.commit is set to true.
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConfig.getMaxPollRecords()); // Imp med -> The maximum number of records returned in a single call to poll().
        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConfig.getMaxPollIntervalMs()); // Imp med ->The maximum delay between invocations of poll() when using consumer group management.
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConfig.getHeartbeatIntervalMs()); //Imp high -> Heartbeats are used to ensure that the worker's session stays active and to facilitate re-balancing when new members join or leave the group. The value must be set lower than session.timeout.ms
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConfig.getSessionTimeoutMs()); // Imp high -> The timeout used to detect worker failures. The worker sends periodic heartbeats to indicate its liveness to the broker.
    }

    public static synchronized void shutdown() {
        closed.set(true);
        // the wakeup() method is needed to interrupt consumer.poll() for shutting down.
        consumer.wakeup();
        singleInstance=null;
    }

    private void commitOffsets(KafkaConsumer<String, String> consumer) {
        if (null!= consumer && !partitionToUncommittedOffsetMap.isEmpty()) {
            Map<TopicPartition, OffsetAndMetadata> partitionToMetadataMap = new HashMap<>();
            for (Map.Entry<TopicPartition, OffsetAndMetadata> e : partitionToUncommittedOffsetMap.entrySet()) {
                partitionToMetadataMap.put(e.getKey(), new OffsetAndMetadata(e.getValue().offset() + 1, "no metadata"));
            }
            logger.info("committing the offsets : {}", partitionToMetadataMap);
            consumer.commitAsync(partitionToMetadataMap, callback);
            partitionToUncommittedOffsetMap.clear();
        }
    }

    private static class KafkaOffsetCommitCallback implements OffsetCommitCallback {
        @Override
        public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
            if (null!=exception) {
                logger.error("Error while committing offsets : {}", offsets);
            } else {
                logger.info(" successfully committed offsets : {} successfully", offsets);
            }
        }
    }

    @Override
    public void run() {
        try {
             /*
            Initialize a ThreadPool with size = 3 and use the BlockingQueue with size =1000 to hold submitted tasks.
            Threadpool of consumers
             */
            ExecutorService executor = new ThreadPoolExecutor(NUMBER_OF_THREADS, NUMBER_OF_THREADS_MAX_ALLOWED, THREADS_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(THREAD_CAPACITY), new ThreadPoolExecutor.CallerRunsPolicy());
            while (!closed.get()) {
                synchronized (consumer) {
                    ConsumerRecords<String, String> records = null;
                    records = consumer.poll(Duration.ofMillis(POLL_TIMEOUT_IN_MS));
                    for (final ConsumerRecord<String,String> record : records) {
                        partitionToUncommittedOffsetMap.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset(), "no metadata"));
                        logger.info("uncommitting offsets for committing : {}", partitionToUncommittedOffsetMap);
                        executor.submit(new KafkaConsumerThreadHandler(record));
                        commitOffsets(consumer);
                    }
                }
            }
           Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                   shutdown();
                }
            });
        } catch (WakeupException ex) {
            logger.error("Error in consumer client wakeup call :{}",ex.getMessage(),ex);
        }  finally {
            try{
                consumer.commitSync();
                consumer.close();
            }catch(IllegalStateException | KafkaException iex){
                logger.error("Error in while committing the consumer sync and closing the consumer :{}",iex.getMessage(),iex);
            }
        }
    }
}
