package com.hasanjaved.reportmate.model;

import java.util.List;

public class Equipment {

    String equipmentName;
    String equipmentLocation;
    String generalImage;
    String airTemperature;
    String airHumidity;
    String imageDbBoxFront;
    String imageDbBoxInside;
    String imageDbBoxNamePlate;
    String panelGrounding;

    List<CircuitBreaker> circuitBreakerList;


    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentLocation() {
        return equipmentLocation;
    }

    public void setEquipmentLocation(String equipmentLocation) {
        this.equipmentLocation = equipmentLocation;
    }


    public String getGeneralImage() {
        return generalImage;
    }

    public void setGeneralImage(String generalImage) {
        this.generalImage = generalImage;
    }

    public String getAirTemperature() {
        return airTemperature;
    }

    public void setAirTemperature(String airTemperature) {
        this.airTemperature = airTemperature;
    }

    public String getAirHumidity() {
        return airHumidity;
    }

    public void setAirHumidity(String airHumidity) {
        this.airHumidity = airHumidity+"%";
    }

    public String getImageDbBoxFront() {
        return imageDbBoxFront;
    }

    public void setImageDbBoxFront(String imageDbBoxFront) {
        this.imageDbBoxFront = imageDbBoxFront;
    }

    public String getImageDbBoxInside() {
        return imageDbBoxInside;
    }

    public void setImageDbBoxInside(String imageDbBoxInside) {
        this.imageDbBoxInside = imageDbBoxInside;
    }

    public String getImageDbBoxNamePlate() {
        return imageDbBoxNamePlate;
    }

    public void setImageDbBoxNamePlate(String imageDbBoxNamePlate) {
        this.imageDbBoxNamePlate = imageDbBoxNamePlate;
    }

    public String getPanelGrounding() {
        return panelGrounding;
    }

    public void setPanelGrounding(String panelGrounding) {
        this.panelGrounding = panelGrounding;
    }

    public List<CircuitBreaker> getCircuitBreakerList() {
        return circuitBreakerList;
    }

    public void setCircuitBreakerList(List<CircuitBreaker> circuitBreakerList) {
        this.circuitBreakerList = circuitBreakerList;
    }

    @Override
    public String toString() {
        return "\nEquipment{" +
                "equipmentName='" + equipmentName + '\'' +
                ", equipmentLocation='" + equipmentLocation + '\'' +
                ", generalImage='" + generalImage + '\'' +
                ", airTemperature='" + airTemperature + '\'' +
                ", airHumidity='" + airHumidity + '\'' +
                ", imageDbBoxFront='" + imageDbBoxFront + '\'' +
                ", imageDbBoxInside='" + imageDbBoxInside + '\'' +
                ", imageDbBoxNamePlate='" + imageDbBoxNamePlate + '\'' +
                ", panelGrounding='" + panelGrounding + '\'' +
                ", circuitBreakerList=" + circuitBreakerList +
                '}';
    }
}
