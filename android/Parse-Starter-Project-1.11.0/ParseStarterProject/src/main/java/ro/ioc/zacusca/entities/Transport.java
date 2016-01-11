package ro.ioc.zacusca.entities;

public class Transport {
    private String source;
    private String destination;
    private String date;
    private Integer slotsAvailable;
    private String objectId;

    public Transport(String source, String destination, String date, Integer slotsAvailable, String objectId) {
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.slotsAvailable = slotsAvailable;
        this.objectId = objectId;
    }

    public String getSource() {
        return source;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getSlotsAvailable() {
        return slotsAvailable;
    }

    public void setSlotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }
}
