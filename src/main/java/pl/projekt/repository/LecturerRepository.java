package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pl.projekt.models.Lecturer;


public class LecturerRepository{
    private final String URL = "jdbc:sqlite:Lecturer_db.db";

    public LecturerRepository(){
        createTable();
    }

    public boolean deleteLecturer(String ID){
        String request = "DELETE FROM Lecturers WHERE ID = ?;";
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, ID);

            int rowsAffected = myStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public boolean addLecturer(Lecturer lecturer){
        String request = "INSERT OR IGNORE INTO Lecturers(firstName, lastName, ID, biometricData, pinHash) Values(?,?,?,?,?);";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, lecturer.getFirstName());
            myStatement.setString(2, lecturer.getLastName());
            myStatement.setString(3, lecturer.getID());
            myStatement.setString(4, lecturer.getBiometricData());
            myStatement.setString(5, lecturer.getPinHash());

            int rowsAffected = myStatement.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    public Lecturer getLecturer(String ID){
        String request = "SELECT * FROM Lecturers WHERE ID=?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, ID);

            ResultSet ans = myStatement.executeQuery();
            
            if (ans.next()){ 
                return new Lecturer(ans.getString("ID"), ans.getString("firstName"),
                              ans.getString("lastName"), ans.getString("biometricData") ,ans.getString("pinHash"));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean setLecturer(Lecturer lecturer){
        String request = "UPDATE Lecturers SET firstName = ?, lastName = ?, biometricData = ?, pinHash = ? WHERE ID = ?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, lecturer.getFirstName());
            myStatement.setString(2, lecturer.getLastName());
            myStatement.setString(3, lecturer.getBiometricData());
            myStatement.setString(4, lecturer.getPinHash());
            myStatement.setString(5, lecturer.getID());

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
            String request = "CREATE TABLE IF NOT EXISTS Lecturers(ID TEXT PRIMARY KEY,firstName TEXT,lastName TEXT,biometricData TEXT, pinHash TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}