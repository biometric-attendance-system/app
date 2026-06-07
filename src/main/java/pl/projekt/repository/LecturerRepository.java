package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import pl.projekt.models.Lecturer;

/**
 * @brief Class responsible for connection with Lecturers table in Lecturer database.
 */
public class LecturerRepository{
    private final String URL = "jdbc:sqlite:Lecturer.db";

    /**
     * @brief Constructor invokes createTable method to create Lecturers table.
     */
    public LecturerRepository(){
        createTable();
    }

    /**
     * @brief Function adds lecturer into Lecturers table.
     *
     * @param lecturer Given lecturer.
     * @return True if added, false otherwise.
     */
    public boolean addLecturer(Lecturer lecturer){
        String request = "INSERT OR IGNORE INTO Lecturers(firstName, lastName, ID,  pinHash) Values(?,?,?,?);";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, lecturer.getFirstName());
            myStatement.setString(2, lecturer.getLastName());
            myStatement.setString(3, lecturer.getID());
            myStatement.setString(4, lecturer.getPinHash());

            int rowsAffected = myStatement.executeUpdate();
            
            return rowsAffected > 0;
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * @brief Function returns lecturer from Lecturers table.
     *
     * @return First Lecturer from table.
     */
    public Lecturer getLecturer(){
        String request = "SELECT * FROM Lecturers;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){

            ResultSet ans = myStatement.executeQuery();
            
            if (ans.next()){ 
                return new Lecturer(ans.getString("ID"), ans.getString("firstName"),
                              ans.getString("lastName") ,ans.getString("pinHash"));
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * @brief Function checks whether lecturer's database is empty or not.
     *
     * @return True if empty false otherwise.
     */
    public boolean isEmpty(){
        String request = "SELECT 1 FROM Lecturers;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            ResultSet ans = myStatement.executeQuery();
            
            return !ans.next(); 
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return false;
    }

    /**
     * @brief Function creates table Lecturers.
     */
    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()){
            String request = "CREATE TABLE IF NOT EXISTS Lecturers(ID TEXT PRIMARY KEY,firstName TEXT,lastName TEXT, pinHash TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}