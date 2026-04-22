package pl.projekt.models;

public class Student{
    
    private String albumNumber;
    private String firstName;
    private String lastName;
    private String biometricData;

    public Student(String firstName, String lastName, String albumNumber, String biometricData){
        this.firstName = firstName;
        this.lastName = lastName;
        this.albumNumber = albumNumber;
        this.biometricData = biometricData;
    };

    public Student(String firstName, String lastName, String albumNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.albumNumber = albumNumber;
    };

    public Student(){
        this.firstName = "";
        this.lastName = "";
        this.albumNumber = "";
        this.biometricData = "";
    };

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setAlbumNumber(String albumNumber) {this.albumNumber = albumNumber;}
    public void setBiometricData(String biometricData) {this.biometricData = biometricData;}

    public String getFirstName(){return firstName;}
    public String getLastName(){return lastName;}
    public String getAlbumNumber(){return albumNumber;}
    public String getBiometricData(){return biometricData;}
}
