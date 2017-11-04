package com.example.adrian.projectcar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Adrian on 04.11.2017.
 */

public class CarPartDetails extends Activity{

    private Button updateButton;
    private Button sendButton;
    private DatabaseReference mDatabase;
    private EditText mCarPartNameField;
    private EditText mEstimatedPriceField;
    private EditText mSellerEmailField;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.part_details);

        Bundle b = getIntent().getExtras();
        String reference = "";
        if(b != null) {
            reference = b.getString("key");
        }


//        mDatabase = FirebaseDatabase.getInstance().getReference();
        sendButton = (Button) findViewById(R.id.send_mail);
        updateButton = (Button) findViewById(R.id.update_changes);
        mCarPartNameField = (EditText) findViewById(R.id.part_name_edit);
        mSellerEmailField = (EditText) findViewById(R.id.part_email_edit);
        mEstimatedPriceField = (EditText) findViewById(R.id.part_price_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Part").child(reference);

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CarPart part = (CarPart) dataSnapshot.getValue(CarPart.class);

                mCarPartNameField.setText(part.getCarPartNameField());
                mSellerEmailField.setText((String)part.getSellerEmailField());
                mEstimatedPriceField.setText(Integer.toString(part.getEstimatedPriceField()));
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {

            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carPartNameField = mCarPartNameField.getText().toString().trim();
                String sellerEmailField = mSellerEmailField.getText().toString().trim();
                String estimatedPriceField = mEstimatedPriceField.getText().toString().trim();

                CarPart carPart = new CarPart();
                carPart.setCarPartNameField(carPartNameField);
                carPart.setEstimatedPriceField(Integer.parseInt(estimatedPriceField));
                carPart.setSellerEmailField(sellerEmailField);

                mDatabase.setValue(carPart).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CarPartDetails.this, "Car part was updated", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(CarPartDetails.this, "Error in updating car part", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String carPartNameField = mCarPartNameField.getText().toString().trim();
                String sellerEmailField = mSellerEmailField.getText().toString().trim();
                String estimatedPriceField = mEstimatedPriceField.getText().toString().trim();

                Intent i = new Intent(Intent.ACTION_SENDTO);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, carPartNameField);
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{sellerEmailField});
                i.putExtra(Intent.EXTRA_TEXT   , "I will offer you "+estimatedPriceField+" lei");

                i.setData(Uri.parse("mailto:"));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CarPartDetails.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
