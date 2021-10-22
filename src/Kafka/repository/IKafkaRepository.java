/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.repository;
import com.ecw.dao.ECWDataSource;
import com.ecw.dao.exception.EcwDataSourceException;
import ereferralemr.kafka.model.KafkaProperty;
import ereferralemr.kafka.model.KafkaTopic;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository("KafkaRepository")
public interface IKafkaRepository {
    void setDataSource(ECWDataSource ecwDataSource) throws EcwDataSourceException;
    List<KafkaProperty> getKafkaPropertyList();
    Map<Integer, List<KafkaTopic>> getByPartitionKafkaTopicList(int isConsumer, int isProducer,String guid);
    Map<String,Long> getVerifiedKafkaTopicMap();
    int updateVerifiedFlagByKafkaTopicId(Long id);
    void updateKafkaConsumerServerConfig(List<KafkaProperty> kafkaProperties);
    Set<String> getAPPServersConsumerURLs();
}
