package com.example.adrian.projectcar;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class HelloEverybody extends AppCompatActivity {

    private Button addButton;
    private Button viewPartsButton;

    private DatabaseReference mDatabase;

    private EditText mCarPartNameField;
    private EditText mSellerEmailField;
    private EditText mEstimatedPriceField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_everybody);

        addButton = (Button) findViewById(R.id.add_button);
        viewPartsButton = (Button) findViewById(R.id.view_parts_button);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mCarPartNameField = (EditText) findViewById(R.id.car_part_name);
        mSellerEmailField = (EditText) findViewById(R.id.seller_email);
        mEstimatedPriceField = (EditText) findViewById(R.id.car_part_price);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carPartNameField = mCarPartNameField.getText().toString().trim();
                String sellerEmailField = mSellerEmailField.getText().toString().trim();
                String estimatedPriceField = mEstimatedPriceField.getText().toString().trim();

                CarPart carPart = new CarPart();
                carPart.setCarPartNameField(carPartNameField);
                carPart.setEstimatedPriceField(Integer.parseInt(estimatedPriceField));

                mDatabase.child("Part").push().setValue(carPart).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(HelloEverybody.this, "Car part was registered", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HelloEverybody.this, "Error in saving car part", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        viewPartsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelloEverybody.this, CarPartsList.class));
            }
        });
    }
}
