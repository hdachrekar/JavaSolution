/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.model;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class KafkaPropertyRowMapper implements RowMapper<KafkaProperty> {
    @Override
    public KafkaProperty mapRow(ResultSet resultSet, int i) throws SQLException {
        KafkaProperty kafkaproperty = new KafkaProperty();
        kafkaproperty.setKey(resultSet.getString("config_key"));
        kafkaproperty.setValue(resultSet.getString("config_value"));
        return kafkaproperty;
    }
}
