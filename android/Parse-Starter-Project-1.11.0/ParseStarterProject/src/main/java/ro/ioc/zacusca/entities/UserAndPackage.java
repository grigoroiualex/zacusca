package ro.ioc.zacusca.entities;

public class UserAndPackage {
    private String objectId;
    private String name;
    private String source;
    private String destination;
    private String date;
    private String state;
    private String userFullName;
    private String userEmail;
    private String userPhone;

    public UserAndPackage(String objectId, String name, String source, String destination, String date, String state, String userFullName, String userEmail, String userPhone) {
        this.objectId = objectId;
        this.name = name;
        this.source = source;
        this.destination = destination;
        this.date = date;
        this.state = state;
        this.userFullName = userFullName;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullname(String userFullname) {
        this.userFullName = userFullname;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
}
