package com.example.paynow;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToPaymentMethodButton = findViewById(R.id.payButton);
        Button goToCardPayment = findViewById(R.id.cardButton);

        goToPaymentMethodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String backendUrl = "https://669a-2401-4900-1c29-3a5-90ab-bbf0-ce51-e448.ngrok-free.app/create-payment-intent"; // Replace with your actual URL
                Intent intent = new Intent(MainActivity.this, PaymentMethodActivity.class);
                intent.putExtra("BACKEND_URL", backendUrl);
                startActivity(intent);
            }
        });

        goToCardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apiUrl = "https://2978-2401-4900-1c29-705d-5814-7254-348-beaf.ngrok-free.app/customer-cards";
                String CustomerId = "cus_PnuOphxSbYxAtB";
                Intent intent = new Intent(MainActivity.this, CardDetailsActivity.class);
                intent.putExtra("API_URL", apiUrl);
                intent.putExtra("CUSTOMER_ID",CustomerId );
                startActivity(intent);
            }
        });
    }
}