package com.example.socialmediafirstaidapplication;


public class User {
    public String id, user_id, name, email, gender, birthday;
    public boolean isResponder;

    public User() {

    }

    public User (String id, String user_id, String name, String email, String gender, String birthday, boolean isResponder) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.isResponder = isResponder;
    }

    public String getId() {
        return id;
    }

    public String getUser_id() {
        return user_id;
    }

    public boolean isResponder() {
        return isResponder;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getGender() {
        return gender;
    }

    public String getBirthday() {
        return birthday;
    }

}