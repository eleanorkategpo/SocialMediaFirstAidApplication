package com.example.socialmediafirstaidapplication;

public class FirstAidRequest {
    public String id, user_id, user_name, situation, responder_id, formattedAddress, phoneNumber, dateRequested, dateAccepted;
    public double longitude, latitude;
    public int status;

    public FirstAidRequest () {

    }

    public FirstAidRequest(String id, String user_id, String user_name, String situation, String responder_id, double longitude, double latitude, String formattedAddress, String phoneNumber, String dateRequested, String dateAccepted, int status) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.situation = situation;
        this.responder_id = responder_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.formattedAddress = formattedAddress;
        this.phoneNumber = phoneNumber;
        this.dateRequested = dateRequested;
        this.dateAccepted = dateAccepted;
        this.status = status;
    }


    //getter
    public String getId() {
        return id;
    }

    public String getUserId() {
        return user_id;
    }

    public String getName() {
        return user_name;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getSituation() {
        return situation;
    }

    public String getResponder_id() {
        return responder_id;
    }

    public int getStatus() {
        return status;
    }

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public String getDateAccepted() {
        return dateAccepted;
    }

    //setter

    public void setId(String id) {
        this.id = id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setResponderId(String responder_id) {
        this.responder_id = responder_id;
    }

    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public void setDateAccepted(String dateAccepted) {
        this.dateAccepted = dateAccepted;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
