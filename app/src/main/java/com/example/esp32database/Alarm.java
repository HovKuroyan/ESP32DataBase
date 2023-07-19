package com.example.esp32database;

public class Alarm {
    private String dateTime;
    private String alarmType;
    private String switchState;
    private String schoolName;

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Alarm() {}

    public Alarm(String dateTime, String alarmType, String switchState, String schoolName) {
        this.dateTime = dateTime;
        this.alarmType = alarmType;
        this.switchState = switchState;
        this.schoolName = schoolName;
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

    public String getSwitchState() {
        return switchState;
    }

    public void setSwitchState(String switchState) {
        this.switchState = switchState;
    }
}
