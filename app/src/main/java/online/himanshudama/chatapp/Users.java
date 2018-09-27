package online.himanshudama.chatapp;


public class Users {

    public String image, firstName, lastName, userName;

    public Users() {}

    public Users(String image, String firstName, String lastName, String userName ) {
        this.image = image;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
