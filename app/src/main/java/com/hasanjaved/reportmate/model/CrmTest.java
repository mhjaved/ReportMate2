package com.hasanjaved.reportmate.model;

public class CrmTest {

    String connectionImage;
    String resultImage;
    String rResValue;
    String rResUnit;
    String yResValue;
    String yResUnit;
    String bResValue;
    String bResUnit;

    public String getConnectionImage() {
        return connectionImage;
    }

    public void setConnectionImage(String connectionImage) {
        this.connectionImage = connectionImage;
    }

    public String getResultImage() {
        return resultImage;
    }

    public void setResultImage(String resultImage) {
        this.resultImage = resultImage;
    }

    public String getrResValue() {
        return rResValue;
    }

    public void setrResValue(String rResValue) {
        this.rResValue = rResValue;
    }

    public String getrResUnit() {
        return rResUnit;
    }

    public void setrResUnit(String rResUnit) {
        this.rResUnit = rResUnit;
    }

    public String getyResValue() {
        return yResValue;
    }

    public void setyResValue(String yResValue) {
        this.yResValue = yResValue;
    }

    public String getyResUnit() {
        return yResUnit;
    }

    public void setyResUnit(String yResUnit) {
        this.yResUnit = yResUnit;
    }

    public String getbResValue() {
        return bResValue;
    }

    public void setbResValue(String bResValue) {
        this.bResValue = bResValue;
    }

    public String getbResUnit() {
        return bResUnit;
    }

    public void setbResUnit(String bResUnit) {
        this.bResUnit = bResUnit;
    }

    @Override
    public String toString() {
        return "CrmTest{" +
                "connectionImage='" + connectionImage + '\'' +
                ", resultImage='" + resultImage + '\'' +
                ", rResValue='" + rResValue + '\'' +
                ", rResUnit='" + rResUnit + '\'' +
                ", yResValue='" + yResValue + '\'' +
                ", yResUnit='" + yResUnit + '\'' +
                ", bResValue='" + bResValue + '\'' +
                ", bResUnit='" + bResUnit + '\'' +
                '}';
    }
}
