package com.example.paynow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentActivity extends AppCompatActivity {

    Button buttonProceedPayment; // Declaration

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // Initialization
        buttonProceedPayment = findViewById(R.id.buttonProceedPayment);

        // Handle button click
        buttonProceedPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform payment processing here
                proceedWithPayment();
            }
        });
    }

    private void proceedWithPayment() {
        // URL of your backend endpoint
        String url = "https://2978-2401-4900-1c29-705d-5814-7254-348-beaf.ngrok-free.app/create-payment";

        // Initialize a new JSONObject to hold your payload (if any)
        JSONObject payload = new JSONObject();

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create the JsonObjectRequest with POST method
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                payload,  // Pass your payload here, if needed
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle successful response
                        // For example, show a confirmation message
                        Toast.makeText(PaymentActivity.this, "Payment successful", Toast.LENGTH_SHORT).show();
                        // Redirect to MainActivity
                        startActivity(new Intent(PaymentActivity.this, MainActivity.class));
                        // Finish the current activity to prevent user from coming back to this screen
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors
                        // For example, show an error message
                        Toast.makeText(PaymentActivity.this, "Error occurred while processing payment", Toast.LENGTH_SHORT).show();
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }
}
