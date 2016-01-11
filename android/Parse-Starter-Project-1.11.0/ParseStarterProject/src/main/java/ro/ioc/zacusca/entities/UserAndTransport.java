package ro.ioc.zacusca.entities;

import java.util.ArrayList;

public class UserAndTransport {
    private String username;
    private String email;
    private String phone;
    private String source;
    private String destination;
    private String date;
    private Integer slotsAvailable;
    private ArrayList<String> acceptedPackages;
    private ArrayList<String> pendingPackages;
    private String packageId;
    private String transportId;

    public UserAndTransport(String packageId, String transportId, String username, String email, String phone, String source, String destination, String date) {
        this.packageId = packageId;
        this.transportId = transportId;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.source = source;
        this.destination = destination;
        this.date = date;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public Integer getSlotsAvailable() {
        return slotsAvailable;
    }

    public void setSlotsAvailable(Integer slotsAvailable) {
        this.slotsAvailable = slotsAvailable;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArrayList<String> getAcceptedPackages() {
        return acceptedPackages;
    }

    public void setAcceptedPackages(ArrayList<String> acceptedPackages) {
        this.acceptedPackages = acceptedPackages;
    }

    public ArrayList<String> getPendingPackages() {
        return pendingPackages;
    }

    public void setPendingPackages(ArrayList<String> pendingPackages) {
        this.pendingPackages = pendingPackages;
    }
}
