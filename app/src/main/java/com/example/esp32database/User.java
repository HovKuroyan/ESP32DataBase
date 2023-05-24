package com.example.esp32database;

public class User {
    private String uid;
    private String role;

    public User() {
        // Required empty constructor for Firestore
    }

    public User(String uid, String role) {
        this.uid = uid;
        this.role = role;
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
}
