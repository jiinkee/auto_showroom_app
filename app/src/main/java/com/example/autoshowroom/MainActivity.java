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
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText makerEditText, modelEditText, yearEditText, colorEditText, seatEditText, priceEditText;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ListView carList;
    private Context context;
    ArrayList<String> carStringArray = new ArrayList<>();
    ArrayAdapter<String> carStringArrayAdapter;

    ArrayList<Car> carsArray = new ArrayList<>();

    private final static int NULL_INT_INPUT = -1;
    private final static float NULL_FLOAT_INPUT = -1;

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
        restoreSharedPreferences();
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
                case R.id.remove_last:
                    if (carStringArray.size() > 0) {
                        carStringArray.remove(carStringArray.size() - 1);
                        carStringArrayAdapter.notifyDataSetChanged();
                    }
                    break;
                case R.id.remove_all:
                    carStringArray.clear();
                    carStringArrayAdapter.notifyDataSetChanged();
                    break;
                case R.id.list_all_cars:
                    Intent intent = new Intent(context, CarListActivity.class);
                    String carJson = new Gson().toJson(carsArray);
                    intent.putExtra(CarListActivity.CAR_OBJ_LIST, carJson);
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

        // save user inputs into SharedPreferences file
        saveSharedPreferences();

        // add car details to car list
        Car newCar = new Car(makerEditText.getText().toString(),
                            modelEditText.getText().toString(),
                            Integer.parseInt(yearEditText.getText().toString()),
                            colorEditText.getText().toString(),
                            Integer.parseInt(seatEditText.getText().toString()),
                            Float.parseFloat(priceEditText.getText().toString()));

        carsArray.add(newCar);
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

    private void saveSharedPreferences() {
        SharedPreferences persistentLastCar = getPreferences(0);
        SharedPreferences.Editor editor = persistentLastCar.edit();

        editor.putString(getString(R.string.maker), makerEditText.getText().toString());
        editor.putString(getString(R.string.model), modelEditText.getText().toString());
        editor.putInt(getString(R.string.year), Integer.parseInt(yearEditText.getText().toString()));
        editor.putString(getString(R.string.color), colorEditText.getText().toString());
        editor.putInt(getString(R.string.seats), Integer.parseInt(seatEditText.getText().toString()));
        editor.putFloat(getString(R.string.price), Float.parseFloat(priceEditText.getText().toString()));

        editor.apply();
    }

    private void restoreSharedPreferences() {
        SharedPreferences persistentLastCar = getPreferences(0);

        String lastMaker = persistentLastCar.getString(getString(R.string.maker), "");
        makerEditText.setText(lastMaker);

        String lastModel = persistentLastCar.getString(getString(R.string.model), "");
        modelEditText.setText(lastModel);

        String lastYear = Integer.toString(persistentLastCar.getInt(getString(R.string.year), NULL_INT_INPUT));
        yearEditText.setText(validateInput(lastYear));

        String lastColor = persistentLastCar.getString(getString(R.string.color), "");
        colorEditText.setText(lastColor);

        String lastSeat = Integer.toString(persistentLastCar.getInt(getString(R.string.seats), NULL_INT_INPUT));
        seatEditText.setText(validateInput(lastSeat));

        String lastPrice = Float.toString(persistentLastCar.getFloat(getString(R.string.price), NULL_FLOAT_INPUT));
        priceEditText.setText(validateInput(lastPrice));
    }

    private String validateInput(String userInput) {
        return (userInput.equals(Integer.toString(NULL_INT_INPUT)) || userInput.equals(Float.toString(NULL_FLOAT_INPUT)))
                ? ""
                : userInput;
    }

    private void clearSharedPreferences() {
        SharedPreferences persistentLastCar = getPreferences(0);
        SharedPreferences.Editor editor = persistentLastCar.edit();

        editor.clear();
        editor.apply();
    }
}