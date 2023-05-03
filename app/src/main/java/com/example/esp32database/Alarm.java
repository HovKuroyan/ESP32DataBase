package com.example.esp32database;

public class Alarm {
    private String dateTime;
    private String alarmType;
    private String switchState;

    public Alarm() {}

    public Alarm(String dateTime, String alarmType, String switchState) {
        this.dateTime = dateTime;
        this.alarmType = alarmType;
        this.switchState = switchState;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String isSwitchState() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState = switchState;
    }
}
