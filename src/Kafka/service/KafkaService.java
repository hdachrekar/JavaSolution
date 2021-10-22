/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.service;

import catalog.EcwCacheManager;
import com.ecw.connect.EcwRemoteConnect;
import com.ecw.encryption.Aes128Kafka;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ereferralemr.Util;
import ereferralemr.kafka.client.KafkaConsumerClient;
import ereferralemr.kafka.client.KafkaProducerClient;
import ereferralemr.kafka.model.KafkaConfig;
import ereferralemr.kafka.model.KafkaObject;
import ereferralemr.kafka.model.KafkaObjectManager;
import ereferralemr.kafka.model.KafkaObserver;
import ereferralemr.kafka.model.KafkaProducerParams;
import ereferralemr.kafka.model.KafkaProperty;
import ereferralemr.kafka.model.KafkaTopic;
import ereferralemr.kafka.repository.IKafkaRepository;
import org.apache.kafka.clients.CommonClientConfigs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static ereferralemr.kafka.client.KafkaConsumerClient.getClosed;
import static ereferralemr.kafka.client.KafkaConsumerClient.getKafkaConsumerInstance;
@Configuration
@ComponentScan(basePackages = {"ereferralemr.kafka"})
@Qualifier("KafkaService")
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class KafkaService implements IKafkaService {
    @Autowired
    @Qualifier("KafkaRepository")
    private IKafkaRepository kafkaRepository;

    private static Map kafkaTopicProducerMap;
    private static Map kafkaTopicConsumerMap;
    private static KafkaConfig kafkaConfig;

    public static final int THREAD_TIMEOUT=200;
    private static final int DEFAULT_OBJECT_WAIT_IN_MS=10000;

    public static final int HTTP_READ_TIMEOUT = 60000;
    public static final int HTTP_CONN_TIMEOUT = 30000;
    private static final int MAX_RESPONSE_SIZE = 1024;

    public void setKafkaConfig(KafkaConfig kafkaConfigObj) {
        kafkaConfig = kafkaConfigObj;
    }
    public static void setKafkaTopicProducerMap(Map kafkaTopicProducerMap) {
        KafkaService.kafkaTopicProducerMap = kafkaTopicProducerMap;
    }
    public static void setKafkaTopicConsumerMap(Map kafkaTopicConsumerMap) {
        KafkaService.kafkaTopicConsumerMap = kafkaTopicConsumerMap;
    }
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
    @Override
    public KafkaConfig getConfigurations() {
        try {
            if (null==kafkaConfig) {
                List<KafkaProperty> kafkaPropertyList = kafkaRepository.getKafkaPropertyList();
                Map<String, Object> configurationMap = new HashMap();
                if (null!=kafkaPropertyList) {
                    for (KafkaProperty kafkaPropertyObject : kafkaPropertyList) {
                        configurationMap.put(kafkaPropertyObject.getKey(), kafkaPropertyObject.getValue());
                    }
                    ObjectMapper objectMapper = new ObjectMapper();
                    setKafkaConfig(objectMapper.convertValue(configurationMap, new TypeReference<KafkaConfig>() {}));
                }
            }
        } catch (IllegalArgumentException   ex) {
            LOGGER.error("IllegalArgumentException while creating kafkaConfigObject", ex);
        }
        return kafkaConfig;
    }
    @Override
    public void setupKafkaService() {
        int enableKafkaPolling = "true".equalsIgnoreCase(Util.getNHXKafkaPollenabled()) ? 1 : 0;
        if (enableKafkaPolling == 1) {
            toggleKafkaConsumer(enableKafkaPolling);
        }
    }
    @Override
    public void toggleKafkaConsumer(int enableKafkaPolling) {
        try {
            final KafkaConsumerClient kafkaConsumerClient = getKafkaConsumerInstance();
            if (enableKafkaPolling == 1)//enable kafka  polling
            {
                if(null!=kafkaConsumerClient){
                startKafkaConsumer(kafkaConsumerClient);
                Util.toggleNHXKafkaPollEnable("true");
                }
            } else {
                if (null!=kafkaConsumerClient && !getClosed().get()) {
                    kafkaConsumerClient.shutdown();
                }
                Util.toggleNHXKafkaPollEnable("false");
            }
        } catch (SQLException| ConcurrentModificationException | IllegalStateException e) {
            try{
                Util.toggleNHXKafkaPollEnable("false");
            }catch(Exception sqex){
                LOGGER.error("Error while toggling between kafka poll and shutdown after an exception has occurred.", e);
            }
            LOGGER.error("Error while toggling between kafka poll and shutdown.", e);
        }
    }
    @Override
    public Map<Integer, List<KafkaTopic>> getPerPartitionKafkaTopicList(int isConsumer, int isProducer,String guid) {
        Map<Integer, List<KafkaTopic>> finalTopicMap = null;
        if (isConsumer == 1)
        {
            if (kafkaTopicConsumerMap == null) {
                setKafkaTopicConsumerMap(kafkaRepository.getByPartitionKafkaTopicList(isConsumer, isProducer,guid));
            }
            finalTopicMap = kafkaTopicConsumerMap;
        }
        if (isProducer == 1)
        {
            if (kafkaTopicProducerMap == null) {
                setKafkaTopicProducerMap(kafkaRepository.getByPartitionKafkaTopicList(isConsumer, isProducer,guid));
            }
            finalTopicMap = kafkaTopicProducerMap;
        }
        return finalTopicMap;
    }

    @Override
    public synchronized String publish(KafkaProducerParams kafkaProducerParams) {
        String topicResponse="";
        try {
			kafkaConfig = getConfigurations();
            String guid = kafkaProducerParams.getMessageId();
            KafkaObject kafkaObject = new KafkaObject(kafkaProducerParams.getTopicData());
            KafkaObjectManager.setKafkaObject(guid, kafkaObject);
            KafkaObserver kafkaObserver = new KafkaObserver(kafkaObject);
            publish(kafkaProducerParams.getTopicData(), kafkaProducerParams.getTopicName(), kafkaProducerParams.getTopicType());
            LOGGER.debug("Healow kafka request topic name: "+kafkaProducerParams.getTopicName());
            LOGGER.debug("Healow kafka request topic type: "+kafkaProducerParams.getTopicType());
            synchronized (kafkaObject) {
                int timeoutCounter = 0;
                while (kafkaObserver.getKafkaResponse().isEmpty()) {
                    timeoutCounter++;
                    if (timeoutCounter == TimeUnit.MILLISECONDS.toSeconds(kafkaConfig.getKafkaConnectionTimeout())) {
                        LOGGER.debug("Healow kafka response connection timeout.");
                        break;
                    }
                    kafkaObject.wait(DEFAULT_OBJECT_WAIT_IN_MS);
                }
            }
            LOGGER.debug("Healow kafka response: "+new Aes128Kafka().encrypt(kafkaObserver.getKafkaResponse()));
            topicResponse= kafkaObserver.getKafkaResponse();
            KafkaObjectManager.removeKafkaObject(guid);
        } catch (InterruptedException ex) {
            LOGGER.error(ex.getMessage(), ex);
            Thread.currentThread().interrupt();
        }
        return topicResponse;
    }

    @Override
    public void publish(String data, String topicName,String topicType) {
            /*
                Varuna: for the emr producer (publish to kafka) the guid is not required as the topicname is always "healow_response". Hence passing 0.
                pl. check with me or harsh before making any changes to the below.
             */
        Map<Integer, List<KafkaTopic>> kafkaTopicsByPartitionMap = getPerPartitionKafkaTopicList(0, 1,"0");
        for (Map.Entry<Integer, List<KafkaTopic>> entry : kafkaTopicsByPartitionMap.entrySet()) {
            List<KafkaTopic> kafkaTopicList=  entry.getValue();
            for (KafkaTopic kafkaTopic : kafkaTopicList){
                if(kafkaTopic.getTopicName().equalsIgnoreCase(topicName)){
                    new KafkaProducerClient(data, topicName,topicType, kafkaTopic.getPartitionId()).publishToKafkaTopic();
                    return;
                }
            }
        }
    }
    @Override
    public Map<String,Long> getVerifiedKafkaTopicMap() {
        return kafkaRepository.getVerifiedKafkaTopicMap();
    }

    @Override
    public int updateVerifiedFlagByKafkaTopicId(Long id) {
         return kafkaRepository.updateVerifiedFlagByKafkaTopicId(id);
    }

    @Override
    public void updateKafkaConsumerServerConfig(String bootstrapServer, String jaasUsername, String jaasPassword,String kafkaGuid) {
        try{
            String decBootstrapServer= Aes128Kafka.decrypt(bootstrapServer);
            String decJaasUsername= Aes128Kafka.decrypt(jaasUsername);
            String decJaasPassword= Aes128Kafka.decrypt(jaasPassword);
            if(!decBootstrapServer.isEmpty() && !decJaasUsername.isEmpty() && !decJaasPassword.isEmpty()) {
                List<KafkaProperty> kafkaPropertyList = new ArrayList<>();
                KafkaProperty kafkaProperty = new KafkaProperty();
                kafkaProperty.setKey(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG);
                kafkaProperty.setValue(bootstrapServer);
                kafkaPropertyList.add(kafkaProperty);

                kafkaProperty = new KafkaProperty();
                kafkaProperty.setKey("jaas.config.username");
                kafkaProperty.setValue(jaasUsername);
                kafkaPropertyList.add(kafkaProperty);

                kafkaProperty = new KafkaProperty();
                kafkaProperty.setKey("jaas.config.password");
                kafkaProperty.setValue(jaasPassword);
                kafkaPropertyList.add(kafkaProperty);

                kafkaProperty = new KafkaProperty();
                kafkaProperty.setKey("kafka.guid");
                kafkaProperty.setValue(kafkaGuid);
                kafkaPropertyList.add(kafkaProperty);
                kafkaRepository.updateKafkaConsumerServerConfig(kafkaPropertyList);
                kafkaConfig = null;
            }
        }catch(Exception ex){
            LOGGER.error("Error while updating the kafka config data", ex);
        }
    }

    @Override
    public void toggleConsumerOnAPPServers() {
        Set<String> consumerUrls = kafkaRepository.getAPPServersConsumerURLs();
        if(!CollectionUtils.isEmpty(consumerUrls)){

            for(final String urlWeb : consumerUrls){
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try{
                            int userId = EcwCacheManager.getInstance().getItemkeyId("Administrator");
                            URL url = new URL(urlWeb);
                            URLConnection urlConnection = url.openConnection();
                            String strProtocol = url.getProtocol();
                            String strHost = url.getHost();
                            int port = url.getPort();

                            String jsessionid = EcwRemoteConnect.getSessionId(strProtocol, strHost, String.valueOf(port), userId);
                            urlConnection.setRequestProperty("Cookie", "JSESSIONID="+jsessionid);

                            urlConnection.setConnectTimeout(HTTP_CONN_TIMEOUT);
                            urlConnection.setReadTimeout(HTTP_READ_TIMEOUT);
                            try(InputStream is = urlConnection.getInputStream();
                                InputStreamReader isr= new InputStreamReader(is);
                            ) {
                                int numCharsRead;
                                char[] charArray = new char[MAX_RESPONSE_SIZE];
                                StringBuilder sb = new StringBuilder();
                                while ((numCharsRead = isr.read(charArray)) > 0) {
                                    sb.append(charArray, 0, numCharsRead);
                                }
                                String strStatus = sb.toString().trim();
                                LOGGER.info("Consumer status on {} : {}",strHost+":"+port, strStatus);
                            }
                            EcwRemoteConnect.logoutFromRemoteServer(strProtocol, strHost, String.valueOf(port), jsessionid);
                        }catch (IOException |RuntimeException e) {
                            LOGGER.error(e.getMessage(), e);
                        }
                    }
                }).start();
            }
        }
    }

    private void startKafkaConsumer(final KafkaConsumerClient kafkaConsumerClient) {
        final ExecutorService executorService = Executors.newFixedThreadPool(1);
        getClosed().set(false);
        executorService.execute(kafkaConsumerClient);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            kafkaConsumerClient.shutdown();
            executorService.shutdown();
            try {
                executorService.awaitTermination(THREAD_TIMEOUT, TimeUnit.MILLISECONDS);
            } catch (InterruptedException | ConcurrentModificationException e) {
                LOGGER.error("Shutting down executorService for workers systematically", e);
                Thread.currentThread().interrupt();
            }
        }));
    }
}
