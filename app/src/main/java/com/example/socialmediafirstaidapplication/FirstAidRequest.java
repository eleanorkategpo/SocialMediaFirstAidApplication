package com.example.socialmediafirstaidapplication;

public class FirstAidRequest {
    public String id, user_id, situation, responder_id;
    public double longitude, latitude;
    public int status;

    public FirstAidRequest () {

    }

    public FirstAidRequest(String id, String user_id, double longitude, double latitude, String situation, String responder_id, int status) {
        this.id = id;
        this.user_id = user_id;
        this.longitude = longitude;
        this.latitude = latitude;
        this.situation = situation;
        this.responder_id = responder_id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
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
}
