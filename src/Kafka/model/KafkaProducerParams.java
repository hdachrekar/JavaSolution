package ereferralemr.kafka.model;

import org.springframework.stereotype.Component;

/**
 * @author varuna.naik on 7/1/2020
 */
@Component
public class KafkaProducerParams {

    private int apuId;
    private int partition;
    private String messageId;
    private String topicName;
    private String topicNameGuid;
    private String topicType;
    private String topicData;
    private KafkaBrokerConfig kafkaBrokerConfig;

    public int getApuId() {
        return apuId;
    }

    public void setApuId(int apuId) {
        this.apuId = apuId;
    }

    public int getPartition() {
        return partition;
    }

    public void setPartition(int partition) {
        this.partition = partition;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getTopicData() {
        return topicData;
    }

    public void setTopicData(String topicData) {
        this.topicData = topicData;
    }

    public String getTopicNameGuid() {
        return topicNameGuid;
    }

    public void setTopicNameGuid(String topicNameGuid) {
        this.topicNameGuid = topicNameGuid;
    }

    public KafkaBrokerConfig getKafkaBrokerConfig() {
        return kafkaBrokerConfig;
    }

    public void setKafkaBrokerConfig(KafkaBrokerConfig kafkaBrokerConfig) {
        this.kafkaBrokerConfig = kafkaBrokerConfig;
    }
}