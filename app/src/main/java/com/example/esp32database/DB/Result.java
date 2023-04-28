package com.example.esp32database.DB;

public class Result {
    private int id;
    private String bellNumber;
    private String sendText;
    private String receiveText;
    private int stayIn;

    public Result(int id, String bellNumber, String sendText, String receiveText, int stayIn) {
        this.id = id;
        this.bellNumber = bellNumber;
        this.sendText = sendText;
        this.receiveText = receiveText;
        this.stayIn = stayIn;
    }

    public int getId() {
        return id;
    }

    public String getBellNumber() {
        return bellNumber;
    }

    public String getSendText() {
        return sendText;
    }

    public String getReceiveText() {
        return receiveText;
    }

    public int getStayIn() {
        return stayIn;
    }
}

