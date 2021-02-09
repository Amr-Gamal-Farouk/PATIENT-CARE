package com.example.amrgamal.weartracker;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

public class User_Map_Location extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Marker marker;


    String userID ; //"jZnCpjCptvfwXlp5vygVmxOiQ0W2";
    String currentID ;// "nLJLkSQiBGVTlYMd6H09PdLGNf02";
    private DatabaseReference mCheckType;

    String latitude,longitude,place;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__map__location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        userID = getIntent().getStringExtra("user_id");
        currentID = getIntent().getStringExtra("current_id");
        Log.v("aaaaaaaaaaaaaa", userID + "____id");
        Log.v("aaaaaaaaaaaaaa", currentID + "___id");

        try{
            mCheckType = FirebaseDatabase.getInstance().getReference().child("Child").child(currentID).child(userID);

        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }

//        openLocation();
}

    @Override
    public void onBackPressed() {
        try{
            finish();
            startActivity(new Intent( getBaseContext(),Home_Activity.class));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        mCheckType.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                openLocation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void openLocation() {

        mCheckType.child("latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                latitude = String.valueOf(dataSnapshot.getValue());

                mCheckType.child("longitude").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        longitude = String.valueOf(dataSnapshot.getValue());

                        mCheckType.child("adress").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                try {

                                    place = String.valueOf(dataSnapshot.getValue());
                                    Log.v("aaaaaaaaaaaaaa", latitude + "___lat");
                                    Log.v("aaaaaaaaaaaaaa", longitude + "___lat");
                                    Log.v("aaaaaaaaaaaaaa", place + "___lat");


                                    if (marker != null) {
                                        marker.remove();
                                    }
                                    LatLng latlng = new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude));
                                    marker=   mMap.addMarker(new MarkerOptions().position(latlng).title(place));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 18.2f));

                                }catch (Exception e){
                                    Toast.makeText(getBaseContext(), "لم يتم تحديد المكان حتي الان", Toast.LENGTH_SHORT).show();
                                }


                            }

                            @Override
                            public void onCancelled(DatabaseError error) {
                                // Failed to read value
                                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                    }
                });

            }


            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }
}
