package com.example.paynow;

import androidx.appcompat.app.AppCompatActivity;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Button;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;



public class AddCardActivity extends AppCompatActivity {

    private EditText cardHolderNameEditText;
    private EditText cardNumberEditText;
    private EditText expDateEditText;
    private EditText cvvEditText;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        // Initialize EditText fields
        cardHolderNameEditText = findViewById(R.id.cardHolderName);
        cardNumberEditText = findViewById(R.id.cardNumber);
        expDateEditText = findViewById(R.id.expDate);
        cvvEditText = findViewById(R.id.cvv);

        Button sendDataButton = findViewById(R.id.createCard);

        sendDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendDataToBackend();
                Intent intent = new Intent(AddCardActivity.this, PaymentActivity.class);
                startActivity(intent);

            }
        });

    }

    private void sendDataToBackend() {
        String cardHolderName = cardHolderNameEditText.getText().toString();
        String cardNumber = cardNumberEditText.getText().toString();
        String expDate = expDateEditText.getText().toString();
        String cvv = cvvEditText.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("cardHolderName", cardHolderName);
            jsonObject.put("cardNumber", cardNumber);
            jsonObject.put("expDate", expDate);
            jsonObject.put("cvv", cvv);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Call your method to send data using Volley
        apiCall();
    }

    private void apiCall() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://2978-2401-4900-1c29-705d-5814-7254-348-beaf.ngrok-free.app/create-card";

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null,
                response -> {

                    Log.d("VolleyResponse", "Response: " + response.toString());
                },
                error -> {
                    Log.e("VolleyError", "Error: " + error.toString());
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}