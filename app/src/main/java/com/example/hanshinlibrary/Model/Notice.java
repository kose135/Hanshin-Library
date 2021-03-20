package com.example.hanshinlibrary.Model;

public class Notice {
    public String num;
    public String title;
    public String type;
    public String date;
    public String contents;

    public Notice(String num, String title, String type, String date, String contents) {
        this.num = num;
        this.title = title;
        this.type = type;
        this.date = date;
        this.contents = contents;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
