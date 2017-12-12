package com.example.adrian.projectcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.numetriclabz.numandroidcharts.BarChart;
import com.numetriclabz.numandroidcharts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
//import com.github.mikephil.charting.data.ChartData;
import com.numetriclabz.numandroidcharts.ChartData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Adrian on 04.11.2017.
 */

public class CarPartDetails extends Activity{

    private Button updateButton;
    private Button sendButton;
    private Button deleteButton;
    private Button dateButton;
    private DatabaseReference mDatabase;
    private EditText mCarPartNameField;
    private EditText mEstimatedPriceField;
    private EditText mSellerEmailField;
    private String date = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.part_details);

        LineChart chart = (LineChart) findViewById(R.id.chart);

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(4f, 0f));
        entries.add(new Entry(8f, 1f));
        entries.add(new Entry(6f, 2f));
        entries.add(new Entry(12f, 3f));
        entries.add(new Entry(18f, 4f));
        entries.add(new Entry(9f, 5f));
        LineDataSet dataSet = new LineDataSet(entries, "Label");
        LineData lineData = new LineData(dataSet);
        Description desc = new Description();
        desc.setText("Price compared to budget");
        chart.setDescription(desc);
        chart.setData(lineData);
        chart.invalidate();
        chart.animateXY(3000, 3000);
        Bundle b = getIntent().getExtras();
        String reference = "";
        if(b != null) {
            reference = b.getString("key");
        }


//        mDatabase = FirebaseDatabase.getInstance().getReference();
        sendButton = (Button) findViewById(R.id.send_mail);
        updateButton = (Button) findViewById(R.id.update_changes);
        deleteButton = (Button) findViewById(R.id.delete);
        dateButton = (Button) findViewById(R.id.set_date);
        mCarPartNameField = (EditText) findViewById(R.id.part_name_edit);
        mSellerEmailField = (EditText) findViewById(R.id.part_email_edit);
        mEstimatedPriceField = (EditText) findViewById(R.id.part_price_edit);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Part").child(reference);
//        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

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
                            startActivity(new Intent(CarPartDetails.this, CarPartsList.class));
                        } else {
                            Toast.makeText(CarPartDetails.this, "Error in updating car part", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CarPartDetails.this, "Car part was deleted", Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CarPartDetails.this, CarPartsList.class));
                        } else {
                            Toast.makeText(CarPartDetails.this, "Error in deleting car part", Toast.LENGTH_LONG).show();
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
                if (CarPartDetails.this.date != null) {
                    i.putExtra(Intent.EXTRA_TEXT   , "I will offer you "+estimatedPriceField+" lei on "+CarPartDetails.this.date);
                } else {
                    i.putExtra(Intent.EXTRA_TEXT   , "I will offer you "+estimatedPriceField+" lei");
                }

                i.setData(Uri.parse("mailto:"));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CarPartDetails.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final NumberPicker picker1 = new NumberPicker(CarPartDetails.this);
                picker1.setMinValue(1);
                picker1.setMaxValue(31);
                final NumberPicker picker2 = new NumberPicker(CarPartDetails.this);
                picker2.setMinValue(1);
                picker2.setMaxValue(12);
                final NumberPicker picker3 = new NumberPicker(CarPartDetails.this);
                picker3.setMinValue(2017);
                picker3.setMaxValue(2027);

                final FrameLayout layout = new FrameLayout(CarPartDetails.this);
                layout.addView(picker1, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.LEFT));
                layout.addView(picker2, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER));
                layout.addView(picker3, new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.RIGHT));

                new AlertDialog.Builder(CarPartDetails.this)
                        .setView(layout)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                CarPartDetails.this.date = picker1.getValue() + "-" + picker2.getValue() + "-" + picker3.getValue();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .show();
            }
        });

    }

}
