package org.example.complete_ums;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import org.example.complete_ums.Databases.DatabaseConnection;
import org.example.complete_ums.Java_StyleSheet.Button3DEffect;
import org.example.complete_ums.ToolsClasses.LoadFrame;
import org.example.complete_ums.ToolsClasses.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class InsertMissingFieldsIntoSQLTable implements Initializable {
    Button3DEffect button3DEffect;
    LoadFrame loadFrame;
    SessionManager sessionManager = SessionManager.getInstance();
    @FXML
    private Button btnAttendance, btnFees, btnSalary, btnLibrary,
            btnCourses, btnEvent, btnExam, btnFeedback, btnHostel, btnLogs, btnTriggerAll;

    @FXML
    private Label errorMessageLabel;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        button3DEffect.applyEffect(btnAttendance, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnCourses, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnFeedback, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnFees, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnSalary, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnLibrary, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnHostel, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnEvent, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnExam, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnLogs, "/sounds/hover.mp3");
        button3DEffect.applyEffect(btnTriggerAll, "/sounds/hover.mp3");
        String role = sessionManager.getRole();
//        if (role.equals("Teacher")) {
//            btnAttendance.setDisable(false);
//            btnExam.setDisable(false);
//            btnCourses.setDisable(false);
//            btnEvent.setDisable(false);
//
//        }
//        if (role.equals("Accountant")) {
//            btnFees.setDisable(false);
//            btnHostel.setDisable(false);
//            btnEvent.setDisable(false);
//
//        }
//        if (role.equals("Librarian")) {
//            btnAttendance.setDisable(false);
//            btnCourses.setDisable(false);
//            btnEvent.setDisable(false);
//
//        }
//        if (role.equals("Admin")) {
//            btnAttendance.setDisable(false);
//            btnCourses.setDisable(false);
//            btnFees.setDisable(false);
//            btnSalary.setDisable(false);
//            btnLibrary.setDisable(false);
//            btnHostel.setDisable(false);
//            btnExam.setDisable(false);
//            btnCourses.setDisable(false);
//            btnLogs.setDisable(false);
//            btnEvent.setDisable(false);
//            btnTriggerAll.setDisable(false);
//        }



        btnAttendance.setDisable(false);
            btnCourses.setDisable(false);
            btnFees.setDisable(false);
            btnSalary.setDisable(false);
            btnLibrary.setDisable(false);
            btnHostel.setDisable(false);
            btnExam.setDisable(false);
            btnCourses.setDisable(false);
            btnLogs.setDisable(false);
            btnEvent.setDisable(false);
            btnTriggerAll.setDisable(false);

    }

    @FXML
    private void initializeAttendanceData() {
        String sql = """
                    INSERT INTO Attendances (User_Id, Attendance_Date, Status, Remarks)
                    SELECT u.User_Id, CURDATE(), 'Present', 'Auto Initialized'
                    FROM Users u
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Attendances a
                        WHERE a.User_Id = u.User_Id AND a.Attendance_Date = CURDATE()
                    );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Attendance initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeFeesData() {
        String sql = """
                    INSERT INTO Student_Fees (User_Id, Fee_Type_Id, Academic_Year, Due_Date, Amount_Due)
                    SELECT s.User_Id, ft.Fee_Type_Id, 2025, CURDATE(), ft.Default_Amount
                    FROM Students s
                    JOIN Fee_Types ft ON ft.Departments = s.Course
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Student_Fees sf
                        WHERE sf.User_Id = s.User_Id
                          AND sf.Fee_Type_Id = ft.Fee_Type_Id
                          AND sf.Academic_Year = 2025
                    );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Student fees initialized for " + rowsInserted + " records.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeSalaryData() {
        String sql = """
                    INSERT INTO Salary_Payments (User_Id, Salary_Month, Salary_Year, Gross_Amount)
                    SELECT u.User_Id, MONTH(CURDATE()), YEAR(CURDATE()), 0.00
                    FROM Users u
                    WHERE u.Role IN ('Teacher', 'Staff', 'Accountant', 'Admin', 'Librarian')
                      AND NOT EXISTS (
                          SELECT 1 FROM Salary_Payments sp
                          WHERE sp.User_Id = u.User_Id AND sp.Salary_Month = MONTH(CURDATE()) AND sp.Salary_Year = YEAR(CURDATE())
                      );
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Salary data initialized for " + rowsInserted + " users.", "GREEN");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeExamData() {
        String query = "INSERT INTO Exams (User_Id, Exam_Type, Subject, Marks_Obtained, Total_Marks, Exam_Date) " +
                "SELECT u.User_Id, 'Mid-Term', 'General Awareness', 0, 100, CURDATE() " +
                "FROM Users u " +
                "WHERE NOT EXISTS (SELECT 1 FROM Exams e WHERE e.User_Id = u.User_Id)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Exams initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initializeEventData() {
        String query = """
                INSERT INTO Events (Event_Name, Event_Date, Organized_By_User_Id)
                SELECT 'Orientation Day', CURDATE(), u.User_Id
                FROM Users u
                WHERE NOT EXISTS (
                    SELECT 1 FROM Events e 
                    WHERE e.Event_Name = 'Orientation Day' 
                      AND e.Organized_By_User_Id = u.User_Id
                )
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Events initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeLogData() {
        String query = """
                   INSERT INTO Admin_Activity_Log (Admin_User_Id, Action_Type, Target_Table, Target_Record_Id, Action_Details, IP_Address)
                      SELECT a.User_Id, 'INSERT', 'Events', NULL, 'Default orientation event added', '127.0.0.1'
                      FROM Admins a
                      WHERE a.User_Id NOT IN (
                          SELECT s.Admin_User_Id FROM Admin_Activity_Log s WHERE s.Action_Type = 'INSERT' AND s.Target_Table = 'Events'
                      );
                
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel, "Admin Activity Log initialized for " + rowsInserted + " " +
                    "users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initializeHostelData() {
        String query = """
                    INSERT INTO Hostel (User_Id, Room_No, Allocation_Date, Status)
                    SELECT u.User_Id, 'NA', CURDATE(), 'Pending'
                    FROM Users u
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Hostel h WHERE h.User_Id = u.User_Id
                    );
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Hostel initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeCourseData() {
        String query = """
                    INSERT INTO Course_Mapping (User_Id, Course_Name, Enrollment_Date, Status)
                    SELECT u.User_Id, 'General Studies', CURDATE(), 'Pending'
                    FROM Users u
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Course_Mapping cm WHERE cm.User_Id = u.User_Id
                    );
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Course Mapping initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeFeedbackData() {
        String query = """
                    INSERT INTO Feedback (User_Id, Feedback_Type, Comments, Submitted_On)
                    SELECT u.User_Id, 'General', 'No feedback yet', CURDATE()
                    FROM Users u
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Feedback f WHERE f.User_Id = u.User_Id
                    );
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Feedback initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void initializeLibraryData() {

        String query = """
                    INSERT INTO Library (User_Id, Book_Name, Issue_Date, Return_Date, Status, Remarks)
                    SELECT u.User_Id, 'No Book Issued', CURDATE(), NULL, 'Issued', 'Auto initialized'
                    FROM Users u
                    WHERE NOT EXISTS (
                        SELECT 1 FROM Library l WHERE l.User_Id = u.User_Id
                    );
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            int rowsInserted = stmt.executeUpdate();
            loadFrame.setMessage(errorMessageLabel,
                    "Library initialized for " + rowsInserted + " users.", "green");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleTriggerAll() {
        initializeLibraryData();
        initializeAttendanceData();
        initializeCourseData();
        initializeEventData();
        initializeExamData();
        initializeHostelData();
        initializeFeedbackData();
        initializeFeesData();
        initializeLogData();
        initializeSalaryData();
        loadFrame.setMessage(errorMessageLabel, "All data initialized successfully.", "green");
    }
}
