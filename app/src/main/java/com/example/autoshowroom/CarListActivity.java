package com.example.autoshowroom;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoshowroom.service.Car;
import com.example.autoshowroom.service.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CarListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CarViewModel viewModel;
    CarListAdapter carListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        // set my own toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // retrieve view model
        viewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // initialize recycler view
        recyclerView = findViewById(R.id.carListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        carListAdapter = new CarListAdapter(this);
        recyclerView.setAdapter(carListAdapter);

        // FAB
        FloatingActionButton fab = findViewById(R.id.carListFAB);
        fab.setOnClickListener(view -> {
            finish();
        });

        // observe the car list data from view model and update recycler view adapter accordingly
        viewModel.getAllCars().observe(this, cars -> {
            carListAdapter.setCarsData(cars);
            carListAdapter.notifyDataSetChanged();
        });

    }
}
