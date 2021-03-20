package com.example.hanshinlibrary.Model;

public class BookHistory {
    private String id;
    private String title;
    private String author;
    private String publisher;
    private String state;
    private String loanDate;
    private String returnDate;

    public BookHistory(String id, String title, String author, String publisher, String state, String loanDate, String returnDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.state = state;
        this.loanDate = loanDate;
        this.returnDate = returnDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(String loanDate) {
        this.loanDate = loanDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }
}
