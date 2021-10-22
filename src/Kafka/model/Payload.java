package ereferralemr.kafka.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class Payload {
    @SerializedName("topic_type")
    String topicType;
    @SerializedName("topic_data")
    String topicData;

    public Payload() {
    }

    public Payload(String topicType, String topicData) {
        this.topicType = topicType;
        this.topicData = topicData;
    }
    @Override
    public  String  toString() {
        return  new Gson().toJson(this);
    }

    public String getTopicType() {
        return topicType;
    }

    public void setTopicType(String topicType) {
        this.topicType = topicType;
    }

    public String getTopicData() {
        return topicData;
    }

    public void setTopicData(String topicData) {
        this.topicData = topicData;
    }
}
