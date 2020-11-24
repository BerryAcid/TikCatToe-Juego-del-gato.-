package com.berryacid.tikcattoe.model;

public class User {
    private String name;
    private int point;
    private int partidasJugadas;

    public User() {
    }

    public User(String name, int point, int partidasJugadas) {
        this.name = name;
        this.point = point;
        this.partidasJugadas = partidasJugadas;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPoint() {
        return point;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }
}
