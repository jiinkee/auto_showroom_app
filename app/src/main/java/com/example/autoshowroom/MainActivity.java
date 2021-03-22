package com.example.autoshowroom;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    private EditText makerEditText, modelEditText, yearEditText, colorEditText, seatEditText, priceEditText;

    private final static int NULL_INT_INPUT = -1;
    private final static float NULL_FLOAT_INPUT = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllEditTexts();

        // add car to showroom and save car details to SharedPreferences
        Button button = findViewById(R.id.btnAddNewCar);
        button.setOnClickListener(new addNewCarButtonListener());

        // wipe off data in EditTexts and SharedPreferences
        Button clearBtn = findViewById(R.id.btnClear);
        clearBtn.setOnClickListener(new clearButtonListener());

        // Request permissions to access SMS
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS,
        Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 0);

        // register broadcast receiver to tokenize an display values in SMS
        IntentFilter smsTokenizeIntentFilter = new IntentFilter(SMSReceiver.SMS_TOKENIZE);
        BroadcastReceiver smsTokenizeReceiver = new SMSTokenizeReceiver();
        registerReceiver(smsTokenizeReceiver, smsTokenizeIntentFilter);

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
            seatEditText.setText(seats);
            priceEditText.setText(price);
        }
    }

    @Override
    protected void onStart() {
        restoreSharedPreferences();
        super.onStart();
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

    private class addNewCarButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // hide keyboard
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            // show toaster
            String makerInput = makerEditText.getText().toString();
            Toast.makeText(MainActivity.this, "We added a new car (" + makerInput + ")", Toast.LENGTH_SHORT).show();

            // save user inputs into SharedPreferences file
            saveSharedPreferences();
        }
    }

    private class clearButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            clearAllEditTexts();
            clearSharedPreferences();
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