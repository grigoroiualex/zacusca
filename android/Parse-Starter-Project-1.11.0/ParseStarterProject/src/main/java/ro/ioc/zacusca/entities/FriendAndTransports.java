package ro.ioc.zacusca.entities;

import java.util.ArrayList;

public class FriendAndTransports {
    private String username;
    private ArrayList<Transport> transports;

    public FriendAndTransports(String username) {
        this.username = username;
        transports = new ArrayList<Transport>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<Transport> getTransports() {
        return transports;
    }

    public void setTransports(ArrayList<Transport> transports) {
        this.transports = transports;
    }

    public void addTransport(Transport transport) {
        transports.add(transport);
    }
}
