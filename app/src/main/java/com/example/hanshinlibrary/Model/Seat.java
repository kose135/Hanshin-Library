package com.example.hanshinlibrary.Model;

public class Seat {
    private String roomName;
    private String num;
    private String state;
    private String date;
    private String student_ID;

    public Seat(String roomName, String num, String state, String date, String student_ID) {
        this.roomName = roomName;
        this.num = num;
        this.state = state;
        this.date = date;
        this.student_ID = student_ID;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudent_ID() {
        return student_ID;
    }

    public void setStudent_ID(String student_ID) {
        this.student_ID = student_ID;
    }
}
