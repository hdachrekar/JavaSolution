package ereferralemr.kafka.model;

import java.util.ArrayList;
import java.util.List;

public class KafkaObject {
    private List<Observer> observers = new ArrayList<Observer>();
    private String request;
    private String response;

    public KafkaObject(String request){
        this.request=request;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
        notifyAllObservers();
    }

    public void attach(Observer observer){
        observers.add(observer);
    }

    public void notifyAllObservers(){
        for (Observer observer : observers) {
            observer.updateKafkaResponse();
        }
    }
}