package com.example.esp32database;

public class User {
    private String uid;
    private String role;
    private String name;
    private String password;
    private String isAlarm;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String uid, String role, String name, String password, String isAlarm) {
        this.uid = uid;
        this.role = role;
        this.name = name;
        this.password = password;
        this.isAlarm = isAlarm;
    }



    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIsAlarm() {
        return isAlarm;
    }

    public void setIsAlarm(String isAlarm) {
        this.isAlarm = isAlarm;
    }
}
