package com.example.autoshowroom.service;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.ArrayList;

@Dao
public interface CarDao {
    @Query("select * from cars")
    LiveData<ArrayList<Car>> getAllCars();

    @Insert
    void insert(Car car);
}
