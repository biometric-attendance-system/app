package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pl.projekt.models.Student;

public class StudentRepository{
    private final String URL = "jdbc:sqlite:Student_db.db";
    
    public StudentRepository(){
        createTable();
    }

    public boolean addStudent(Student student){
        String request = "INSERT OR IGNORE INTO Students(firstName, lastName, albumNumber, biometricData) Values(?,?,?,?);";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, student.getFirstName());
            myStatement.setString(2, student.getLastName());
            myStatement.setString(3, student.getAlbumNumber());
            myStatement.setString(4, student.getBiometricData());

            int rowsAffected = myStatement.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

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


    public ArrayList<Student> getStudents(){
        String request = "SELECT * FROM Student;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            ResultSet ans = myStatement.executeQuery();
            ArrayList<Student> studentList = new ArrayList<>();

            while (ans.next()){ 
                studentList.add(new Student(ans.getString("firstName"), ans.getString("lastName"),
                              ans.getString("albumNumber"), ans.getString("biometricData")));
            }

            return studentList;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Student getStudent(String albumNumber){
        String request = "SELECT * FROM Students WHERE albumNumber=?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, albumNumber);

            ResultSet ans = myStatement.executeQuery();
            
            if (ans.next()){ 
                return new Student(ans.getString("firstName"), ans.getString("lastName"),
                              ans.getString("albumNumber"), ans.getString("biometricData"));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean setStudent(Student student){
        String request = "UPDATE Students SET firstName = ?, lastName = ?, biometricData = ? WHERE albumNumber = ?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, student.getFirstName());
            myStatement.setString(2, student.getLastName());
            myStatement.setString(3, student.getBiometricData());
            myStatement.setString(4, student.getAlbumNumber());

            int rowsAffected = myStatement.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()){
            String request = "CREATE TABLE IF NOT EXISTS Students(albumNumber TEXT PRIMARY KEY,firstName TEXT,lastName TEXT,biometricData TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}