package com.example.autoshowroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private EditText makerEditText;
    private EditText modelEditText;
    private EditText yearEditText;
    private EditText colorEditText;
    private EditText seatEditText;
    private EditText priceEditText;
    private EditText addressEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getAllEditTexts();

        Button button = findViewById(R.id.btnAddNewCar);
        button.setOnClickListener(new addNewCarButtonListener());

        Button resetBtn = findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(new resetButtonListener());
    }

    @Override
    protected void onStart(){
        restoreSharedPreferences();
        super.onStart();
    }

    @Override
    protected void onPause(){
        saveSharedPreferences();
        super.onPause();
    }

    private void saveSharedPreferences() {
        SharedPreferences persistentLastCar = getPreferences(0);
        SharedPreferences.Editor editor = persistentLastCar.edit();

        editor.putString(getString(R.string.maker), makerEditText.getText().toString());
        editor.putString(getString(R.string.model), modelEditText.getText().toString());
        editor.putString(getString(R.string.year), yearEditText.getText().toString());
        editor.putString(getString(R.string.color), colorEditText.getText().toString());
        editor.putString(getString(R.string.seats), seatEditText.getText().toString());
        editor.putString(getString(R.string.price), priceEditText.getText().toString());
        editor.putString(getString(R.string.address), addressEditText.getText().toString());

        editor.commit();
    }

    private void restoreSharedPreferences() {
        SharedPreferences persistentLastCar = getPreferences(0);

        String lastMaker = persistentLastCar.getString(getString(R.string.maker), "");
        makerEditText.setText(lastMaker);

        String lastModel = persistentLastCar.getString(getString(R.string.model), "");
        modelEditText.setText(lastModel);

        String lastYear = persistentLastCar.getString(getString(R.string.year), "");
        yearEditText.setText(lastYear);

        String lastColor = persistentLastCar.getString(getString(R.string.color), "");
        colorEditText.setText(lastColor);

        String lastSeat = persistentLastCar.getString(getString(R.string.seats), "");
        seatEditText.setText(lastSeat);

        String lastPrice = persistentLastCar.getString(getString(R.string.price), "");
        priceEditText.setText(lastPrice);

        String lastAddress = persistentLastCar.getString(getString(R.string.address), "");
        addressEditText.setText(lastAddress);
    }

    private void getAllEditTexts() {
        makerEditText = findViewById(R.id.editMaker);
        modelEditText = findViewById(R.id.editModel);
        yearEditText = findViewById(R.id.editYear);
        colorEditText = findViewById(R.id.editColor);
        seatEditText = findViewById(R.id.editSeat);
        priceEditText = findViewById(R.id.editPrice);
        addressEditText = findViewById(R.id.editAddress);
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
        }
    }

    private class resetButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // reset all input
            makerEditText.getText().clear();
            modelEditText.getText().clear();
            yearEditText.getText().clear();
            colorEditText.getText().clear();
            seatEditText.getText().clear();
            priceEditText.getText().clear();
            addressEditText.getText().clear();

        }
    }
}