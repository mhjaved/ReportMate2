package com.hasanjaved.reportmate.model;

import java.util.List;

public class ReportHistory {

    List<Report> completedReportList ;
    List<Report> incompleteReportList ;

    public List<Report> getCompletedReportList() {
        return completedReportList;
    }

    public void setCompletedReportList(List<Report> completedReportList) {
        this.completedReportList = completedReportList;
    }

    public List<Report> getIncompleteReportList() {
        return incompleteReportList;
    }

    public void setIncompleteReportList(List<Report> incompleteReportList) {
        this.incompleteReportList = incompleteReportList;
    }
}
