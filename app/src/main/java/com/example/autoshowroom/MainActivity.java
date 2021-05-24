package com.example.autoshowroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.icu.number.Scale;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.autoshowroom.service.Car;
import com.example.autoshowroom.service.CarViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText makerEditText, modelEditText, yearEditText, colorEditText, seatEditText, priceEditText;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Context context;
    private CarViewModel viewModel;
    public DatabaseReference myRef;
//    private float initialX, initialY; // store the initial x & y coordinates of touch event

    Gson gson = new Gson();
    public final static String CAR_OBJ_LIST = "car_obj_list";
    public final static String CAR_SP = "cars";

    GestureDetector gestureDetector;
    ScaleGestureDetector scaleGestureDetector;

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
        // allow app to get car details input from SMS
        initializeSMSInput();

        // get view model
        viewModel = new ViewModelProvider(this).get(CarViewModel.class);

        // set up Firebase connection
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("autoShowroom/fleet");

        // set gesture detector listener
        gestureDetector = new GestureDetector(this, new MyGestureDetector());
        scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureDetector());

        View constraintLayout = findViewById(R.id.constraintLayoutId);
        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                scaleGestureDetector.onTouchEvent(event);
                return true;
            }
        });
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
                    // do nothing
                    break;
                case R.id.remove_all:
                    viewModel.deleteAllCars();
                    // delete all cars in Firebase as well
                    myRef.removeValue();
                    Toast.makeText(getApplicationContext(), "All cars deleted from database", Toast.LENGTH_SHORT).show();
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
        Car newCar = new Car(modelEditText.getText().toString(),
                            makerEditText.getText().toString(),
                            Integer.parseInt(yearEditText.getText().toString()),
                            colorEditText.getText().toString(),
                            Integer.parseInt(seatEditText.getText().toString()),
                            Float.parseFloat(priceEditText.getText().toString()));

        // save the latest car into SharedPreferences file
        saveLastCarInSP(newCar);

        // save the latest car into local ROOM Database
        viewModel.addNewCar(newCar);

        // store the latest car in Firebase
        myRef.push().setValue(newCar);
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

    private void saveLastCarInSP(Car car) {
        SharedPreferences persistentCars = getSharedPreferences(CAR_SP,0);
        SharedPreferences.Editor editor = persistentCars.edit();

        // overwrite the original cars array json with the new one
        Gson gson = new Gson();
        String carJson = gson.toJson(car);
        editor.putString(CAR_OBJ_LIST, carJson);

        editor.apply();
    }

    private void restoreLastCarFromSP() {
        // get the saved last car stored in SharedPreferences
        SharedPreferences persistentCars = getSharedPreferences(CAR_SP, 0);
        String carJson = persistentCars.getString(CAR_OBJ_LIST, "");
        Type type = new TypeToken<Car>() {}.getType();
        Car lastCar = gson.fromJson(carJson, type);

        if (lastCar != null) {
            makerEditText.setText(lastCar.getMaker());
            modelEditText.setText(lastCar.getModel());
            yearEditText.setText(lastCar.getYearString());
            colorEditText.setText(lastCar.getColor());
            seatEditText.setText(lastCar.getSeatNumString());
            priceEditText.setText(lastCar.getPriceString());
        }
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

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
        // single tap - increment number of seats by 1
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            int initialSeatNum = 0;
            if (!seatEditText.getText().toString().equals("")) {
                initialSeatNum = Integer.parseInt(seatEditText.getText().toString());
            }
            int increasedSeatNum = initialSeatNum + 1;
            seatEditText.setText(Integer.toString(increasedSeatNum));
            return true;
        }

        // double tap - load a default car
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Car defaultCar = new Car("Default", "BMW", 2021, "black", 6, 1000);
            makerEditText.setText(defaultCar.getMaker());
            modelEditText.setText(defaultCar.getModel());
            yearEditText.setText(defaultCar.getYearString());
            colorEditText.setText(defaultCar.getColor());
            seatEditText.setText(defaultCar.getSeatNumString());
            priceEditText.setText(defaultCar.getPriceString());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            // get the initial price value
            float initialPrice = 0;
            if (!priceEditText.getText().toString().equals("")) {
                initialPrice = Float.parseFloat(priceEditText.getText().toString());
            }

            float newPrice = 0;

            // horizontal right to left scroll - increment price by the amount of scroll
            if (distanceX > 0) {
                newPrice = Math.min(initialPrice + Math.abs(distanceX), 5000);
            }
            // horizontal left to right scroll - decrement price by the amount of scroll
            else {
                newPrice = Math.max(initialPrice - Math.abs(distanceX), 0);
            }

            priceEditText.setText(Float.toString(newPrice));
            return true;
        }

        // fling - move activity to the background
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (velocityX > 600 || velocityY > 600) {
                moveTaskToBack(true);
                return true;
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        // long press - clear all fields
        @Override
        public void onLongPress(MotionEvent e) {
            clearAllEditTexts();
            super.onLongPress(e);
        }
    }

    class MyScaleGestureDetector extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            int currentYear = Integer.parseInt(yearEditText.getText().toString());

            float scaling = detector.getScaleFactor();

            if (scaling < 1) {
                currentYear --;
                yearEditText.setText(Integer.toString(currentYear));
            } else if (scaling > 1) {
                currentYear ++;
                yearEditText.setText(Integer.toString(currentYear));
            }

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }
}