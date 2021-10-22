package ereferralemr.kafka.model;

import ereferralemr.Util;

/**
 * @author varuna.naik
 */
public enum TopicResponse {

    HEALOW_APPOINTMENT_REQUEST (Util.getAPUID()+"_request","appointment_searches"),
    APPOINTMENT_SEARCHES ("healow_response","appointment_searches"),
    GROUP_VISIT_APPOINTMENTS ("healow_response","group_visit_appointments"),
    OA_BOOKING_REQUEST (Util.getAPUID()+"_request","appointment_booking"),
    RPM_SYNC_TRACKER_DATA ("_request","rpm_sync_tracker_data"),
    RPM_ACK_SYNC_TRACKER_RESPONSE("healow_response","rpm_ack_sync_tracker_response"),
    RPM_SYNC_META_DATA ("_request","rpm_sync_meta_data"),
    RPM_ACK_SYNC_META_DATA_RESPONSE ("healow_response","rpm_ack_sync_meta_data_response"),
    RPM_PATIENT_UNLINK_DEVICE_NOTIFICATION ("_request","rpm_patient_unlink_device_notification"),
    RPM_PATIENT_UNLINK_DEVICE_NOTIFICATION_RESPONSE ("healow_response","rpm_patient_unlink_device_notification_response"),
    RPM_EMR_REQUEST("healow_response","rpm_emr_request"),
    RPM_EMR_RESPONSE("_request","rpm_emr_response");

    private String topicName;
    private String topicType;

    TopicResponse(String topicName, String topicType) {
        this.topicName = topicName;
        this.topicType = topicType;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getTopicType() {
        return topicType;
    }
}
