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
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.ListTopicsOptions;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.errors.TopicExistsException;
import org.apache.logging.log4j.core.util.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;

public class KafkaAdminUtilityClient {
    private static final Logger logger = LoggerFactory.getLogger(KafkaAdminUtilityClient.class);
    private static Map<String, Long> uniqueTopicNameMap =null;
    private static AtomicBoolean oneTimeGetVerifiedTopicNames = new AtomicBoolean(false);
    private static Properties config = new Properties();

    /*
       *  EMR (client) should not have the ability to create kafka topics hence below method is NOT IN USE. pl. check with me before using the same.
     */
    public static synchronized void createKafkaTopic(String topicName, KafkaTopic kafkaTopic) {
        IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
        KafkaConfig kafkaConfig = kafkaService.getConfigurations();
        if(null!=kafkaConfig){
            setAdminConfigProperties(kafkaConfig);
            try (AdminClient admin = AdminClient.create(config)) {
                Map<String, String> configs = new HashMap<>();
                configs.put(TopicConfig.RETENTION_MS_CONFIG, kafkaConfig.getAdminRetentionConfig());
                configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, kafkaConfig.getAdminCleanupPolicy());
                CreateTopicsResult result = admin.createTopics(asList(new NewTopic(topicName, kafkaTopic.getPartitionCount(), (short) kafkaTopic.getReplicationFactor()).configs(configs)));
                for (Map.Entry<String, KafkaFuture<Void>> entry : result.values().entrySet()) {
                    /*
                    below is necessary to check if the topic is created.
                     */
                    entry.getValue().get();
                    if(entry.getKey().equalsIgnoreCase(topicName)){
                        updateVerifiedFlagKafkaTopic(topicName, kafkaTopic.getId());
                    }
                }
            } catch (InterruptedException | ExecutionException | KafkaException ex) {
                if (ex instanceof TopicExistsException) {
                    logger.info("topic already exist {}", topicName);
                }else if(ex instanceof  InterruptedException){
                    Thread.currentThread().interrupt();
                    logger.error("interrupt exception while creating topic {} at broker {}", topicName,kafkaConfig.getBootstrapServers());
                }else{
                    logger.error("error while creating topic {} at broker {}", topicName,kafkaConfig.getBootstrapServers());
                }
            }
        }
    }

    private static void updateVerifiedFlagKafkaTopic(String topicName, Long id) {
        try {
            IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
            int updateResult = kafkaService.updateVerifiedFlagByKafkaTopicId(id);
            if (updateResult > 0) {
                uniqueTopicNameMap.put(topicName, id);
            }
        } catch (BeansException| IllegalArgumentException| IllegalStateException ex) {
            logger.error("exception while updating the kafka verified flag for topic name {}",topicName, ex);
        }
    }
    private static final void setAdminConfigProperties(KafkaConfig kafkaConfig) {
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getBootstrapServers());
        String jaasCfg = String.format(kafkaConfig.getJaasTemplate(), kafkaConfig.getJaasConfigUsername(), kafkaConfig.getJaasConfigPassword());
        config.put(AdminClientConfig.SECURITY_PROTOCOL_CONFIG, kafkaConfig.getSecurityProtocol());
        config.put("sasl.mechanism", kafkaConfig.getSaslMechanism());
        config.put("sasl.jaas.config", jaasCfg);
    }

    //return the if of the kafka topic in db
    public static synchronized Long kafkaTopicExists(String topicName, Long id) {
        Long returnId = 0L;
        if (!oneTimeGetVerifiedTopicNames.get()) {
            uniqueTopicNameMap = new ConcurrentHashMap<>();
            uniqueTopicNameMap = getVerifiedTopics();
            oneTimeGetVerifiedTopicNames.set(true);
        }
        // check in cache
        if (uniqueTopicNameMap != null) {

            for (Map.Entry<String, Long> entry : uniqueTopicNameMap.entrySet()) {
                String topicVerified = entry.getKey();
                if (topicName.equalsIgnoreCase(topicVerified)) {
                    return entry.getValue();
                }
            }
        }
        // If the below condition is reached which means the kafkatopic is not found in the uniqueTopicNameMap and needs to be validated on the server.
        if (checkIfKafkaTopicExistsOnServer(topicName, id)) {
            return id;
        }
        return returnId;
    }

    private static synchronized boolean checkIfKafkaTopicExistsOnServer(String topicName, Long id) {
        boolean bKafkaTopicExists = false;
        IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
        KafkaConfig kafkaConfig = kafkaService.getConfigurations();
        setAdminConfigProperties(kafkaConfig);
        try (AdminClient client = AdminClient.create(config)) {
            ListTopicsOptions options = new ListTopicsOptions();
            options.listInternal(true); // includes internal topics such as __consumer_offsets
            ListTopicsResult topics = client.listTopics(options);
            Set<String> currentTopicList = topics.names().get();
            bKafkaTopicExists = currentTopicList.contains(topicName);
            if (bKafkaTopicExists) {
                int result = kafkaService.updateVerifiedFlagByKafkaTopicId(id);
                if (result > 0) {
                    uniqueTopicNameMap.put(topicName, id);
                }
            }
        } catch (InterruptedException e) {
            logger.error("interrupted while checking kafka topic on the server for {}",topicName, e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            logger.error("execution while checking kafka topic on the server for {}",topicName, e);
        } catch (TopicExistsException e) {
            if (Throwables.getRootCause(e) instanceof TopicExistsException) {
                logger.info("topic already {} existed", topicName);
            }
        } catch (Exception e) {
            logger.error("exception while checking kafka topic on the server for {}",topicName, e);
        }
        return bKafkaTopicExists;
    }

    private static Map<String,Long> getVerifiedTopics() {
        IKafkaService kafkaService = EMRAppContext.getEMRContext().getBean(KafkaService.class);
       return kafkaService.getVerifiedKafkaTopicMap();
    }

    public static Map<String, Long> getUniqueTopicNameMap() {
        return uniqueTopicNameMap;
    }
}
