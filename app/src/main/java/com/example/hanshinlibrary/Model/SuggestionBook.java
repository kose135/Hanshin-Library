package com.example.hanshinlibrary.Model;

public class SuggestionBook {
    public String num1;
    public String type1;
    public String title1;
    public String date1;
    public String writer1;
    public String contents1;

    public SuggestionBook(String num1, String type1, String title1, String date1, String writer1, String contents1) {
        this.num1 = num1;
        this.type1 = type1;
        this.title1 = title1;
        this.date1 = date1;
        this.writer1 = writer1;
        this.contents1 = contents1;
    }

    public String getNum1() {
        return num1;
    }

    public void setNum1(String num1) {
        this.num1 = num1;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getDate1() {
        return date1;
    }

    public void setDate1(String date1) {
        this.date1 = date1;
    }

    public String getWriter1() {
        return writer1;
    }

    public void setWriter1(String writer1) {
        this.writer1 = writer1;
    }

    public String getContents1() {
        return contents1;
    }

    public void setContents1(String contents1) {
        this.contents1 = contents1;
    }
}
