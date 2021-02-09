package com.example.amrgamal.testwear;

import android.*;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.amrgamal.testwear.Fragment.ChatFragment;
import com.example.amrgamal.testwear.Fragment.DrugsFragment;
import com.example.amrgamal.testwear.model.Drug_Model;
import com.example.amrgamal.testwear.model.Time;
import com.example.amrgamal.testwear.service.AlarmRecever;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import dmax.dialog.SpotsDialog;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback ,
        SinchService.StartFailedListener,ServiceConnection {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mCheckType;

    private DatabaseReference mPhone;
    private DatabaseReference mName;
    private DatabaseReference mGetParent;

    private DatabaseReference mLocation;
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    String phone;
    String token;
    public static String name;



    AlarmManager alarmManager;
    Intent alarmIntent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
//    int lanSettings;


    LocationManager locationManager;

    public static String parentID;

    private int[] tabIcons = {
            R.drawable.drags,
            R.drawable.chat,
            R.drawable.hart
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.call_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        if (selectedItem == R.id.call) {


            String posted_by = phone;
            Log.v("aaaaaaaaaa", "clicked");
            Log.v("eeeeeeeeee", phone);

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return true;
            }

            String uri = "tel:" + phone.trim();
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse(uri));
            startActivity(intent);


        }
        if (selectedItem == R.id.noti) {
            startActivity(new Intent(HomeActivity.this, NextDrugs.class));
        }
        if (selectedItem == R.id.logout){
            auth.signOut();
            startActivity(new Intent(HomeActivity.this,Login.class));
            finish();
    }
//

        return super.onOptionsItemSelected(item);
    }


    public static void sendPushToSingleInstance(final Context activity,
                                                final HashMap dataValue /*your data from the activity*/,
                                                final String instanceIdToken /*firebase instance token you will find in documentation that how to get this*/ ) {

        FirebaseDatabase database =  FirebaseDatabase.getInstance();

        DatabaseReference mRef =  database.getReference().child("Ding").child(parentID).child(name);
        mRef.setValue(name).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    final String url = "https://fcm.googleapis.com/fcm/send";
                    StringRequest myReq = new StringRequest(Request.Method.POST,url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(activity, "تم ارسال التنبيه", Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(activity, "حدث خطأ اثناء الارسال حاول مره اخري", Toast.LENGTH_SHORT).show();
                                }
                            }) {

                        @Override
                        public byte[] getBody() throws AuthFailureError {
                            Map<String, Object> rawParameters = new Hashtable();
//                rawParameters.put("data", dataValue);
                            rawParameters.put("data", new JSONObject(dataValue));
                            rawParameters.put("to", instanceIdToken);
                            return new JSONObject(rawParameters).toString().getBytes();
                        };

                        public String getBodyContentType()
                        {
                            return "application/json; charset=utf-8";
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            final String apiKey = "AAAAifj2MXg:APA91bFylBWIPRhei-amk9aCOBFZbbV5ArTkaKEBsZIqb8sn0IIdJ60OxgEpkOfE_xocFgA8BobXztfDvuJocCL5TUiM-ztRene6BmW8zqt2v1u0ngqmuWvqUKS9sMkXRmssSxWypztb";
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Authorization", "key="+apiKey);
                            return headers;
                        }

                    };

                    Volley.newRequestQueue(activity).add(myReq);

                }
            }
        });
}
    public  String send(String to,  String body) {
        try {

            final String apiKey = "AAAAifj2MXg:APA91bFylBWIPRhei-amk9aCOBFZbbV5ArTkaKEBsZIqb8sn0IIdJ60OxgEpkOfE_xocFgA8BobXztfDvuJocCL5TUiM-ztRene6BmW8zqt2v1u0ngqmuWvqUKS9sMkXRmssSxWypztb";
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "key=" + apiKey);
            conn.setDoOutput(true);
            JSONObject message = new JSONObject();
            message.put("to", to);
            message.put("priority", "high");

            JSONObject notification = new JSONObject();
            // notification.put("title", title);
            notification.put("body", body);
            message.put("data", notification);
            OutputStream os = conn.getOutputStream();
            os.write(message.toString().getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + message.toString());
            System.out.println("Response Code : " + responseCode);
            System.out.println("Response Code : " + conn.getResponseMessage());

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // print result
            System.out.println(response.toString());
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            Log.v("tttttttt",e.toString()+"");
        }
        return "error";
    }

    String currentUserId;
    Calendar c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE},100);
        }
        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

        mCheckType = FirebaseDatabase.getInstance().getReference().child("Type");
        mPhone = FirebaseDatabase.getInstance().getReference().child("Users").child("mobile");
        mName = FirebaseDatabase.getInstance().getReference().child("Users").child("wear");

        mGetParent = FirebaseDatabase.getInstance().getReference().child("Parent");

        auth = FirebaseAuth.getInstance();

        //get current user
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(HomeActivity.this, Login.class));
                    finish();
                }
            }
        };

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        currentUserId = auth.getCurrentUser().getUid();

        mCheckType.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!String.valueOf(dataSnapshot.getValue()).equals("wear")) {

                    Toast.makeText(HomeActivity.this, "هذا الايميل خاص بالهاتف لا يمكن الدخول من الساعه", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    startActivity(new Intent(HomeActivity.this, Login.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
            }
        });
        setupTabIcons();



        sharedpreferences = getSharedPreferences("check", Context.MODE_PRIVATE);
        editor= sharedpreferences.edit();



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs").child(currentUserId);
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.v("aaaaaaaaaaaa",currentUserId);
                updateNotification();

                Log.v("aaaaaaaaaaaa","add");

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.v("aaaaaaaaaaaa",currentUserId);
                updateNotification();

                Log.v("aaaaaaaaaaaa","change");

//                Toast.makeText(HomeActivity.this,"success update",Toast.LENGTH_LONG).show();

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.v("aaaaaaaaaaaa",currentUserId);

                Log.v("aaaaaaaaaaaa","remove");
                updateNotification();


            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.v("aaaaaaaaaaaa",currentUserId);

                Log.v("aaaaaaaaaaaa","move");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.v("aaaaaaaaaaaa",currentUserId);

                Log.v("aaaaaaaaaaaa","canceled");

            }
        });




        mGetParent.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {



                parentID = String.valueOf(dataSnapshot.getValue());
                Log.v("aaaaaa",parentID+"____h");
                mLocation = FirebaseDatabase.getInstance().getReference().child("Child").child(parentID);

                mPhone.child(parentID).child("phone").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        phone  = String.valueOf(dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                        if (!getSinchServiceInterface().isStarted()) {
                            getSinchServiceInterface().startClient(currentUserId);
                        }
                    }
                });


                mName.child(currentUserId).child("name").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {



                        name  = String.valueOf(dataSnapshot.getValue().toString());

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                    }
                });
                mPhone.child(parentID).child("token").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        token  = String.valueOf(dataSnapshot.getValue());

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                   }
                });


        /////////////////////////////////// Location

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission
                (HomeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        markerOptions = new MarkerOptions();
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 100, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    // get latitude
                    double latitude=location.getLatitude();
                    // get longitude
                    final double longitude=location.getLongitude();
                    // instantiate class LatLng
                    LatLng latlng=new LatLng(latitude,longitude);
                    // instantiate class Geocoder
                    Geocoder geocoder=new Geocoder(getApplicationContext());

                    try {
                        List<Address> addressList=geocoder.getFromLocation(latitude,longitude,1);
                        String string=addressList.get(0).getLocality()+"," ;
                        string+=addressList.get(0).getCountryName();

                        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+3:00"));
                        Date currentLocalTime = cal.getTime();
                        DateFormat date = new SimpleDateFormat("HH:mm a");
                        date.setTimeZone(TimeZone.getTimeZone("GMT+3:00"));
                        String localTime = date.format(currentLocalTime);

                        string=string+" "+ localTime;

                        Log.v("fffffffffff",latlng+"");
                        Log.v("fffffffffff",string);



                        mLocation.child(currentUserId).child("adress").setValue(string).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });

                        mLocation.child(currentUserId).child("latitude").setValue(latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        mLocation.child(currentUserId).child("longitude").setValue(longitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                        mLocation.child(currentUserId).child("time").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });


//                        m.remove();

//                        googleMap.addMarker(markerOptions.position(latlng).title(string));
//                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng,18.2f));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            });

        }

    }

    @Override
    public void onCancelled(DatabaseError error) {
        // Failed to read value
        Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
    }
});








    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new DrugsFragment(), "الدواء");
        adapter.addFragment(new ChatFragment(), "المحادثات");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    public void dangBTN(View view) {

        HashMap<String, String> hmap = new HashMap<String , String>();

            hmap.put("to", "aaaaaaaaaa");
            hmap.put("body", name);

        sendPushToSingleInstance(HomeActivity.this,hmap,
                token);

    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = (SinchService.SinchServiceInterface) iBinder;
            onServiceConnected();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        if (SinchService.class.getName().equals(componentName.getClassName())) {
            mSinchServiceInterface = null;
            onServiceDisconnected();
        }
    }

    protected void onServiceConnected() {
        // for subclasses
    }

    protected void onServiceDisconnected() {
        // for subclasses
    }

    protected SinchService.SinchServiceInterface getSinchServiceInterface() {
        return mSinchServiceInterface;
    }


    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

    }


    class ViewPagerAdapter extends FragmentPagerAdapter {
            private final List<Fragment> mFragmentList = new ArrayList<>();
            private final List<String> mFragmentTitleList = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager) {

                super(manager);
            }

            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            public void addFragment(Fragment fragment, String title) {
                mFragmentList.add(fragment);
                mFragmentTitleList.add(title);
            }



            @Override
            public CharSequence getPageTitle(int position) {
                return null;
            }

    }


int nKey=0;
    private void collectUserData(Map<String,Object> users) {

        for (final Map.Entry<String, Object> entry : users.entrySet()){
            final String time="";
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Drugs")
                    .child(currentUserId).child(entry.getKey());

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.v("lllllllllllllllll", dataSnapshot.getKey()+"");
                    Log.v("lllllllllllllllll", dataSnapshot.getValue().toString()+"");
                    Log.v("lllllllllllllllll", dataSnapshot.getValue().getClass()+"");
                    Log.v("xxxxxxxxxxxxxx", "after");

                    GenericTypeIndicator<List<Time>> t = new GenericTypeIndicator<List<Time>>() {};
                    List<Time> questionList = dataSnapshot.getValue(t);
                    Log.v("xxxxxxxxxxxxxx", questionList.size()+"");
                    Log.v("xxxxxxxxxxxxxx", "before");

                    for (int i = 0 ; i < questionList.size() ; i++){
                        Log.v("zzzzzzzz", questionList.get(i).getMinute()+" Minute");
                        Log.v("zzzzzzzz", questionList.get(i).getHour()+" Hour");


                        openNotification(questionList.get(i).getHour(),questionList.get(i).getMinute(),nKey);
                            nKey++;

                        Log.v("ccccccccccccc","a");


                    }


                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }



    }


    private void openNotification(int h,int m,int k) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, h);
        calendar.set(Calendar.MINUTE, m);

        Log.v("mmmmmmmmmmm","hour = "+h+" , "+"minute = "+m);

        alarmIntent = new Intent(getApplicationContext(), AlarmRecever.class);




        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,
                PendingIntent.getBroadcast(getApplicationContext(), k, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    protected void onStart() {
        super.onStart();

        updateNotification();
    }


    private void updateNotification() {

        DatabaseReference reference2 = FirebaseDatabase.getInstance().getReference("Drugs").child(currentUserId);
        reference2.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try{



                        String data = sharedpreferences.getString("time","");
                        if (!dataSnapshot.getValue().toString().equals(data)){

                            try {
                                Log.v("llllllllllll","done");
                                for (int i = 0 ; i <1000;i++){
                                    alarmIntent = new Intent(getApplicationContext(), AlarmRecever.class);
                                    alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                    alarmManager.cancel(PendingIntent.getBroadcast(getApplicationContext(), i, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT));

                                }
                            }catch (Exception e){

                            }



                            editor.putString("time", dataSnapshot.getValue().toString());
                            collectUserData((Map<String,Object>) dataSnapshot.getValue());
                        }

                        }catch (Exception e){

                        }
                            Log.v("oooooooooooooo","done");

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
