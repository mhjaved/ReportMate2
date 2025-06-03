package com.hasanjaved.reportmate.model;

public class Report {

    Employee employee;
    String testDate;
    String EmployeeId;
    String projectNo;
    String folderLocation;
    String customerName;
    String customerAddress;
    String userName;
    String userAddress;
    String generalImageTemperature;
    IrTest irTest;
    CrmTest crmTest;
    TripTest tripTest;

    String ownerIdentification;

    String dateOfLastInspection;
    String lastInspectionReportNo;
    Equipment equipment;

    PanelBoard panelBoard;
    ManufacturerCurveDetails manufacturerCurveDetails;

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


    public String getFolderLocation() {
        return folderLocation;
    }

    public void setFolderLocation(String folderLocation) {
        this.folderLocation = folderLocation;
    }

    public String getGeneralImageTemperature() {
        return generalImageTemperature;
    }

    public void setGeneralImageTemperature(String generalImageTemperature) {
        this.generalImageTemperature = generalImageTemperature;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getOwnerIdentification() {
        return ownerIdentification;
    }

    public void setOwnerIdentification(String ownerIdentification) {
        this.ownerIdentification = ownerIdentification;
    }

    public String getDateOfLastInspection() {
        return dateOfLastInspection;
    }

    public void setDateOfLastInspection(String dateOfLastInspection) {
        this.dateOfLastInspection = dateOfLastInspection;
    }

    public String getLastInspectionReportNo() {
        return lastInspectionReportNo;
    }

    public void setLastInspectionReportNo(String lastInspectionReportNo) {
        this.lastInspectionReportNo = lastInspectionReportNo;
    }

    public PanelBoard getPanelBoard() {
        return panelBoard;
    }

    public void setPanelBoard(PanelBoard panelBoard) {
        this.panelBoard = panelBoard;
    }

    public ManufacturerCurveDetails getManufacturerCurveDetails() {
        return manufacturerCurveDetails;
    }

    public void setManufacturerCurveDetails(ManufacturerCurveDetails manufacturerCurveDetails) {
        this.manufacturerCurveDetails = manufacturerCurveDetails;
    }

    public CrmTest getCrmTest() {
        return crmTest;
    }

    public void setCrmTest(CrmTest crmTest) {
        this.crmTest = crmTest;
    }

    public TripTest getTripTest() {
        return tripTest;
    }

    public void setTripTest(TripTest tripTest) {
        this.tripTest = tripTest;
    }
}
