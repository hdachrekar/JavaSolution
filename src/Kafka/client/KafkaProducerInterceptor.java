/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.client;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class KafkaProducerInterceptor implements ProducerInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerInterceptor.class);
    @Override
    public void configure(Map<String, ?> configs) {
    }

    @Override
    public void close() {
    }

    @Override
    public void onAcknowledgement(final RecordMetadata metadata, final Exception exception) {
        if (exception instanceof UnsupportedOperationException){
            LOGGER.error("error while updating metadata", exception);
        }
    }

    @Override
    public ProducerRecord onSend(final ProducerRecord record) {
        return record;
    }

}
