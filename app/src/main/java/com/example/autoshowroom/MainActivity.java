package com.example.autoshowroom;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.btnAddNewCar);
        button.setOnClickListener(new addNewCarButtonListener());

        Button resetBtn = findViewById(R.id.btnReset);
        resetBtn.setOnClickListener(new resetButtonListener());
    }

    private class addNewCarButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // hide keyboard
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            // show toaster
            EditText makerEditText = findViewById(R.id.editMaker);
            String makerInput = makerEditText.getText().toString();

            Toast.makeText(MainActivity.this, "We added a new car (" + makerInput + ")", Toast.LENGTH_SHORT).show();
        }
    }

    private class resetButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            // reset all input
            EditText makerEditText = findViewById(R.id.editMaker);
            makerEditText.getText().clear();

            EditText modelEditText = findViewById(R.id.editModel);
            modelEditText.getText().clear();

            EditText yearEditText = findViewById(R.id.editYear);
            yearEditText.getText().clear();

            EditText colorEditText = findViewById(R.id.editColor);
            colorEditText.getText().clear();

            EditText seatEditText = findViewById(R.id.editSeat);
            seatEditText.getText().clear();

            EditText priceEditText = findViewById(R.id.editPrice);
            priceEditText.getText().clear();

            EditText addressEditText = findViewById(R.id.editAddress);
            addressEditText.getText().clear();

        }
    }
}