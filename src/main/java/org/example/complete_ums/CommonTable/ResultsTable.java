package org.example.complete_ums.CommonTable;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ResultsTable {

    private SimpleIntegerProperty resultCreditsCol;
    private SimpleStringProperty resultStatusCol, resultSubjectCodeCol, resultSubjectNameCol,resultGradeCol;


    public ResultsTable(int resultCreditsCol, String resultStatusCol, String resultSubjectCodeCol, String resultSubjectNameCol,
                        String resultGradeCol) {
        this.resultCreditsCol = new SimpleIntegerProperty(resultCreditsCol);
        this.resultStatusCol = new SimpleStringProperty(resultStatusCol);
        this.resultSubjectCodeCol = new SimpleStringProperty(resultSubjectCodeCol);
        this.resultSubjectNameCol = new SimpleStringProperty(resultSubjectNameCol);
        this.resultGradeCol = new SimpleStringProperty(resultGradeCol);
    }

    public int getResultCreditsCol() {
        return resultCreditsCol.get();
    }

    public SimpleIntegerProperty resultCreditsColProperty() {
        return resultCreditsCol;
    }

    public String getResultStatusCol() {
        return resultStatusCol.get();
    }

    public SimpleStringProperty resultStatusColProperty() {
        return resultStatusCol;
    }

    public String getResultSubjectCodeCol() {
        return resultSubjectCodeCol.get();
    }

    public SimpleStringProperty resultSubjectCodeColProperty() {
        return resultSubjectCodeCol;
    }

    public String getResultSubjectNameCol() {
        return resultSubjectNameCol.get();
    }

    public SimpleStringProperty resultSubjectNameColProperty() {
        return resultSubjectNameCol;
    }

    public String getResultGradeCol() {
        return resultGradeCol.get();
    }

    public SimpleStringProperty resultGradeColProperty() {
        return resultGradeCol;
    }
}
