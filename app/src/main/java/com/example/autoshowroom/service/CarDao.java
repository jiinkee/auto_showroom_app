package com.example.autoshowroom.service;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RawQuery;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.List;

@Dao
public interface CarDao {
    @Query("select * from cars")
    LiveData<List<Car>> getAllCars();

    @Insert
    void insert(Car car);

    @Query("delete from cars")
    void deleteAll();

    @RawQuery(observedEntities = Car.class)
    LiveData<List<Car>> getCarsByFilters(SimpleSQLiteQuery filterQuery);
}
