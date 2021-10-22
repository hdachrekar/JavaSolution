/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.service;

import ereferralemr.kafka.model.KafkaConfig;
import ereferralemr.kafka.model.KafkaProducerParams;
import ereferralemr.kafka.model.KafkaTopic;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("KafkaService")
public interface IKafkaService {
    KafkaConfig getConfigurations();
    void setupKafkaService();
    void toggleKafkaConsumer(int enableKafkaPolling);
    Map<Integer, List<KafkaTopic>> getPerPartitionKafkaTopicList(int isConsumer, int isProducer, String guid);
    String publish(KafkaProducerParams kafkaProducerParams);
    void publish(String data, String topicName,String topicType);
    Map<String,Long> getVerifiedKafkaTopicMap();
    int updateVerifiedFlagByKafkaTopicId(Long id);
    void updateKafkaConsumerServerConfig(String bootstrapServer, String jaasUsername, String jaasPassword, String kafkaGuid);
    void toggleConsumerOnAPPServers();
}
