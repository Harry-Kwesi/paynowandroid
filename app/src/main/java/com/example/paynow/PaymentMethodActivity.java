package com.example.paynow;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class PaymentMethodActivity extends AppCompatActivity {

    private ImageView qrCodeImageView;
    private RequestQueue requestQueue;

    private TextView countdownTimerTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);

        qrCodeImageView = findViewById(R.id.qrCodeImage);
        countdownTimerTextView = findViewById(R.id.countdownTimerTextView);

        // Instantiate the RequestQueue.
        requestQueue = Volley.newRequestQueue(this);

        // Fetch and display QR code image
        fetchAndDisplayQRCode();
    }

    private void fetchAndDisplayQRCode() {
        // Retrieve the URL of the backend endpoint passed from MainActivity
        String backendUrl = getIntent().getStringExtra("BACKEND_URL");
        Log.d("BackendURL", "Backend URL: " + backendUrl);

        // Create a request body with the amount
        JSONObject requestBody = new JSONObject();
        try {
            // Add the amount to the request body
            requestBody.put("amount", 20000);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Create a request to fetch the JSON response from the backend endpoint using POST method
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                backendUrl,
                requestBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response", "JSON response received: " + response.toString());
                        try {
                            // Extract the amount from the JSON response
                            int amount = response.getJSONObject("confirmed_payment_intent_data").getInt("amount");

                            // Display the amount
                            TextView amountTextView = findViewById(R.id.amountTextView);
                            amountTextView.setText(String.valueOf(amount));

                            // Extract the image URL from the JSON response
                            JSONObject nextAction = response.getJSONObject("confirmed_payment_intent_data").getJSONObject("next_action");
                            JSONObject paynowDisplayQRCode = nextAction.getJSONObject("paynow_display_qr_code");
                            String imageUrl = paynowDisplayQRCode.getString("image_url_png");

                            // Start countdown timer
                            long timerDurationMillis = 2 * 60 * 60 * 1000; // 2 hours in milliseconds
                            startCountdownTimer(timerDurationMillis);


                            // Create a request for the QR code image
                            ImageRequest imageRequest = new ImageRequest(
                                    imageUrl,
                                    new Response.Listener<Bitmap>() {
                                        @Override
                                        public void onResponse(Bitmap response) {
                                            qrCodeImageView.setImageBitmap(response);
                                        }
                                    },
                                    0,
                                    0,
                                    null,
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("Volley Error", "Failed to fetch QR code image: " + error.getMessage());
                                            Toast.makeText(PaymentMethodActivity.this, "Failed to load QR code", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                            );

                            // Add the request to the RequestQueue.
                            requestQueue.add(imageRequest);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(PaymentMethodActivity.this, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", "Failed to fetch JSON response from backend: " + error.getMessage());
                        Toast.makeText(PaymentMethodActivity.this, "Failed to fetch JSON response from backend", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);
    }

    // Countdown timer method
    private void startCountdownTimer(long milliseconds) {
        new CountDownTimer(milliseconds, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update the TextView with the remaining time
                long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(hours);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String timerText = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
                countdownTimerTextView.setText(timerText);
            }

            public void onFinish() {
                countdownTimerTextView.setText("Expired");
                // Handle timer expiration here
            }
        }.start();
    }


    public void saveQRCodeToGallery(View view) {
        // Get the bitmap from the ImageView
        BitmapDrawable bitmapDrawable = (BitmapDrawable) qrCodeImageView.getDrawable();
        if (bitmapDrawable != null) {
            Bitmap bitmap = bitmapDrawable.getBitmap();

            // Save the bitmap to the gallery
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "QR_Code");
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

            if (imageUri != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(imageUri)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    Toast.makeText(this, "QR code saved to gallery", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to create image file", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No QR code to save", Toast.LENGTH_SHORT).show();
        }
    }


}

