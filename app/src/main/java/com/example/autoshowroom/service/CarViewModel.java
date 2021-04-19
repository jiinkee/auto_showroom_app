package com.example.autoshowroom.service;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;

public class CarViewModel extends AndroidViewModel {
    private CarRepository carRepo;

    public CarViewModel(@NonNull Application application) {
        super(application);
        carRepo = new CarRepository(application);
    }

    public LiveData<ArrayList<Car>> getAllCars() {
        return carRepo.getAllCars();
    }

    public void addNewCar(Car car) {
        carRepo.insert(car);
    }
}
