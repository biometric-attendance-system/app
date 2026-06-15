package pl.projekt.service;
import java.util.ArrayList;
import pl.projekt.models.Attendance;
import pl.projekt.repository.AttendanceRepository;
import pl.projekt.models.Student;
import pl.projekt.service.StudentService;

/**
 * @brief Service responsible for attendance logic, directly communicates with AttendanceRepository.
 */
public class AttendanceService{

    private final AttendanceRepository repository;
    private final StudentService studentService;

    /**
     * @brief Constructor prepares repository and service.
     */
    public AttendanceService() {
        this.repository = new AttendanceRepository();
        this.studentService = new StudentService();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public AttendanceService(AttendanceRepository repository, StudentService studentService) {
        this.repository = repository;
        this.studentService = studentService;
    }

    /**
     * @brief Clears whole database.
     */
    public void clear(){
        repository.clear();
    }

    /**
     * @brief After successful recording at HomeController, function fills in absent students
     * (those who are in the student database but not present at given day in attendance database).
     *
     * @param date Given date YEAR:MONTH:DAY
     * @param time Given time HH:MM:SS
     */
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

    /**
     * @brief Function adds attendance to the database.
     * If student is already present it does not change status.
     * If student is already absent, it changes status to present.
     *
     * @param attendance Attendance object.
     */
    public void addAttendance(Attendance attendance){
        String status = repository.getStatus(attendance.getAlbumNumber(), attendance.getDate());
        if ("present".equals(status)){
            return;
        }
        else if ("absent".equals(status)) {
            repository.setStatus(attendance);
            System.out.println("Changed status: " + attendance.getAlbumNumber());
            return;
        }
        else if (status == null){
            repository.addAttendance(attendance);
            System.out.println("Added student: " + attendance.getAlbumNumber());
            return;
        }
        
        return;
    }

    /**
     * @brief Function filters attendances by given date.
     *
     * @param date Given date YEAR:MONTH:DAY.
     * @return ArrayList of every attendance at given day.
     */
    public ArrayList<Attendance> getAttendanceByDate(String date){
        return repository.getAttendanceByDate(date);
    }

    /**
     * @brief Counts number of attendances for a specific person by their album number.
     *
     * @param albumNumber Student's album number.
     * @return Number of attendances.
     */
    public int countAttendance(String albumNumber){
        return repository.countAttendance(albumNumber);
    }

    /**
     * @brief Counts number of days when student was present.
     *
     * @param albumNumber Student's album number.
     * @return Number of present days.
     */
    public int countPresent(String albumNumber){
        return repository.countPresent(albumNumber);
    }
}