package com.example.autoshowroom;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.autoshowroom.service.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class CarListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    CarViewModel viewModel;
    CarListAdapter carListAdapter;
    EditText filterMaker, filterYear, filterPrice;
    Button filterButton, clearFilterButton;

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

        // filter car list
        filterMaker = findViewById(R.id.filterMaker);
        filterPrice = findViewById(R.id.filterPrice);
        filterYear = findViewById(R.id.filterYear);
        filterButton = findViewById(R.id.btnFilter);
        clearFilterButton = findViewById(R.id.btnClearFilter);

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String makerValue = filterMaker.getText().toString();
                String yearValue = filterYear.getText().toString();
                String priceValue = filterPrice.getText().toString();
                viewModel.getCarsByFilter(makerValue, yearValue, priceValue);
            }
        });

        clearFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear all filters
                filterMaker.setText("");
                filterYear.setText("");
                filterPrice.setText("");
                // get all cars again
                viewModel.getCarsByFilter("", "", "");
            }
        });


    }
}
