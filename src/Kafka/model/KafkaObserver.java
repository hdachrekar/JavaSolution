package ereferralemr.kafka.model;

public class KafkaObserver extends Observer {

    private String response="";
    public KafkaObserver(KafkaObject kafkaObject){
        this.kafkaObject = kafkaObject;
        this.kafkaObject.attach(this);
    }

    public String getKafkaResponse(){
        return this.response;
    }

    @Override
    public void updateKafkaResponse() {
        this.response= this.kafkaObject.getResponse();
        synchronized (this.kafkaObject){
            this.kafkaObject.notifyAll();
        }
    }
}