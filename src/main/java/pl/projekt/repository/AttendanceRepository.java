package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import pl.projekt.models.Attendance;

/**
 * @brief Class responsible for connection with Attendance table in Attendance database.
 */
public class AttendanceRepository{

    private final String URL;

    /**
     * @brief Constructor invokes createTable method to create Attendance table.
     */
    public AttendanceRepository() {
        this.URL = "jdbc:sqlite:Attendance.db";
        createTable();
    }

    /**
     * @brief Constructor used for mock tests.
     */
    public AttendanceRepository(String url) {
        this.URL = url;
        createTable();
    }

    /**
     * @brief Clears whole table.
     */
    public void clear(){
        try (Connection myConnection = DriverManager.getConnection(URL);
             Statement myStatement = myConnection.createStatement()) {
            String request = "DELETE FROM Attendance;";

            myStatement.execute(request);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * @brief Counts number of days when student was present.
     *
     * @param albumNumber Student's album number.
     * @return Number of present days.
     */
    public int countPresent(String albumNumber){
         String request = "SELECT COUNT(*)  FROM Attendance WHERE albumNumber=? AND status=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
                
                myStatement.setString(1, albumNumber);
                myStatement.setString(2, "present");

                ResultSet answer = myStatement.executeQuery();
                if (answer.next()) return answer.getInt(1);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * @brief Counts number of attendances for a specific person by their album number.
     *
     * @param albumNumber Student's album number.
     * @return Number of attendances.
     */
    public int countAttendance(String albumNumber){
         String request = "SELECT COUNT(*)  FROM Attendance WHERE albumNumber=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
                
                myStatement.setString(1, albumNumber);

                ResultSet answer = myStatement.executeQuery();
                if (answer.next()) return answer.getInt(1);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    /**
     * @brief Function adds student's attendance if it does not already exist.
     *
     * @param attendance Attendance class object.
     */
    public void addAttendance(Attendance attendance){
        String request = "INSERT INTO Attendance(albumNumber, date, time, status) SELECT ?, ?, ?, ? " + 
                            "WHERE NOT EXISTS (SELECT 1 FROM Attendance WHERE albumNumber = ? AND date = ?);";

        try (Connection myConnection = DriverManager.getConnection(URL);
             PreparedStatement myStatement = myConnection.prepareStatement(request)) {

                myStatement.setString(1, attendance.getAlbumNumber());
                myStatement.setString(2, attendance.getDate());
                myStatement.setString(3, attendance.getTime());
                myStatement.setString(4, attendance.getStatus());

                myStatement.setString(5, attendance.getAlbumNumber());
                myStatement.setString(6, attendance.getDate());

                myStatement.executeUpdate();
                
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @brief Function sets student's attendance status and time.
     *
     * @param attendance Attendance class object.
     */
    public void setStatus(Attendance attendance){
        String request = "UPDATE Attendance SET status=?, time=? WHERE albumNumber=? AND date=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, attendance.getStatus());
            myStatement.setString(2, attendance.getTime());
            myStatement.setString(3, attendance.getAlbumNumber());
            myStatement.setString(4, attendance.getDate());
            
            myStatement.execute();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * @brief Function returns status of student's attendance on a given day.
     *
     * @param date Given date YEAR:MONTH:DAY.
     * @return Status of attendance.
     */
    public String getStatus(String albumNumber, String date){
        String request = "SELECT status FROM Attendance WHERE albumNumber=? AND date=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, albumNumber);
            myStatement.setString(2, date);
            ResultSet answer = myStatement.executeQuery();

            if (answer.next()) return answer.getString("status");

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * @brief Function filters attendances by given date.
     *
     * @param date Given date YEAR:MONTH:DAY.
     * @return ArrayList of every attendance at given day.
     */
    public ArrayList<Attendance> getAttendanceByDate(String date){
        String request = "SELECT * FROM Attendance WHERE date=?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, date);
            ResultSet ans = myStatement.executeQuery();
            ArrayList<Attendance> attendanceList = new ArrayList<>();

            while (ans.next()){ 
                attendanceList.add(new Attendance(ans.getString("albumNumber"), ans.getString("date"), ans.getString("time"), ans.getString("status")));
            }

            return attendanceList;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    /**
     * @brief Function deletes all student's attendances from the database.
     *
     * @param albumNumber Given student's album number.
     */
    public void deleteAttendances(String albumNumber) {
        String request = "DELETE FROM Attendance WHERE albumNumber = ?;";

        try (Connection myConnection = DriverManager.getConnection(URL);
             PreparedStatement myStatement = myConnection.prepareStatement(request)) {

            myStatement.setString(1, albumNumber);

            myStatement.executeUpdate();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * @brief Function creates table Attendance.
     */
    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()) {
            String request = "CREATE TABLE IF NOT EXISTS Attendance(albumNumber TEXT, date TEXT, time TEXT, status TEXT); ";

            myStatement.execute(request);

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}