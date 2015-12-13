package ro.ioc.zacusca.entities;

import java.util.Date;

public class Package {
    private String source;
    private String destination;
    private String name;
    private Date date;

    public Package(String source, String destination, String name, Date date) {
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.date = date;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
