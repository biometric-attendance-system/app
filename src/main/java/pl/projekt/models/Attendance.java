package pl.projekt.models;

public class Attendance{
    
    private final String albumNumber;
    private final String date;
    private final String status;

    public Attendance(String albumNumber, String date, String status){
        this.albumNumber = albumNumber;
        this.date = date;
        this.status = status;
    };

    public String getAlbumNumber() {return albumNumber;}
    public String getDate() {return date;}
    public String getStatus() {return status;}

}
