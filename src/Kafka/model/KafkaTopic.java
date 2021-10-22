/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.model;
public class KafkaTopic {

    private long id;
    private String topicName;
    private int partitionId;
    private int partitionCount;
    private int replicationFactor;
    private int verified;
    public long getId() {return id;}
    public void setId(long id) {this.id = id;}
    public int getVerified() {return verified; }
    public void setVerified(int verified) {  this.verified = verified;  }
    public String getTopicName() {
        return topicName;
    }
    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
    public int getPartitionId() {
        return partitionId;
    }
    public void setPartitionId(int partitionId) {
        this.partitionId = partitionId;
    }
    public int getPartitionCount() {
        return partitionCount;
    }
    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }
    public int getReplicationFactor() {
        return replicationFactor;
    }
    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }
}
