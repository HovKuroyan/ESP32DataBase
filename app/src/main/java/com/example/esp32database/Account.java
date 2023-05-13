package com.example.esp32database;

public class Account {
    private String login;
    private String name;
    private String password;

    public Account(String name, String login, String password) {
        this.login = login;
        this.name = name;
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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
}

