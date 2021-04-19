package com.example.autoshowroom;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoshowroom.service.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CarListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<Car> carsData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        // set my own toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // retrieve car list data from Intent
        String carJson = (String) getIntent().getSerializableExtra(MainActivity.CAR_OBJ_LIST);
        Type type = new TypeToken<ArrayList<Car>>() {}.getType();
        carsData = new Gson().fromJson(carJson, type);

        // initialize recycler view
        recyclerView = findViewById(R.id.carListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CarListAdapter(this, carsData));

        // FAB
        FloatingActionButton fab = findViewById(R.id.carListFAB);
        fab.setOnClickListener(view -> {
            finish();
        });

    }

    private void getCarsDataFromSP() {
        SharedPreferences persistentCars = getSharedPreferences(MainActivity.CAR_SP, 0);
        String carsJson = persistentCars.getString(MainActivity.CAR_OBJ_LIST, "");

        if (carsJson.equals("")) {
            carsData = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<Car>>() {}.getType();
            Gson gson = new Gson();
            carsData = gson.fromJson(carsJson, type);
        }
    }
}
