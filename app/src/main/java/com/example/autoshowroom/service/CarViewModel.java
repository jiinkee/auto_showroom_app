package com.example.autoshowroom.service;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;

import java.util.ArrayList;
import java.util.List;

public class CarViewModel extends AndroidViewModel {
    private CarRepository carRepo;
    private MediatorLiveData<List<Car>> cars;

    public CarViewModel(@NonNull Application application) {
        super(application);
        carRepo = new CarRepository(application);
        cars = new MediatorLiveData<>();
        // fetch all cars at start up
        cars.addSource(carRepo.getAllCars(), carResponse -> {
            cars.setValue(carResponse);
        });
    }

    public LiveData<List<Car>> getAllCars() {
        return cars;
    }

    public void getCarsByFilter(String maker, String year, String price) {
        String query = "";
        List<Object> queryArgs = new ArrayList<>();
        boolean containsConditions = false;
        query += "SELECT * FROM CARS";

        // form query
        if (!maker.equals("")) {
            query += " WHERE carMaker = ?";
            queryArgs.add(maker);
            containsConditions = true;
        }

        if (!year.equals("")) {
            if (containsConditions) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += " carManufactureYear = ?";
            queryArgs.add(year);
            containsConditions = true;
        }

        if (!price.equals("")) {
            if (containsConditions) {
                query += " AND";
            } else {
                query += " WHERE";
            }
            query += " carPrice = ?";
            queryArgs.add(price);
            containsConditions = true;
        }

        query += ";";

        SimpleSQLiteQuery sqlQuery = new SimpleSQLiteQuery(query, queryArgs.toArray());

        cars.addSource(carRepo.getCarsByFilters(sqlQuery), carResponse -> {
           cars.setValue(carResponse);
        });
    }

    public void addNewCar(Car car) {
        carRepo.insert(car);
    }

    public void deleteAllCars() {
        carRepo.deleteAll();
    }
}
