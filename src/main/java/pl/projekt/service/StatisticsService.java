package pl.projekt.service;
import java.util.ArrayList;
import pl.projekt.models.Statistics;
import pl.projekt.models.Student;

/**
 * @brief Class responsible for creating statistics based on
 * information in both student and attendance databases.
 */
public class StatisticsService{
    
    private final StudentService studentService;
    private final AttendanceService attendanceService;

    /**
     * @brief Constructor initializing variables.
     */
    public StatisticsService(){
        studentService = new StudentService();
        attendanceService = new AttendanceService();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public StatisticsService(StudentService studentService, AttendanceService attendanceService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
    }

    /**
     * @brief Function calculates every student's
     * percentage of present days.
     *
     * @return ArrayList of Statistics.
     */
    public ArrayList<Statistics> calculateStatistics(){

        ArrayList<Student> studentList = studentService.getStudents();
        ArrayList<Statistics> statsList = new ArrayList<>();
        
        if (studentList.isEmpty()) return null;

        int present;
        double mean;
        int all;
    
        for (Student stud : studentList){
            present = attendanceService.countPresent(stud.getAlbumNumber());
            all = attendanceService.countAttendance(stud.getAlbumNumber());

            mean = (all > 0) ? (double) present / (double) all : 0;
            statsList.add(new Statistics(stud.getAlbumNumber(), stud.getFirstName(),
                                         stud.getLastName(), present, all, mean));
        }

        return statsList;
    }

}