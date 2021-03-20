package com.example.hanshinlibrary.Model;

public class ReadingRoom {
    private String name;
    private String total;
    private String using;
    private String reservation;
    private String residual;

    public ReadingRoom(String name, String total, String using, String reservation, String residual) {
        this.name = name;
        this.total = total;
        this.using = using;
        this.reservation = reservation;
        this.residual = residual;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getUsing() {
        return using;
    }

    public void setUsing(String using) {
        this.using = using;
    }

    public String getReservation() {
        return reservation;
    }

    public void setReservation(String reservation) {
        this.reservation = reservation;
    }

    public String getResidual() {
        return residual;
    }

    public void setResidual(String residual) {
        this.residual = residual;
    }
}
