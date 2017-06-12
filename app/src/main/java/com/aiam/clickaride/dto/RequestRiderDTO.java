package com.aiam.clickaride.dto;

/**
 * Created by aiam on 5/28/2017.
 */

public class RequestRiderDTO {
    private String requestor;
    private String requestTime;
    private String requestLocationOrigin;
    private String requestLocationDestination;

    private String status;
    private double distance;
    private double price;

    private String acceptedBy;
    private String arrivalTime;
    private String arrivalLocationOrigin;
    private String arrivalLocationCurrent;

    public String getRequestor() {
        return requestor;
    }

    public void setRequestor(String requestor) {
        this.requestor = requestor;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getRequestLocationOrigin() {
        return requestLocationOrigin;
    }

    public void setRequestLocationOrigin(String requestLocationOrigin) {
        this.requestLocationOrigin = requestLocationOrigin;
    }

    public String getRequestLocationDestination() {
        return requestLocationDestination;
    }

    public void setRequestLocationDestination(String requestLocationDestination) {
        this.requestLocationDestination = requestLocationDestination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalLocationOrigin() {
        return arrivalLocationOrigin;
    }

    public void setArrivalLocationOrigin(String arrivalLocationOrigin) {
        this.arrivalLocationOrigin = arrivalLocationOrigin;
    }

    public String getArrivalLocationCurrent() {
        return arrivalLocationCurrent;
    }

    public void setArrivalLocationCurrent(String arrivalLocationCurrent) {
        this.arrivalLocationCurrent = arrivalLocationCurrent;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
