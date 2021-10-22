/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.model;
import ereferralemr.Util;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KafkaTopicMapExtractor implements ResultSetExtractor<Map<Integer, List<KafkaTopic>>> {

    private String guid;

    public KafkaTopicMapExtractor(String guid){
        this.guid=guid;
    }

    @Override
    public Map<Integer, List<KafkaTopic>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, List<KafkaTopic>> kafkaTopicMap = new HashMap<>();
        while (rs.next()) {
            Long id = rs.getLong("id");
            Integer partition = rs.getInt("partitionId");
            int isConsumer = rs.getInt("is_consumer");
            String topic = "";
            if (isConsumer == 1) {
                topic = Util.getAPUID().concat("_").concat(guid).concat(rs.getString("topic"));
            } else {
                topic = rs.getString("topic");
            }
            int partitionCount = rs.getInt("partition_count");
            int replicationCount = rs.getInt("replication_factor");
            int verified = rs.getInt("verified");
            KafkaTopic kafkaTopic = new KafkaTopic();
            kafkaTopic.setId(id);
            kafkaTopic.setTopicName(topic);
            kafkaTopic.setPartitionCount(partitionCount);
            kafkaTopic.setReplicationFactor(replicationCount);
            kafkaTopic.setVerified(verified);
            List<KafkaTopic> kafkaTopics = kafkaTopicMap.get(partition);
            if (kafkaTopics == null) {
                kafkaTopics = new ArrayList<>();
                kafkaTopics.add(kafkaTopic);
                kafkaTopicMap.put(partition, kafkaTopics);
            } else {
                kafkaTopics.add(kafkaTopic);
            }
        }
        return kafkaTopicMap;
    }
}
