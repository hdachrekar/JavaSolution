/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {"ereferralemr.kafka"})
public class EMRAppContext implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(EMRAppContext.class);

    @Override
    public void setApplicationContext(ApplicationContext appContext) {
        setEMRContext(appContext);
    }

    private static void setEMRContext(ApplicationContext appContext) {
       try {
           applicationContext = appContext;
       }catch(BeansException ex)
       {
           logger.error("Error while creating bean definition", ex);
       }
    }

    public static ApplicationContext getEMRContext()  {
        try {
            if (applicationContext == null) {
                applicationContext = new AnnotationConfigApplicationContext(DataSourceConfigAdapter.class);
            }
        } catch (BeansException ex) {
            logger.error("Error while creating bean definition", ex);
        }
        return applicationContext;
    }

    public static <T> T getBean(Class<T> beanClass) {
        try {
            if (applicationContext == null) {
                applicationContext = new AnnotationConfigApplicationContext(DataSourceConfigAdapter.class);
            }
        } catch (BeansException ex) {
            logger.error("Error while creating bean definition", ex);
        }
        return applicationContext.getBean(beanClass);
    }

}
