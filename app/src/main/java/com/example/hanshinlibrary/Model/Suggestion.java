package com.example.hanshinlibrary.Model;

public class Suggestion {
    public String num;
    public String type;
    public String title;
    public String date;
    public String writer;
    public String contents;

    public Suggestion(String num, String type, String title, String date, String writer, String contents) {
        this.num = num;
        this.type = type;
        this.title = title;
        this.date = date;
        this.writer = writer;
        this.contents = contents;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
