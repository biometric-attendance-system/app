package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pl.projekt.models.Student;

/**
 * @brief Class responsible for connection with Students table in Student database.
 */
public class StudentRepository{

    private final String URL;

    /**
     * @brief Constructor invokes createTable method to create Students table.
     */
    public StudentRepository() {
        this.URL = "jdbc:sqlite:Student.db";
        createTable();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public StudentRepository(String url) {
        this.URL = url;
        createTable();
    }

    /**
     * @brief Clears whole table.
     */
    public void clear(){
        try (Connection myConnection = DriverManager.getConnection(URL);
             Statement myStatement = myConnection.createStatement()) {
            String request = "DELETE FROM Students;";

            myStatement.execute(request);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * @brief Function adds student into Students table.
     *
     * @param student Given student.
     * @return True if added, false otherwise.
     */
    public boolean addStudent(Student student){
        String request = "INSERT OR IGNORE INTO Students(firstName, lastName, albumNumber) Values(?,?,?);";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, student.getFirstName());
            myStatement.setString(2, student.getLastName());
            myStatement.setString(3, student.getAlbumNumber());

            int rowsAffected = myStatement.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * @brief Function deletes student from Students table.
     *
     * @param albumNumber Student's album number.
     * @return True if deleted, false otherwise.
     */
    public boolean deleteStudent(String albumNumber){
        String request = "DELETE FROM Students WHERE albumNumber = ?;";
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, albumNumber);

            int rowsAffected = myStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * @brief Function returns all students form the database.
     *
     * @return ArrayList of students.
     */
    public ArrayList<Student> getStudents(){
        String request = "SELECT * FROM Students;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            ResultSet ans = myStatement.executeQuery();
            ArrayList<Student> studentList = new ArrayList<>();

            while (ans.next()){ 
                studentList.add(new Student(ans.getString("firstName"), ans.getString("lastName"),
                              ans.getString("albumNumber")));
            }

            return studentList;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @brief Function creates table Students.
     */
    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()){
            String request = "CREATE TABLE IF NOT EXISTS Students(albumNumber TEXT PRIMARY KEY,firstName TEXT,lastName TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}