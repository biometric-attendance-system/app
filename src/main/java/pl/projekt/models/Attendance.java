package pl.projekt.models;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Attendance{
    
    private String albumNumber;
    private String date;
    private String status;

    public Attendance(String albumNumber){
        this.albumNumber = albumNumber;
        LocalDate date = LocalDate.now();
        this.date = date.toString();
        this.status = "";
    };

    public Attendance(String albumNumber, String date, String status){
        this.albumNumber = albumNumber;
        this.date = date;
        this.status = status;
    };

    public void setAlbumNumber(String albumNumber) {this.albumNumber = albumNumber;}
    public void setDate(String date) {this.date = date;}
    public void setStatus(String status) {this.status = status;}

    public String getAlbumNumber() {return albumNumber;}
    public String getDate() {return date;}
    public String getStatus() {return status;}

}
