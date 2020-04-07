package ua.com.it_school.weatherapp;

import java.util.Date;

public class Main {
    private double temp;
    private int pressure;
    private int humidity;
    private Date date;
    private String description;
    private int speed;
    private int deg;
    private int clouds;
    private String name;

    public Main(double temp, int pressure, int humidity, Date date, String description, int speed, int deg, int clouds, String city) {
        this.temp = temp;
        this.pressure = pressure;
        this.humidity = humidity;
        this.date = date;
        this.description = description;
        this.speed = speed;
        this.deg = deg;
        this.clouds = clouds;
        this.name = city;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(int pressure) {
        this.pressure = pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date;}

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        return sb.append("City: " + name).append(", \n" + date).append("\n" +  description).append(", " + String.format("%4.1f",temp)).
                append("\n" + pressure + " mm").append(",\thumidity: " + humidity).append("\nwind: " + speed + "("+ deg+")").toString();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getDeg() {
        return deg;
    }

    public void setDeg(int deg) {
        this.deg = deg;
    }

    public int getClouds() {
        return clouds;
    }

    public void setClouds(int clouds) {
        this.clouds = clouds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}