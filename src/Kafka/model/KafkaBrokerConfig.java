package ereferralemr.kafka.model;


import com.ecw.encryption.Aes128Kafka;

/**
 * @author varuna.naik on 7/6/2020
 */
public class KafkaBrokerConfig {

    private int brokerId;
    private String brokerName;
    private String brokerUrl;
    private String brokerUserName;
    private String brokerPassword;
    private int isConsumerScheduled;
    private int topicCapacity;
    private int topicCount;
    private int verified;

    public int getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(int brokerId) {
        this.brokerId = brokerId;
    }

    public String getBrokerName() {
        return brokerName;
    }

    public void setBrokerName(String brokerName) {
        this.brokerName = brokerName;
    }

    public String getBrokerUrl() {
        return brokerUrl;
    }

    public void setBrokerUrl(String brokerUrl) {
        this.brokerUrl = Aes128Kafka.decrypt(brokerUrl);
    }

    public String getBrokerUserName() {
        return brokerUserName;
    }

    public void setBrokerUserName(String brokerUserName) {
        this.brokerUserName = Aes128Kafka.decrypt(brokerUserName);
    }

    public String getBrokerPassword() {
        return brokerPassword;
    }

    public void setBrokerPassword(String brokerPassword) {
        this.brokerPassword = Aes128Kafka.decrypt(brokerPassword);
    }

    public int getIsConsumerScheduled() {
        return isConsumerScheduled;
    }

    public void setIsConsumerScheduled(int isConsumerScheduled) {
        this.isConsumerScheduled = isConsumerScheduled;
    }

    public int getTopicCapacity() {
        return topicCapacity;
    }

    public void setTopicCapacity(int topicCapacity) {
        this.topicCapacity = topicCapacity;
    }

    public int getTopicCount() {
        return topicCount;
    }

    public void setTopicCount(int topicCount) {
        this.topicCount = topicCount;
    }

    public int getVerified() {
        return verified;
    }

    public void setVerified(int verified) {
        this.verified = verified;
    }
}