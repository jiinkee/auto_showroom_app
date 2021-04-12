package com.example.autoshowroom;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class CarListActivity extends AppCompatActivity {
    public final static String CAR_OBJ_LIST = "car_obj_list";
    RecyclerView recyclerView;
    ArrayList<Car> carsData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_list);

        // set my own toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // retrieve data from Intent
        String carJson = (String) getIntent().getSerializableExtra(CAR_OBJ_LIST);
        Type type = new TypeToken<ArrayList<Car>>() {}.getType();
        carsData = new Gson().fromJson(carJson, type);

        // initialize recycler view
        recyclerView = findViewById(R.id.carListRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new CarListAdapter(this, carsData));

    }
}
