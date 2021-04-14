package com.example.autoshowroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText makerEditText, modelEditText, yearEditText, colorEditText, seatEditText, priceEditText;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ListView carList;
    private Context context;
    // for list view (volatile data)
    ArrayList<String> carStringArray = new ArrayList<>();
    ArrayAdapter<String> carStringArrayAdapter;
    // for recycler view (persistent data)
    ArrayList<Car> carsArray;

    Gson gson = new Gson();

    public final static String CAR_OBJ_LIST = "car_obj_list";
    public final static String CAR_SP = "cars";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        context = this;

        getAllEditTexts();

        // add my own toolbar
        initializeMyOwnToolBar();
        // add navigation drawer & toggle menu
        initializeNavMenu();
        // add FAB
        initializeFAB();
        // bind list view with its data source using adapter
        initializeListView(savedInstanceState);
        // allow app to get car details input from SMS
        initializeSMSInput();

    }

    private void initializeMyOwnToolBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initializeNavMenu() {
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open_drawer, R.string.close_drawer);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navView = findViewById(R.id.nav_view);
        navView.setNavigationItemSelectedListener(new NavMenuListener());
    }

    private void initializeFAB() {
        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> addNewCar());
    }

    private void initializeListView(Bundle savedInstanceState) {
        carList = findViewById(R.id.listView);
        if (savedInstanceState != null) {
            carStringArray = savedInstanceState.getStringArrayList("CARS");
        }
        carStringArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, carStringArray);
        carList.setAdapter(carStringArrayAdapter);
    }

    private void initializeSMSInput() {
        // Request permissions to access SMS
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
                Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 0);

        // register broadcast receiver to tokenize an display values in SMS
        IntentFilter smsTokenizeIntentFilter = new IntentFilter(SMSReceiver.SMS_TOKENIZE);
        BroadcastReceiver smsTokenizeReceiver = new SMSTokenizeReceiver();
        registerReceiver(smsTokenizeReceiver, smsTokenizeIntentFilter);
    }

    @Override
    protected void onStart() {
        // get the last added cars from SP and populate its details in the fields
        restoreLastCarFromSP();
        super.onStart();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putStringArrayList("CARS", carStringArray);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        clearAllEditTexts();
        return true;
    }

    class NavMenuListener implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.add_car:
                    addNewCar();
                    break;
                case R.id.remove_last: // TODO does this affect the persistent SP data?
                    if (carStringArray.size() > 0) {
                        carStringArray.remove(carStringArray.size() - 1);
                        carStringArrayAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.remove_all: // TODO does this affect the persistent SP data?
                    carStringArray.clear();
                    carStringArrayAdapter.notifyDataSetChanged();
                    break;
                case R.id.list_all_cars:
                    Intent intent = new Intent(context, CarListActivity.class);
                    startActivity(intent);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    private void addNewCar() {
        // hide keyboard
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(findViewById(android.R.id.content).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        // show toaster
        String makerInput = makerEditText.getText().toString();
        Toast.makeText(MainActivity.this, "A new car from " + makerInput + " added", Toast.LENGTH_SHORT).show();

        // add car details to car list
        Car newCar = new Car(makerEditText.getText().toString(),
                            modelEditText.getText().toString(),
                            Integer.parseInt(yearEditText.getText().toString()),
                            colorEditText.getText().toString(),
                            Integer.parseInt(seatEditText.getText().toString()),
                            Float.parseFloat(priceEditText.getText().toString()));

        // save user inputs into SharedPreferences file
        saveSharedPreferences(newCar);

        carStringArray.add(newCar.toSimpleString());
        carStringArrayAdapter.notifyDataSetChanged();
    }

    private void getAllEditTexts() {
        makerEditText = findViewById(R.id.editMaker);
        modelEditText = findViewById(R.id.editModel);
        yearEditText = findViewById(R.id.editYear);
        colorEditText = findViewById(R.id.editColor);
        seatEditText = findViewById(R.id.editSeat);
        priceEditText = findViewById(R.id.editPrice);
    }

    private void clearAllEditTexts() {
        makerEditText.getText().clear();
        modelEditText.getText().clear();
        yearEditText.getText().clear();
        colorEditText.getText().clear();
        seatEditText.getText().clear();
        priceEditText.getText().clear();
    }

    private void saveSharedPreferences(Car car) {
        SharedPreferences persistentCars = getSharedPreferences(CAR_SP,0);
        SharedPreferences.Editor editor = persistentCars.edit();

        // add the new car into the cars array list
        carsArray.add(car);

        // overwrite the original cars array json with the new one
        Gson gson = new Gson();
        String carJson = gson.toJson(carsArray);
        editor.putString(CAR_OBJ_LIST, carJson);

        editor.apply();
    }

    private void restoreLastCarFromSP() {
        // get the original list of cars stored in SharedPreferences
        SharedPreferences persistentCars = getSharedPreferences(CAR_SP, 0);
        String carJson = persistentCars.getString(CAR_OBJ_LIST, "");
        Type type = new TypeToken<ArrayList<Car>>() {}.getType();
        carsArray = gson.fromJson(carJson, type);

        // get the last added car and populate its details into the EditText
        if (carsArray != null && carsArray.size() >= 1){
            Car lastCar = carsArray.get(carsArray.size() - 1);
            makerEditText.setText(lastCar.getMaker());
            modelEditText.setText(lastCar.getModel());
            yearEditText.setText(lastCar.getYearString());
            colorEditText.setText(lastCar.getColor());
            seatEditText.setText(lastCar.getSeatNumString());
            priceEditText.setText(lastCar.getPriceString());
        } else {
            carsArray = new ArrayList<>();
        }
    }

    private void clearSharedPreferences() {
        SharedPreferences persistentLastCar = getSharedPreferences(CAR_SP, 0);
        SharedPreferences.Editor editor = persistentLastCar.edit();

        editor.clear();
        editor.apply();
    }

    class SMSTokenizeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG);

            StringTokenizer tokenizer = new StringTokenizer(msg, ";");
            String maker = tokenizer.nextToken();
            String model = tokenizer.nextToken();
            String year = tokenizer.nextToken();
            String colour = tokenizer.nextToken();
            String seats = tokenizer.nextToken();
            String price = tokenizer.nextToken();

            makerEditText.setText(maker);
            modelEditText.setText(model);
            yearEditText.setText(year);
            colorEditText.setText(colour);
            priceEditText.setText(price);

            // validate the number of seats
            if (Integer.parseInt(seats) < 4 || Integer.parseInt(seats) > 8) {
                seatEditText.setText("Must be within 4 to 8");
            } else {
                seatEditText.setText(seats);
            }
        }
    }
}