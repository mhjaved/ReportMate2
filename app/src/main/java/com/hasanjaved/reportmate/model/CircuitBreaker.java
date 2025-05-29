
package com.hasanjaved.reportmate.model;

public class CircuitBreaker {

    private String circuitId;
    private String name;
    private String size;
    private TripTest tripTest;
    private CrmTest crmTest;

    public String getCircuitId() {
        return circuitId;
    }

    public void setCircuitId(String circuitId) {
        this.circuitId = circuitId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public TripTest getTripTest() {
        return tripTest;
    }

    public void setTripTest(TripTest tripTest) {
        this.tripTest = tripTest;
    }

    public CrmTest getCrmTest() {
        return crmTest;
    }

    public void setCrmTest(CrmTest crmTest) {
        this.crmTest = crmTest;
    }
}
