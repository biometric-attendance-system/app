package pl.projekt.service;
import java.util.ArrayList;
import pl.projekt.models.Statistics;
import pl.projekt.models.Student;

public class StatisticsService{
    
    private final StudentService studentService;
    private final AttendanceService attendanceService;

    public StatisticsService(){
        studentService = new StudentService();
        attendanceService = new AttendanceService();
    }

    public StatisticsService(StudentService studentService, AttendanceService attendanceService){
        this.attendanceService = attendanceService;
        this.studentService = studentService;
    }
    
    public ArrayList<Statistics> calculateStatistics(){

        ArrayList<Student> studentList = studentService.getStudents();
        ArrayList<Statistics> statsList = new ArrayList<>();
        
        if (studentList.isEmpty()) return null;

        int present;
        Double mean;
        int all = attendanceService.countAttendance(studentList.get(0).getAlbumNumber());
    
        for (Student stud : studentList){
            present = attendanceService.countPresent(stud.getAlbumNumber());
            mean = (all > 0) ? Double.valueOf(present)/Double.valueOf(all) : 0;
            statsList.add(new Statistics(stud.getAlbumNumber(), stud.getFirstName(),
                                         stud.getLastName(), present, all, mean));
        }

        return statsList;
    }

}