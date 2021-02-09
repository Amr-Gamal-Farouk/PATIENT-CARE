package com.example.amrgamal.weartracker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class Insert_User_Activity extends AppCompatActivity {

    TextInputLayout input_insert_name,input_insert_email,input_insert_phone,input_insert_password;
    EditText edit_insert_name,edit_insert_phone,edit_insert_email,edit_insert_password;

    private DatabaseReference mDatabase;
    private DatabaseReference mDeviceType;
    private DatabaseReference mParent,mChild;

    private FirebaseAuth auth;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String email,password;
    String uEmail,uPassword;

    String mID,wID;
    String name_first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert__user_);

        auth = FirebaseAuth.getInstance();
//        pref = getApplicationContext().getSharedPreferences("loginData", MODE_PRIVATE);
//        editor = pref.edit();

        pref = getSharedPreferences("loginData", MODE_PRIVATE);
//        editor = pref.edit();
        uEmail = pref.getString("email","error");
        uPassword = pref.getString("password","error");

        input_insert_name = (TextInputLayout) findViewById(R.id.input_insert_name);
        input_insert_email = (TextInputLayout) findViewById(R.id.input_insert_email);
        input_insert_phone = (TextInputLayout) findViewById(R.id.input_insert_phone);
        input_insert_password = (TextInputLayout) findViewById(R.id.input_insert_password);

        edit_insert_name = (EditText) findViewById(R.id.edit_insert_name);
        edit_insert_phone = (EditText) findViewById(R.id.edit_insert_phone);
        edit_insert_email = (EditText) findViewById(R.id.edit_insert_email);
        edit_insert_password = (EditText) findViewById(R.id.edit_insert_password);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        mID = currentUser.getUid();
//        email=pref.getString("email", null);         // getting String
//        password=pref.getString("password", null);         // getting String

    }

    public void addNewUser(View view) {


        email = edit_insert_email.getText().toString().trim();
        password = edit_insert_password.getText().toString().trim();
        final String name = edit_insert_name.getText().toString().trim();
        final String phone = edit_insert_phone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            input_insert_name.setError("ادخل الاسم");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            input_insert_phone.setError("ادخل رقم التليفون");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            input_insert_email.setError("ادخل الايميل");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            input_insert_password.setError("ادخل الباسورد");
            return;
        }

        if (password.length() < 6) {
            input_insert_password.setError("الياسورد يجب ان يكون اكثر من 6 احرف");
            return;
        }
        auth.signOut();

        try {

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(Insert_User_Activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            Toast.makeText(Insert_User_Activity.this, "before if", Toast.LENGTH_LONG).show();
                            if (task.isSuccessful()) {

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                String uid = currentUser.getUid();


                                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("wear").child(uid);
                                mDeviceType = FirebaseDatabase.getInstance().getReference().child("Type").child(uid);

                                String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                HashMap<String, String> userMap = new HashMap<>();
                                userMap.put("name", name);
                                userMap.put("phone", phone);
                                userMap.put("token", deviceToken);

                                Toast.makeText(Insert_User_Activity.this, "جاري اضافه شخص جديد...", Toast.LENGTH_SHORT).show();

                                mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            mDeviceType.setValue("wear").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Insert_User_Activity.this, "تم اضافه مستخدم بنجاح.", Toast.LENGTH_LONG).show();
                                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                                    wID = currentUser.getUid();
                                                    name_first = name;
                                                }
                                            });


                                        } else {
                                            Toast.makeText(Insert_User_Activity.this, "لا يمكنك اضافه الحساب, حاول مره اخري.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(Insert_User_Activity.this, "لا يمكنك اضافه الحساب, حاول مره اخري.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }catch (Exception e){
            Toast.makeText(this ," هناك خطأ ما حاول مره اخري ",Toast.LENGTH_LONG).show();
        }

        auth.signOut();

        if (uPassword.equals("errer")||uEmail.equals("errer")){
            editor.putString("email", "errer");  // Saving string
            editor.putString("password", "errer");  // Saving string

            startActivity(new Intent(Insert_User_Activity.this,Login_Activity.class));
        }else {

            auth.signInWithEmailAndPassword(uEmail, uPassword)
                    .addOnCompleteListener(Insert_User_Activity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                // there was an error
                                startActivity(new Intent(Insert_User_Activity.this, Login_Activity.class));
                                editor.putString("email", "errer");  // Saving string
                                editor.putString("password", "errer");  // Saving string

                            } else{

                                mParent = FirebaseDatabase.getInstance().getReference().child("Parent").child(wID);
                                mChild = FirebaseDatabase.getInstance().getReference().child("Child").child(mID).child(wID);

                                mParent.setValue(mID).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        mChild.child("adress").setValue("لم يتم تحديد المكان حتي الان").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        mChild.child("time").setValue(ServerValue.TIMESTAMP).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        mChild.child("name").setValue(name_first).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        mChild.child("latitude").setValue("l").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });
                                        mChild.child("longitude").setValue("l").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });


                                    }
                                });

                                startActivity(new Intent(Insert_User_Activity.this,Home_Activity.class));
                                finish();
                            }


                        }

                    });
        }
    }
}
