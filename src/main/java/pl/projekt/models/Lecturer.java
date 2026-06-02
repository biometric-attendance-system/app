package pl.projekt.models;

public class Lecturer{
    private String ID;
    private String firstName;
    private String lastName;
    private String pinHash;

    public Lecturer(String ID, String firstName, String lastName, String pinHash){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = pinHash;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setID(String ID) {this.ID = ID;}
    public void setPinHash(String pinHash) {this.pinHash = pinHash;}


    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getID(){return ID;}
    public String getPinHash(){return pinHash;}
}