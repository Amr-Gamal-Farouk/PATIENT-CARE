package com.example.amrgamal.weartracker;

import android.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
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
import com.example.amrgamal.weartracker.fragments.Chat_Fragment;
import com.example.amrgamal.weartracker.fragments.ChatsFragment;
import com.example.amrgamal.weartracker.fragments.User_Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.SinchError;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Home_Activity extends AppCompatActivity implements User_Fragment.OnListFragmentInteractionListener,
        SinchService.StartFailedListener , ServiceConnection {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    FloatingActionButton fab;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private DatabaseReference mCheckType;
    private DatabaseReference mUserName;
    String userName;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String currentUserId;
    String userKey;
    private SinchService.SinchServiceInterface mSinchServiceInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_NETWORK_STATE, android.Manifest.permission.READ_PHONE_STATE},100);
        }

        getApplicationContext().bindService(new Intent(this, SinchService.class), this,
                BIND_AUTO_CREATE);

        //test chat

//        Intent i = new Intent(Home_Activity.this,ChatActivity.class);
//        i.putExtra("user_id","N01b8SnjfpgiuPoHgZRd53XrfWh2");
//        i.putExtra("user_name","samy");
//        startActivity(i);

        HashMap test = new HashMap();
        test.put("test", "message from firebase");

        sendPushToSingleInstance(Home_Activity.this, test, "eWdHs8Ec84w:APA91bGjBNN-T7juj-Rk3MWH9mcgjsDKbM44ZPD7YSeDei6tabYLgDnPkLQyCYKaQA2NtTVZNvtxjXwwWQzx3WaJ9vj2hkUf0hC-MToRa5hFhBoCUEyxUP1zrqtGuI9wxPZxCye-PYa4");
        auth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("loginData", MODE_PRIVATE);
//        editor = pref.edit();
//        String s = pref.getString("email","error");
//        String s2 = pref.getString("password","error");
//        Toast.makeText(this,"Email is "+s+" Pass "+s2,Toast.LENGTH_LONG).show();

        mCheckType = FirebaseDatabase.getInstance().getReference().child("Type");
        mUserName = FirebaseDatabase.getInstance().getReference().child("Users").child("wear");


        //get current user
//        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(Home_Activity.this, Login_Activity.class));
                    finish();
                }
            }
        };

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Home_Activity.this, Insert_User_Activity.class));
            }
        });


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if (position == 1)
                    fab.setVisibility(View.INVISIBLE);
                else
                    fab.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        currentUserId = auth.getCurrentUser().getUid();


        mCheckType.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                if (!String.valueOf(dataSnapshot.getValue()).equals("mobile")) {

                    Toast.makeText(Home_Activity.this, "هذا الايميل خاص بالساعه لا يمكن الدخول من موبايل", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    startActivity(new Intent(Home_Activity.this, Login_Activity.class));
                }
//                else if (!getSinchServiceInterface().isStarted()) {
//                        getSinchServiceInterface().startClient(currentUserId);
//                    }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
            }
        });


    }


    @Override
    public void onBackPressed() {
        try {
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.reg_save, menu);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int selectedItem = item.getItemId();
        Intent i;
        if (selectedItem == R.id.logout) {
            auth.signOut();

            startActivity(new Intent(Home_Activity.this, Login_Activity.class));
            finish();

        }
        // another option

        if (selectedItem == R.id.menu_open_map) {
//            startActivity(new Intent(Home_Activity.this,User_Map_Location.class));

            i = new Intent(Home_Activity.this, User_Map_Location.class);
            i.putExtra("user_id", userKey);
            i.putExtra("current_id", currentUserId);
            startActivity(i);

        }

        if (selectedItem == R.id.menu_drugs_time) {
            i = new Intent(Home_Activity.this, Insert_Drugs_Activity.class);
            i.putExtra("user_id", userKey);
            startActivity(i);
//            startActivity(new Intent(Home_Activity.this,AddDrugs.class));
        }
        if (selectedItem == R.id.menu_drugs_report) {
            i = new Intent(Home_Activity.this, Drugs_Report.class);
            i.putExtra("user_id", userKey);
            i.putExtra("current_id", currentUserId);
            startActivity(i);
        }
        if (selectedItem == R.id.menu_message) {
//            startActivity(new Intent(Home_Activity.this,User_Map_Location.class));
            i = new Intent(Home_Activity.this, ChatActivity.class);
            i.putExtra("user_id", userKey);
            i.putExtra("user_name", userName);
            startActivity(i);
        }
//         if (selectedItem == R.id.menu_video_call) {
////            startActivity(new Intent(Home_Activity.this,User_Map_Location.class));
//
//
//
//            i = new Intent(Home_Activity.this, PlaceActivity.class);
//            i.putExtra("user_id", userKey);
////            i.putExtra("user_name", userName);
//
//
//            startActivity(i);
//        }
        if (selectedItem == R.id.menu_call_user) {

            mUserName.child(userKey).child("phone").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String uri = "tel:" + dataSnapshot.getValue();
                    Log.v("tttttttt",uri);
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    if (ActivityCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new User_Fragment(), "المستخدمين");
        adapter.addFragment(new ChatsFragment(), "المحادثات");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onListClick(View v,String k) {
        userKey = k;
        PopupMenu popupMenu = new PopupMenu(this, v);
        popupMenu.getMenuInflater().inflate(R.menu.user_options, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        popupMenu.show();

        mUserName.child(k).child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                userName = String.valueOf(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
            }
        });


        return false;

    }

    @Override
    public void onStartFailed(SinchError error) {

    }

    @Override
    public void onStarted() {

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
            return mFragmentTitleList.get(position);
        }
    }




    public static void sendPushToSingleInstance(final Context activity, final HashMap dataValue /*your data from the activity*/, final String instanceIdToken /*firebase instance token you will find in documentation that how to get this*/ ) {


        final String url = "https://fcm.googleapis.com/fcm/send";
        StringRequest myReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(activity, "Bingo Success", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                        Toast.makeText(activity, "Oops error", Toast.LENGTH_SHORT).show();
                    }
                }) {

            @Override
            public byte[] getBody() throws com.android.volley.AuthFailureError {
                Map<String, Object> rawParameters = new Hashtable();
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key="+"nLJLkSQiBGVTlYMd6H09PdLGNf02");
//                headers.put("Authorization", "key="+YOUR_LEGACY_SERVER_KEY_FROM_FIREBASE_CONSOLE);
                return headers;
            }

        };

        Volley.newRequestQueue(activity).add(myReq);
    }
}