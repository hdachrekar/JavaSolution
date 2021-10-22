/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.client;

import catalog.Root;
import com.ecw.encryption.Aes128Kafka;
import com.ecw.healow.rpm.helper.RPMHelper;
import com.ecw.healow.rpm.util.EMRHealowRequestActions;
import com.google.common.base.Strings;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import empi.EMRServerDetailsBean;
import ereferralemr.NHXSyncClient;
import ereferralemr.Util;
import ereferralemr.jobs.SyncAppointments;
import ereferralemr.jobs.SyncSchedules;
import ereferralemr.kafka.model.Payload;
import ereferralemr.kafka.model.TopicResponse;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;

public class KafkaConsumerThreadHandler implements Runnable {
    private ConsumerRecord consumerRecord;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerThreadHandler.class);
    private String strIsCurrentTomcatIsJOBServer = null;

    public KafkaConsumerThreadHandler(ConsumerRecord consumerRecord) {
        this.consumerRecord = consumerRecord;
    }

    @Override
    public void run() {
        if(Strings.isNullOrEmpty(strIsCurrentTomcatIsJOBServer)){
            initJobServerCheck();
        }
        processConsumerRecord(consumerRecord);
    }

    private void initJobServerCheck() {
        Root oRoot = null;
        try {
            oRoot = Root.createDbConnection(null);
            boolean bIsCurrentTomcatIsJOBServer = EMRServerDetailsBean.checkLocalServerIsJobServer(oRoot);
            strIsCurrentTomcatIsJOBServer = bIsCurrentTomcatIsJOBServer ? "true" : "false";
            LOGGER.debug("Is current tomcat is job server: " + strIsCurrentTomcatIsJOBServer);
        } catch (Exception ex){
            LOGGER.error(ex.getMessage(), ex);
        } finally {
            Root.closeDbConnection(null,oRoot);
        }
    }

    private void processConsumerRecord(ConsumerRecord consumerRecord) {
        try {
            if (KafkaAdminUtilityClient.getUniqueTopicNameMap().containsKey(consumerRecord.topic())){
                String decryptionKey = (String)consumerRecord.key();
                String decryptedPayload= new Aes128Kafka().decrypt(consumerRecord.value().toString(),decryptionKey.getBytes("UTF-8"));
                Payload payload = new GsonBuilder().disableHtmlEscaping().create().fromJson(decryptedPayload, Payload.class);
                String topicType = payload.getTopicType();
                LOGGER.debug("Healow kafka consumer topicType: "+topicType);
                LOGGER.debug("Healow kafka consumer topicData: "+new Aes128Kafka().encrypt(payload.getTopicData()));
                if("true".equalsIgnoreCase(strIsCurrentTomcatIsJOBServer)){
                    processJobServerTopics(payload, topicType);
                }
                /**
                 * For single server tomcats we need to process RPM topics as well.
                 * So thats why below conditional should not be part of else clause.
                 */
                if(TopicResponse.RPM_EMR_RESPONSE.getTopicType().equalsIgnoreCase(topicType)){
                    EMRHealowRequestActions.processHealowResponse(payload.getTopicData());
                }
            }
        } catch (JsonSyntaxException | UnsupportedEncodingException | IllegalArgumentException  ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

    /*
     * With PE-150502 we are running consumer on all app servers, so topic response which are supposed to run on job server
     * add them here to avoid duplicate data processing.
     */
    private void processJobServerTopics(Payload payload, String topicType) {
        if(TopicResponse.HEALOW_APPOINTMENT_REQUEST.getTopicType().equalsIgnoreCase(topicType)){
            new SyncSchedules().processScheduleRequestForOpenAccess(payload.getTopicData());
        } else if (TopicResponse.OA_BOOKING_REQUEST.getTopicType().equalsIgnoreCase(topicType)){
            String emrResponse = new SyncAppointments().processAppointmentRequestFromOpenAccess(payload.getTopicData());
            if(!Strings.isNullOrEmpty(emrResponse)) {
                NHXSyncClient nhxSyncClient   = new NHXSyncClient();
                nhxSyncClient.addParam("operation","processemrscheduleapptresponsetoopenaccess");
                nhxSyncClient.addParam("resp",emrResponse);
                nhxSyncClient.post(Util.getNHXProtocol() + Util.getNHXServer() + "/mobiledoc/Controller");
            }
        } else if (TopicResponse.RPM_SYNC_TRACKER_DATA.getTopicType().equalsIgnoreCase(topicType)){
            new RPMHelper().processPatientTrackerData(payload.getTopicData());
        } else if (TopicResponse.RPM_SYNC_META_DATA.getTopicType().equalsIgnoreCase(topicType)){
            new RPMHelper().processVendorTrackersMetaData(payload.getTopicData(), true);
        } else if(TopicResponse.RPM_PATIENT_UNLINK_DEVICE_NOTIFICATION.getTopicType().equalsIgnoreCase(topicType)) {
            new RPMHelper().processPatientVendorUnlinkingNotifications(payload.getTopicData());
        }
    }

}

