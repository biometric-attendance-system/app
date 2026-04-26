package pl.projekt.repository;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import pl.projekt.models.Attendance;

public class AttendanceRepository{

    private final String URL = "jdbc:sqlite:Attendance_db.db";

    public AttendanceRepository() { createTable(); }

    public void addAttendance(Attendance attendance){
        String request = "INSERT INTO Attendance(albumNumber, date, status) Values(?,?,?);";

        try (Connection myConnection = DriverManager.getConnection(URL);
             PreparedStatement myStatement = myConnection.prepareStatement(request)) {

                myStatement.setString(1, attendance.getAlbumNumber());
                myStatement.setString(2, attendance.getDate());
                myStatement.setString(3, attendance.getStatus());

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Boolean isPresent(String albumNumber, String date){
        String request = "SELECT 1 FROM Attendance WHERE albumNumber=? AND date=?;";
        
        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            myStatement.setString(1, albumNumber);
            myStatement.setString(2, date);
            ResultSet answer = myStatement.executeQuery();

            return answer.next();

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return false;
    }

    public ArrayList<Attendance> getAttendance(){
        String request = "SELECT * FROM Attendance;";

        try (Connection myConnection = DriverManager.getConnection(URL);
            PreparedStatement myStatement = myConnection.prepareStatement(request)){
            
            ResultSet ans = myStatement.executeQuery();
            ArrayList<Attendance> attendanceList = new ArrayList<>();

            while (ans.next()){ 
                attendanceList.add(new Attendance(ans.getString("albumNumber"), ans.getString("date"), ans.getString("status")));
            }

            return attendanceList;

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

        return null;
    }

    private void createTable() {
        try (Connection myConnection = DriverManager.getConnection(URL);
            Statement myStatement = myConnection.createStatement()) {
            String request = "CREATE TABLE IF NOT EXISTS Attendance(albumNumber TEXT, date TEXT, status TEXT); ";
            
            myStatement.execute(request);
            
        } catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }
}