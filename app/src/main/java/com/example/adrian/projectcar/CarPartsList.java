package com.example.adrian.projectcar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

public class CarPartsList extends Activity {

    private DatabaseReference mDatabase;
    private ListView mUserList;
    private ArrayList<String> mParts;
    private Button backButton;
    private ArrayAdapter<String> arrayAdapter;
    private FirebaseListAdapter<CarPart> myAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_parts);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mUserList = (ListView) findViewById(R.id.parts_list);
        backButton = (Button) findViewById(R.id.go_back);
        mParts = new ArrayList<String>();

        mDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getUpdates(dataSnapshot);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CarPartsList.this, HelloEverybody.class));
            }
        });

        mUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                DatabaseReference itemRef = myAdapter.getRef(position);
                CarPart part = (CarPart)adapterView.getItemAtPosition(position);
                Toast.makeText(CarPartsList.this, part.getCarPartNameField(), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(Intent.ACTION_SENDTO);
                String email = part.getSellerEmailField();
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, part.getCarPartNameField());
                i.putExtra(Intent.EXTRA_EMAIL, new String[]{"to@email.com"});
                i.putExtra(Intent.EXTRA_TEXT   , "I will offer you "+part.getEstimatedPriceField()+" lei");

                i.setData(Uri.parse("mailto:"));
                try {
                    startActivity(Intent.createChooser(i, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(CarPartsList.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void getUpdates(DataSnapshot ds) {
//        for (DataSnapshot data: ds.getChildren()) {
//            String carpart = data.getValue(CarPart.class).getCarPartNameField();
//            mParts.add(carpart);
//        }
//        if (mParts.size() > 0) {
//            arrayAdapter = new ArrayAdapter<String>(CarPartsList.this, android.R.layout.simple_list_item_1, mParts);
//            mUserList.setAdapter(myAdapter);
//        }
        myAdapter = new FirebaseListAdapter<CarPart>(this,CarPart.class,android.R.layout.simple_list_item_1,mDatabase.child("Part")) {
            @Override
            protected void populateView(View view, CarPart s, int i) {
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setText(s.getCarPartNameField());
            }
        };
        mUserList.setAdapter(myAdapter);


    }
}
