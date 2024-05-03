package com.example.paynow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Button;
import android.widget.TextView;

public class CardDetailsActivity extends AppCompatActivity {

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_details);

        Button goToNewCard = findViewById(R.id.addCard);

        requestQueue = Volley.newRequestQueue(this);

        fetchCardDetails();

        goToNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CardDetailsActivity.this, AddCardActivity.class);
                startActivity(intent);
            }
        });
    }

    private void fetchCardDetails() {

        // Retrieve the URL of the backend endpoint passed from MainActivity
        String apiUrl = getIntent().getStringExtra("API_URL");

        // Retrieve the customer ID passed from MainActivity
        String customerId = getIntent().getStringExtra("CUSTOMER_ID");
        Log.d("CUSTOMER_ID_LOG", "Customer ID: " + customerId);

        // Create a request body with the customer ID
        JSONObject requestBody = new JSONObject();
        try {
            // Add the customer ID to the request body
            requestBody.put("customerId", customerId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Log the requestBody
        Log.d("REQUEST_BODY_LOG", "Request Body: " + requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                apiUrl,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE_LOG", "Response: " + response.toString());
                        handleApiResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                });

        // Add the request to the RequestQueue
        requestQueue.add(jsonObjectRequest);

    }

    private void handleApiResponse(JSONObject jsonObject) {
        try {
            JSONArray cardsArray = jsonObject.getJSONArray("cards");

            // Check if there are any cards available
            if (cardsArray.length() > 0) {
                // Assuming you want to display details of the first card
                JSONObject cardObject = cardsArray.getJSONObject(0).getJSONObject("card");

                // Extract card details
                String cardBrand = cardObject.getString("brand");
                String lastFourDigits = cardObject.getString("last4");

                // Log card details to check if they are retrieved correctly
                Log.d("CARD_DETAILS_LOG", "Card Brand: " + cardBrand);
                Log.d("CARD_DETAILS_LOG", "Last Four Digits: " + lastFourDigits);

                // Set the appropriate card logo
                int cardLogoResourceId;
                if ("visa".equals(cardBrand)) {
                    cardLogoResourceId = R.drawable.visacard_logo;
                } else if ("mastercard".equals(cardBrand)) {
                    cardLogoResourceId = R.drawable.mastercard_logo;
                } else {
                    cardLogoResourceId = R.drawable.default_card_logo; // Use a default logo if brand is unknown
                }
                ImageView imageViewCardLogo = findViewById(R.id.imageViewCardLogo);
                Glide.with(this).load(cardLogoResourceId).into(imageViewCardLogo);

                // Set the last four digits as text
                TextView textViewCardNumber = findViewById(R.id.textViewCardDetails);
                textViewCardNumber.setText("**** **** **** " + lastFourDigits);
            } else {
                Log.e("HANDLE_RESPONSE", "No cards found in response.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSON_EXCEPTION", "Error parsing JSON: " + e.getMessage());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null) {
            requestQueue.cancelAll(this);
        }
    }
}
