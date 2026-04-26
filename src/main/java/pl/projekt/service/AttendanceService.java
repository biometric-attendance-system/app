package pl.projekt.service;
import java.time.LocalDate;
import pl.projekt.models.Attendance;
import pl.projekt.repository.AttendanceRepository;

public class AttendanceService{
    private AttendanceRepository repository;

    public AttendanceService(AttendanceRepository repository){
        this.repository = repository;
    }

    public void addPresence(String albumNumber){
        LocalDate today = LocalDate.now();

        if (repository.isPresent(albumNumber, today.toString()))
            return;
        
        repository.addAttendance(new Attendance(albumNumber, today.toString(), "present"));

    }

    public void addAbsence(String albumNumber){
        LocalDate today = LocalDate.now();

        if (repository.isPresent(albumNumber, today.toString()))
            return;

        repository.addAttendance(new Attendance(albumNumber, today.toString(), "absent"));

    }



}