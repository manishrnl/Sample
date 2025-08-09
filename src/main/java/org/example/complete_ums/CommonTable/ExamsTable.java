package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class ExamsTable {

    private SimpleObjectProperty examDateCol;
    private SimpleIntegerProperty examDurationCol, examRoomCol;
    private SimpleStringProperty examSubjectCodeCol, examSubjectNameCol, examTimeCol;

    public ExamsTable(LocalDate examDateCol, int examDurationCol,
                      int examRoomCol, String examSubjectCodeCol,
                      String examSubjectNameCol, String examTimeCol) {
        this.examDateCol = new SimpleObjectProperty(examDateCol);
        this.examDurationCol = new SimpleIntegerProperty(examDurationCol);
        this.examRoomCol = new SimpleIntegerProperty(examRoomCol);
        this.examSubjectCodeCol = new SimpleStringProperty(examSubjectCodeCol);
        this.examSubjectNameCol = new SimpleStringProperty(examSubjectNameCol);
        this.examTimeCol = new SimpleStringProperty(examTimeCol);
    }

    public Object getExamDateCol() {
        return examDateCol.get();
    }

    public SimpleObjectProperty examDateColProperty() {
        return examDateCol;
    }

    public int getExamDurationCol() {
        return examDurationCol.get();
    }

    public SimpleIntegerProperty examDurationColProperty() {
        return examDurationCol;
    }

    public int getExamRoomCol() {
        return examRoomCol.get();
    }

    public SimpleIntegerProperty examRoomColProperty() {
        return examRoomCol;
    }

    public String getExamSubjectCodeCol() {
        return examSubjectCodeCol.get();
    }

    public SimpleStringProperty examSubjectCodeColProperty() {
        return examSubjectCodeCol;
    }

    public String getExamSubjectNameCol() {
        return examSubjectNameCol.get();
    }

    public SimpleStringProperty examSubjectNameColProperty() {
        return examSubjectNameCol;
    }

    public String getExamTimeCol() {
        return examTimeCol.get();
    }

    public SimpleStringProperty examTimeColProperty() {
        return examTimeCol;
    }
}
