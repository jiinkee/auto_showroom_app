package com.example.autoshowroom.service;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class CarRepository {
    private CarDao carDao;
    private LiveData<ArrayList<Car>> cars;

    public CarRepository (Application application) {
        CarDatabase carDatabase = CarDatabase.getDatabase(application);
        carDao = carDatabase.carDao();
        cars = carDao.getAllCars();
    }

    public LiveData<ArrayList<Car>> getAllCars() {
        return cars;
    }

    public void insert(Car car) {
        CarDatabase.databaseWriteExecutor.execute(() -> carDao.insert(car));
    }
}
