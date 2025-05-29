package com.hasanjaved.reportmate.model;

public class Employee {
    String employeeId;
    String EmployeeName;
    String device;
    String registrationDate;
    String deviceValidity;

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return EmployeeName;
    }

    public void setEmployeeName(String employeeName) {
        EmployeeName = employeeName;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getDeviceValidity() {
        return deviceValidity;
    }

    public void setDeviceValidity(String deviceValidity) {
        this.deviceValidity = deviceValidity;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", EmployeeName='" + EmployeeName + '\'' +
                ", device='" + device + '\'' +
                ", registrationDate='" + registrationDate + '\'' +
                ", deviceValidity='" + deviceValidity + '\'' +
                '}';
    }
}
