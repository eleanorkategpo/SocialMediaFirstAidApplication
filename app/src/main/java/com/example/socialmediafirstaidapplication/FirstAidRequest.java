package com.example.socialmediafirstaidapplication;

public class FirstAidRequest {
    public String id, user_id, user_name, situation, responder_id, formattedAddress, phoneNumber;
    public double longitude, latitude;
    public int status;

    public FirstAidRequest () {

    }

    public FirstAidRequest(String id, String user_id, String user_name, String situation, String responder_id, double longitude, double latitude, String formattedAddress, String phoneNumber, int status) {
        this.id = id;
        this.user_id = user_id;
        this.user_name = user_name;
        this.situation = situation;
        this.responder_id = responder_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.formattedAddress = formattedAddress;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

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
}
