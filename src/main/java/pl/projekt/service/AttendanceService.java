package pl.projekt.service;
import java.util.ArrayList;
import pl.projekt.models.Attendance;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.models.Student;
import pl.projekt.service.StudentService;

public class AttendanceService{
    
    private final AttendanceRepository repository = new AttendanceRepository();
    private final StudentService studentService = new StudentService();

    public void addAttendances(String[] albumNumbers, String date, String time, String status){
        for (String albumNumber : albumNumbers){
            if (albumNumber.equals("Unknown")) continue;
            if (studentService.getStudent(albumNumber) == null) continue;
            if (addAttendance(new Attendance(albumNumber,date,time,status))){
                System.out.println("Added student: " + albumNumber);
            } 
        }
    }

    public void fillAbsentByDate(String date, String time){
        ArrayList<Attendance> att = getAttendanceByDate(date);
        ArrayList<Student> std = studentService.getStudents();
        boolean flag;

        if (std == null) return;

        if (att == null){
            for (var stud : std){
                addAttendance(new Attendance(stud.getAlbumNumber(), date, time, "absent"));
            }
        } else {
            for (var stud : std){
                flag = true;
                for (var attendance : att){
                    if (stud.getAlbumNumber().equals(attendance.getAlbumNumber())) flag = false;
                }
                if (flag) addAttendance(new Attendance(stud.getAlbumNumber(), date, time, "absent"));
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
            System.out.println("Changed status: " + attendance.getAlbumNumber());
            return true;
        }
        else if (status == null){
            repository.addAttendance(attendance);
            System.out.println("Added student: " + attendance.getAlbumNumber());
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