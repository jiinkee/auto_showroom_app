package com.example.autoshowroom.service;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cars")
public class Car {
    static final String CAR_TABLE = "cars";

    @PrimaryKey (autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "carId")
    private int id;

    @ColumnInfo(name = "carMaker")
    private String maker;

    @ColumnInfo(name = "carModel")
    private String model;

    @ColumnInfo(name = "carColor")
    private String color;

    @ColumnInfo(name = "carManufactureYear")
    private int year;

    @ColumnInfo(name = "carSeatNum")
    private int seatNum;

    @ColumnInfo(name = "carPrice")
    private float price;

    public Car(String model, String maker, int year, String color, int seatNum, float price) {
        this.model = model;
        this.maker = maker;
        this.year = year;
        this.color = color;
        this.seatNum = seatNum;
        this.price = price;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMaker() {
        return maker;
    }

    public void setMaker(String maker) {
        this.maker = maker;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public String getYearString() {
        return Integer.toString(this.year);
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public String getSeatNumString() {
        return Integer.toString(this.seatNum);
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public float getPrice() {
        return price;
    }

    public String getPriceString() {
        return Float.toString(this.price);
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String toSimpleString() {
        return this.model + " | " + this.maker;
    }

    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }
}
