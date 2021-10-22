package ereferralemr.kafka.client;

import catalog.EcwCacheManager;
import catalog.Root;
import empi.EMRServerDetailsBean;
import ereferralemr.Util;
import ereferralemr.kafka.config.EMRAppContext;
import ereferralemr.kafka.service.IKafkaService;
import ereferralemr.kafka.service.KafkaService;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class KafkaConsumerExecutor {

    private final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerExecutor.class);

    @Autowired
    EMRAppContext emrAppContext;

    @PostConstruct
    public void init() {
        Root oRoot = null;
        try{
            oRoot = Root.createDbConnection(null);
            boolean bIsCurrentTomcatIsJOBServer = EMRServerDetailsBean.checkLocalServerIsJobServer(oRoot);
            boolean bRPMEnabled = "yes".equalsIgnoreCase(EcwCacheManager.getInstance().getItemKeyValue("EnableRPM"));
            boolean bKafkaEnabled = "true".equalsIgnoreCase(Util.getNHXKafkaPollenabled());
            if(!bIsCurrentTomcatIsJOBServer && bKafkaEnabled && bRPMEnabled) {
                IKafkaService kafkaService = emrAppContext.getEMRContext().getBean(KafkaService.class);
                kafkaService.setupKafkaService();
            }
        } catch(KafkaException | BeansException | IllegalArgumentException ex){
            LOGGER.error("Failed to start the consumers on PostConstruct",ex);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
        } finally {
            Root.closeDbConnection(null,oRoot);
        }
    }
}
