package com.hasanjaved.reportmate.model;

public class Report {


    Employee employee;
    String testDate;

    String EmployeeId;
    String projectNo;
    IrTest irTest;
    Equipment equipment;

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getTestDate() {
        return testDate;
    }

    public void setTestDate(String testDate) {
        this.testDate = testDate;
    }

    public String getProjectNo() {
        return projectNo;
    }

    public void setProjectNo(String projectNo) {
        this.projectNo = projectNo;
    }

    public IrTest getIrTest() {
        return irTest;
    }

    public void setIrTest(IrTest irTest) {
        this.irTest = irTest;
    }

    public Equipment getEquipment() {
        return equipment;
    }

    public void setEquipment(Equipment equipment) {
        this.equipment = equipment;
    }


    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String employeeName) {
        EmployeeId = employeeName;
    }

    @Override
    public String toString() {
        return "Report{" +
                "employee=" + employee +
                ", testDate='" + testDate + '\'' +
                ", EmployeeName='" + EmployeeId + '\'' +
                ", projectNo='" + projectNo + '\'' +
                ", irTest=" + irTest +
                ", equipment=" + equipment +
                '}';
    }
}
