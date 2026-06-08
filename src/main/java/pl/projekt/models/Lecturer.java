package pl.projekt.models;

public class Lecturer{
    private String ID;
    private String firstName;
    private String lastName;
    private String passwordHash;

    public Lecturer(String ID, String firstName, String lastName, String passwordHash){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.passwordHash = passwordHash;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setID(String ID) {this.ID = ID;}
    public void setPasswordHash(String passwordHash) {this.passwordHash = passwordHash;}


    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getID(){return ID;}
    public String getPasswordHash(){return passwordHash;}
}