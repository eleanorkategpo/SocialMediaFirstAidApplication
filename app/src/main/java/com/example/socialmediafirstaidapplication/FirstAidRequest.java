package com.example.socialmediafirstaidapplication;

public class FirstAidRequest {
    public String user_id, name, situation, responder_id;
    public double longitude, latitude;
    public boolean resolved;

    public FirstAidRequest () {

    }

    public FirstAidRequest(String user_id, String name, double longitude, double latitude, String situation, String responder_id, Boolean resolved) {
        this.user_id = user_id;
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
        this.situation = situation;
        this.responder_id = responder_id;
        this.resolved = resolved;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getName() {
        return name;
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

    public boolean isResolved() {
        return resolved;
    }
}
