package pl.projekt.models;

public class Lecturer{
    private String ID;
    private String firstName;
    private String lastName;
    private String pinHash;
    private String biometricData;

    public Lecturer(){
        this.ID = "";
        this.firstName = "";
        this.lastName = "";
        this.pinHash = "";
        this.biometricData = "";
    }

    public Lecturer(String ID, String firstName, String lastName){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = "";
        this.biometricData = "";
    }

    public Lecturer(String ID, String firstName, String pinHash, String lastName){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.biometricData = "";
        this.pinHash = pinHash;
   
    }

    public Lecturer(String ID, String firstName, String lastName, String pinHash, String biometricData){
        this.ID = ID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.pinHash = pinHash;
        this.biometricData = biometricData;
    }

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setID(String ID) {this.ID = ID;}
    public void setBiometricData(String biometricData) {this.biometricData = biometricData;}
    public void setPinHash(String pinHash) {this.pinHash = pinHash;}


    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getID(){return ID;}
    public String getBiometricData(){return biometricData;}
    public String getPinHash(){return pinHash;}
}