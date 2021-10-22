package ereferralemr.kafka.model;

import java.util.concurrent.ConcurrentHashMap;

public class KafkaObjectManager {

    private static ConcurrentHashMap<String, KafkaObject> kafkaObjectHashMap=new ConcurrentHashMap<>();

    public static KafkaObject getKafkaObject(String guid) {
        return kafkaObjectHashMap.get(guid);
    }

    public static void setKafkaObject(String guid, KafkaObject kafkaObject) {
        synchronized (kafkaObjectHashMap){
            kafkaObjectHashMap.put(guid,kafkaObject);
        }
    }
    public static void removeKafkaObject(String guid) {
        if(kafkaObjectHashMap.containsKey(guid)){
            kafkaObjectHashMap.remove(guid);
        }
    }
}