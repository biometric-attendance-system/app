package pl.projekt.models;

public class Student{
    
    private String albumNumber;
    private String firstName;
    private String lastName;
    
    public Student(String firstName, String lastName, String albumNumber){
        this.firstName = firstName;
        this.lastName = lastName;
        this.albumNumber = albumNumber;
    };

    public Student(){
        this.firstName = "";
        this.lastName = "";
        this.albumNumber = "";
    };

    public void setFirstName(String firstName) {this.firstName = firstName;}
    public void setLastName(String lastName) {this.lastName = lastName;}
    public void setAlbumNumber(String albumNumber) {this.albumNumber = albumNumber;}

    public String getFirstName() {return firstName;}
    public String getLastName() {return lastName;}
    public String getAlbumNumber() {return albumNumber;}
}
