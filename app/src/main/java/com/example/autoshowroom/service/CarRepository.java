package com.example.autoshowroom.service;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class CarRepository {
    private CarDao carDao;
    private LiveData<List<Car>> cars;

    public CarRepository (Application application) {
        CarDatabase carDatabase = CarDatabase.getDatabase(application);
        carDao = carDatabase.carDao();
        cars = carDao.getAllCars();
    }

    public LiveData<List<Car>> getAllCars() {
        return cars;
    }

    public void insert(Car car) {
        CarDatabase.databaseWriteExecutor.execute(() -> carDao.insert(car));
    }

    public void deleteAll() {
        CarDatabase.databaseWriteExecutor.execute(() -> carDao.deleteAll());
    }
}
