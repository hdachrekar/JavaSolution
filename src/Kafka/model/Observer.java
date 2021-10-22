package ereferralemr.kafka.model;

public abstract class Observer {
    protected KafkaObject kafkaObject;
    public abstract void updateKafkaResponse();
}