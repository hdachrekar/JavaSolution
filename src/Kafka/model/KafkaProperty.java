/*
 * @author Harshavardhan Achrekar
 *
 * */
package ereferralemr.kafka.model;
public class KafkaProperty {
    private String key;
    private Object value;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
    public void setValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

}
