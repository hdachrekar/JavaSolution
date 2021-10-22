/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.repository;

import com.ecw.dao.ECWDataSource;
import com.ecw.dao.exception.EcwDataSourceException;
import com.google.common.base.Strings;
import ereferralemr.Util;
import ereferralemr.kafka.model.KafkaProperty;
import ereferralemr.kafka.model.KafkaPropertyRowMapper;
import ereferralemr.kafka.model.KafkaTopic;
import ereferralemr.kafka.model.KafkaTopicMapExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository("KafkaRepository")
public class KafkaJdbcTemplate implements IKafkaRepository {
    private JdbcTemplate jdbcTemplateObject;
    private static final Logger logger = LoggerFactory.getLogger(KafkaJdbcTemplate.class);
    private static final String SELECT_KAFKA_CONFIGURATIONS = "SELECT config_key,config_value FROM kafka_configurations WHERE delflag=0";
    private static final String SELECT_KAFKA_TOPIC = "SELECT id,topic,partitionId,partition_count,replication_factor,verified,is_consumer,is_producer FROM kafka_topics WHERE is_consumer = ? AND is_producer = ? AND delflag=0";
    private static final String SELECT_KAFKA_TOPIC_VERIFIED = "SELECT id,topic,is_consumer,is_producer FROM kafka_topics WHERE verified = 1 and delflag=0";
    private static final String UPDATE_KAFKA_TOPIC_VERIFIED = "UPDATE kafka_topics SET verified=1 WHERE id=? ";
    private static final String UPDATE_CONSUMER_KAFKA_CONFIGURATION = "UPDATE kafka_configurations SET config_value= ? WHERE config_key =? AND delflag=0";

    @Autowired
    public void setDataSource(ECWDataSource ecwDataSource) throws EcwDataSourceException {
        this.jdbcTemplateObject = new JdbcTemplate(ecwDataSource.getDataSource());
    }

    @Override
    public List<KafkaProperty> getKafkaPropertyList() {
        List<KafkaProperty> kafkaPropertyList = null;
        try {
            kafkaPropertyList = jdbcTemplateObject.query(SELECT_KAFKA_CONFIGURATIONS, new KafkaPropertyRowMapper());
        } catch (DataAccessException | IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        }
        return kafkaPropertyList;
    }

    @Override
    public Map<Integer, List<KafkaTopic>> getByPartitionKafkaTopicList(int isConsumer, int isProducer,String guid) {
        Map<Integer, List<KafkaTopic>> kafkaTopicMap = null;
        try {
            kafkaTopicMap = jdbcTemplateObject.query(SELECT_KAFKA_TOPIC, new Object[]{isConsumer, isProducer}, new KafkaTopicMapExtractor(guid));
        } catch (DataAccessException | IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        }
        return kafkaTopicMap;
    }

    @Override
    public Map<String, Long> getVerifiedKafkaTopicMap() {
        Map<String, Long> kafkaTopicNamesMap = new HashMap<>();
        try {
            List<Map<String, Object>> rows = jdbcTemplateObject.queryForList(SELECT_KAFKA_TOPIC_VERIFIED);
            for (Map row : rows) {
                Long id = Long.valueOf(String.valueOf(row.get("id")));
                int isConsumer = Integer.parseInt(String.valueOf(row.get("is_consumer")));
                String topic =(String) row.get("topic");
                if (isConsumer == 1) {
                    topic = Util.getAPUID().concat((String) row.get("topic"));
                }
                kafkaTopicNamesMap.put(topic, id);
            }
        } catch (DataAccessException | IllegalArgumentException e) {
            logger.error(e.getMessage(), e);
        }
        return kafkaTopicNamesMap;
    }

    @Override
    public int updateVerifiedFlagByKafkaTopicId(Long id) {
        Object[] params = {id};
        int[] types = {Types.BIGINT};
        return jdbcTemplateObject.update(UPDATE_KAFKA_TOPIC_VERIFIED, params, types);
    }


    @Override
    public void updateKafkaConsumerServerConfig(final List<KafkaProperty>  kafkaProperties) {
        jdbcTemplateObject.batchUpdate(UPDATE_CONSUMER_KAFKA_CONFIGURATION,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        KafkaProperty property = kafkaProperties.get(i);
                        ps.setString(1, property.getValue().toString());
                        ps.setString(2, property.getKey());
                    }
                    @Override
                    public int getBatchSize() {
                        return kafkaProperties.size();
                    }
                });

    }

    @Override
    public Set<String> getAPPServersConsumerURLs() {
        String strQuery = "SELECT IPAddress, PortNo FROM serverDetails WHERE isDefaultJobServer=0";
        Set<String> urls = new HashSet<>();
        try {
            urls = jdbcTemplateObject.query(strQuery, oRs -> {
                String ipAddress = "";
                String portNo = "";
                StringBuilder appServerURL;
                Set<String> appServerUrls = new HashSet<>();
                while (oRs.next()) {
                    ipAddress = oRs.getString("IPAddress");
                    portNo = oRs.getString("PortNo");
                    if (!Strings.isNullOrEmpty(ipAddress) && !Strings.isNullOrEmpty(portNo)) {
                        appServerURL = new StringBuilder();
                        appServerURL.append("http://").append(ipAddress).append(":").append(portNo).append("/mobiledoc/jsp/campaign/toggleKafkaConsumer.jsp");
                        appServerUrls.add(appServerURL.toString());
                    }
                }
                return appServerUrls;
            });
        } catch (DataAccessException | IllegalArgumentException ex){
            logger.error(ex.getMessage(), ex);
        }
        return urls;
    }


}
