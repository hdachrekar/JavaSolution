/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.config;
import com.ecw.dao.ECWDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "ereferralemr.kafka")
public class DataSourceConfigAdapter {
    @Bean
    public ECWDataSource ecwDataSource() {
        return new ECWDataSource();
    }

}
