package pl.projekt.service;
import java.util.ArrayList;
import pl.projekt.models.Attendance;
import pl.projekt.repository.AttendanceRepository;

public class AttendanceService{
    
    private final AttendanceRepository repository;

    public AttendanceService(){
        repository = new AttendanceRepository();
    }

    public AttendanceService(AttendanceRepository repository){
        this.repository = repository;
    }

    public void addAttendances(String[] albumNumbers, String date, String status){
        for (String albumNumber : albumNumbers){
            if (albumNumber.equals("Unknown")) continue;
            if (addAttendance(new Attendance(albumNumber,date,status))){
                System.out.println("Added student: " + albumNumber);
            } 
        }
    }

    public boolean addAttendance(Attendance attendance){
        String status = repository.getStatus(attendance.getAlbumNumber(), attendance.getDate());
        if ("present".equals(status)){
            return false;
        }
        else if ("absent".equals(status)) {
            repository.setStatus(attendance);
            return true;
        }
        else if (status == null){
            repository.addAttendance(attendance);
            return true;
        }
        
        return false;
    }

    public ArrayList<Attendance> getAttendances(){
        return repository.getAttendances();
    }

    public ArrayList<Attendance> getAttendanceByDate(String date){
        return repository.getAttendanceByDate(date);
    }

    public int countAttendance(String albumNumber){
        return repository.countAttendance(albumNumber);
    }

    public int countPresent(String albumNumber){
        return repository.countPresent(albumNumber);
    }
}