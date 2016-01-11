package ro.ioc.zacusca.entities;

import java.util.Date;

public class Package {
    private String source;
    private String destination;
    private String name;
    private String date;
    private String state;
    private String objectId;

    public Package(String source, String destination, String name, String date, String state, String objectId) {
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.date = date;
        this.state = state;
        this.objectId = objectId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getSource() {
        return source;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
