package pl.projekt.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pl.projekt.models.Lecturer;


public class LecturerService{
    private final String URL = "jdbc:sqlite:Lecturer_db.db";

    public LecturerService(){
        createTable();
    }

    public void deleteLecturer(String ID){
        String request = "DELETE FROM Lecturers WHERE ID = ?;";
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, ID);

            myStatement.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Boolean isLecturerInTable(String ID){
        String request = "SELECT 1 FROM Lecturers WHERE ID=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, ID);
            ResultSet answer = myStatement.executeQuery();

            return answer.next();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public void addLecturer(Lecturer lecturer){
        String request = "INSERT INTO Lecturers(firstName, lastName, ID, biometricData, pinHash) Values(?,?,?,?,?);";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, lecturer.getFirstName());
            myStatement.setString(2, lecturer.getLastName());
            myStatement.setString(3, lecturer.getID());
            myStatement.setString(4, lecturer.getBiometricData());
            myStatement.setString(5, lecturer.getPinHash());

            myStatement.executeUpdate();
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
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

    public void setLecturer(Lecturer lecturer){
        String request = "UPDATE Lecturers SET firstName = ?, lastName = ?, biometricData = ?, pinHash = ? WHERE ID = ?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, lecturer.getFirstName());
            myStatement.setString(2, lecturer.getLastName());
            myStatement.setString(3, lecturer.getBiometricData());
            myStatement.setString(4, lecturer.getPinHash());
            myStatement.setString(5, lecturer.getID());

            myStatement.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()){
            String request = "CREATE TABLE IF NOT EXISTS Lecturers(ID TEXT PRIMARY KEY, firstName TEXT, lastName TEXT, biometricData TEXT, pinHash TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}