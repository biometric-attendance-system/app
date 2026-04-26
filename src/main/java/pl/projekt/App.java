package pl.projekt;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import pl.projekt.database.LecturerService;
import pl.projekt.database.StudentService;
import pl.projekt.models.Lecturer;
import pl.projekt.models.Student;
//import nu.pattern.OpenCV;


/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        stage.setTitle("Rejestrator obecnosci");
        var javaVersion = SystemInfo.javaVersion();
        var javafxVersion = SystemInfo.javafxVersion();

        var label = new Label("Hello, JavaFX " + javafxVersion + ", running on Java " + javaVersion + ".");
        var scene = new Scene(new StackPane(label), 640, 480);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //OpenCV.loadLocally();
        launch();
        StudentService StudServ = new StudentService();
        Student Stud = new Student();
        LecturerService LectServ = new LecturerService();
        Lecturer Lect = new Lecturer();
        

        /*
        Student stud = new Student("szym","k","23");
        s.addStudent(stud);
        System.out.println(s.isStudentInTable("1"));
        System.out.println(s.isStudentInTable("23"));
        s.deleteStudent("23");
        
        Lecturer lec2 = new Lecturer("1","Szymon","Bomba","pin","twqarz");
        serv.addLecturer(lec2);
        serv.addLecturer(lec1);
        Lecturer result = serv.getLecturer("1");
        System.out.println(result.getLastName());
        */
       
    }

}