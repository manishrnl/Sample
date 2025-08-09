package org.example.complete_ums.Students;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import org.example.complete_ums.CommonTable.ExamsTable;
import org.example.complete_ums.CommonTable.ResultsTable;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Exams implements Initializable {

    @FXML
    private Button downloadAdmitCardButton, downloadMarksheetButton;
    @FXML
    private TableColumn<ExamsTable, LocalDate> examDateCol;
    @FXML
    private TableColumn<ExamsTable, Integer> examDurationCol, examRoomCol;
    @FXML
    private TableColumn<ExamsTable, String> examSubjectCodeCol, examSubjectNameCol, examTimeCol;
    @FXML
    private TableView<ExamsTable> upcomingExamsTable;
    @FXML
    private TableView<ResultsTable> resultsTable;

    @FXML
    private ComboBox<String> semesterComboBox;

    @FXML
    private TableColumn<ResultsTable, Integer> resultCreditsCol;
    @FXML
    private TableColumn<ResultsTable, String> resultGradeCol, resultStatusCol,
            resultSubjectCodeCol, resultSubjectNameCol;
    @FXML
    private Label sgpa, cgpa;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        examDateCol.setCellValueFactory(cellData -> cellData.getValue().examDateColProperty());
        examDurationCol.setCellValueFactory(cellData -> cellData.getValue().examDurationColProperty().asObject());
        examRoomCol.setCellValueFactory(cellData -> cellData.getValue().examRoomColProperty().asObject());
        examSubjectCodeCol.setCellValueFactory(cellData -> cellData.getValue().examSubjectCodeColProperty());
        examSubjectNameCol.setCellValueFactory(cellData -> cellData.getValue().examSubjectNameColProperty());
        examTimeCol.setCellValueFactory(cellData -> cellData.getValue().examTimeColProperty());

        insertDataIntoUpcomingExamTable();

        resultCreditsCol.setCellValueFactory(cellData -> cellData.getValue().resultCreditsColProperty().asObject());
        resultStatusCol.setCellValueFactory(cellData -> cellData.getValue().resultStatusColProperty());
        resultSubjectCodeCol.setCellValueFactory(cellData -> cellData.getValue().resultSubjectCodeColProperty());
        resultSubjectNameCol.setCellValueFactory(cellData -> cellData.getValue().resultSubjectNameColProperty());
        resultGradeCol.setCellValueFactory(cellData -> cellData.getValue().resultGradeColProperty());

        // Populate ComboBox and set a listener to update the table
        ObservableList<String> semesters = FXCollections.observableArrayList(
                "Semester 1", "Semester 2", "Semester 3", "Semester 4",
                "Semester 5", "Semester 6", "Semester 7", "Semester 8"
        );
        semesterComboBox.setItems(semesters);
        semesterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                insertDataIntoResultsTable(newVal);
            }
        });

        // Load default semester data
        semesterComboBox.getSelectionModel().select("Semester 5");
        sgpa.setText("8.4");
        cgpa.setText("7.9");
    }

    private void insertDataIntoUpcomingExamTable() {
        ObservableList<ExamsTable> upcomingExams = FXCollections.observableArrayList(
                new ExamsTable(LocalDate.of(2025, 8, 10), 180, 101, "CS301", "Database Management Systems", "09:00 AM"),
                new ExamsTable(LocalDate.of(2025, 8, 12), 180, 205, "MA305", "Probability and Statistics", "02:00 PM"),
                new ExamsTable(LocalDate.of(2025, 8, 14), 180, 102, "EE302", "Digital Signal Processing", "09:00 AM"),
                new ExamsTable(LocalDate.of(2025, 8, 16), 120, 110, "HS301", "Engineering Economics", "02:00 PM"),
                new ExamsTable(LocalDate.of(2025, 8, 18), 180, 201, "CS303", "Operating Systems", "09:00 AM")
        );
        upcomingExamsTable.setItems(upcomingExams);
    }

    private void insertDataIntoResultsTable(String selectedSemester) {
        ObservableList<ResultsTable> results = FXCollections.observableArrayList();
        switch (selectedSemester) {
            case "Semester 1":
                results.addAll(
                        new ResultsTable(4, "Pass", "PH101", "Engineering Physics", "A"),
                        new ResultsTable(4, "Pass", "MA101", "Calculus & Algebra", "A+"),
                        new ResultsTable(3, "Pass", "EE101", "Basic Electrical Engineering", "B+"),
                        new ResultsTable(3, "Pass", "CS101", "Intro to Programming", "A"),
                        new ResultsTable(2, "Pass", "HS101", "Communication Skills", "O")
                );
                sgpa.setText("8.1");
                cgpa.setText("7.9");
                break;
            case "Semester 2":
                results.addAll(
                        new ResultsTable(4, "Pass", "CH101", "Engineering Chemistry", "B+"),
                        new ResultsTable(4, "Pass", "MA102", "Differential Equations", "A"),
                        new ResultsTable(3, "Pass", "ME101", "Engineering Mechanics", "A"),
                        new ResultsTable(3, "Pass", "CS102", "Data Structures", "A+"),
                        new ResultsTable(1, "Pass", "ME102", "Workshop Practice", "O")
                );
                sgpa.setText("7.4");
                cgpa.setText("7.0");
                break;
            case "Semester 3":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS201", "Discrete Mathematics", "B"),
                        new ResultsTable(4, "Pass", "CS203", "Object Oriented Programming", "A+"),
                        new ResultsTable(3, "Pass", "EC201", "Digital Logic Design", "A"),
                        new ResultsTable(3, "Pass", "MA201", "Complex Variables", "B+"),
                        new ResultsTable(2, "Pass", "HS201", "Intro to Economics", "A")
                );
                sgpa.setText("8.1");
                cgpa.setText("7.5");
                break;
            case "Semester 4":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS202", "Design & Analysis of Algorithms", "A"),
                        new ResultsTable(4, "Pass", "CS204", "Computer Organization", "A"),
                        new ResultsTable(3, "Pass", "CS206", "Operating Systems", "B+"),
                        new ResultsTable(3, "Pass", "MA202", "Probability & Statistics", "B"),
                        new ResultsTable(2, "Pass", "EV201", "Environmental Science", "O")
                );
                sgpa.setText("8.2");
                cgpa.setText("7.9");
                break;
            case "Semester 5":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS301", "Database Management Systems", "A+"),
                        new ResultsTable(4, "Pass", "CS303", "Theory of Computation", "A"),
                        new ResultsTable(3, "Pass", "CS305", "Computer Networks", "B+"),
                        new ResultsTable(3, "Pass", "CS307", "Software Engineering", "A"),
                        new ResultsTable(2, "Pass", "HS301", "Organizational Behavior", "O")
                );
                break;
            case "Semester 6":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS302", "Compiler Design", "B"),
                        new ResultsTable(4, "Pass", "CS304", "Artificial Intelligence", "A"),
                        new ResultsTable(3, "Pass", "CS306", "Cryptography & Network Security", "A+"),
                        new ResultsTable(3, "Pass", "CS352", "Elective: Web Technologies", "A"),
                        new ResultsTable(2, "Pass", "HS302", "Principles of Management", "O")
                );
                sgpa.setText("6.4");
                cgpa.setText("6.9");
                break;
            case "Semester 7":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS401", "Machine Learning", "A+"),
                        new ResultsTable(4, "Pass", "CS403", "Cloud Computing", "A"),
                        new ResultsTable(3, "Pass", "CS451", "Elective: Data Science", "A"),
                        new ResultsTable(3, "Pass", "CS491", "Project - Phase I", "O"),
                        new ResultsTable(2, "Pass", "HS401", "Business Ethics", "A")
                );
                sgpa.setText("8.4");
                cgpa.setText("7.9");
                break;
            case "Semester 8":
                results.addAll(
                        new ResultsTable(4, "Pass", "CS402", "Big Data Analytics", "A"),
                        new ResultsTable(4, "Pass", "CS454", "Elective: Deep Learning", "A+"),
                        new ResultsTable(3, "Pass", "CS462", "Elective: Mobile Computing", "B+"),
                        new ResultsTable(6, "Pass", "CS492", "Project - Phase II", "O"),
                        new ResultsTable(2, "Pass", "HS402", "Intellectual Property Rights", "A")
                );
                sgpa.setText("9.4");
                cgpa.setText("9.2");
                break;
        }
        resultsTable.setItems(results);
    }

}


