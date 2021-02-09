package com.example.amrgamal.weartracker;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import dmax.dialog.SpotsDialog;

public class Registration_Activity extends AppCompatActivity {

    TextInputLayout input_reg_name,input_reg_email,input_reg_phone,input_reg_password;
    EditText edit_reg_name,edit_reg_email,edit_reg_phone,edit_reg_password;
    private FirebaseAuth auth;

    private DatabaseReference mDatabase;
    private DatabaseReference mDeviceType;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    AlertDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_);
        dialog = new SpotsDialog(this);

        auth = FirebaseAuth.getInstance();
        pref = getSharedPreferences("loginData", MODE_PRIVATE);
        editor = pref.edit();

        input_reg_name = (TextInputLayout) findViewById(R.id.input_reg_name);
        input_reg_email = (TextInputLayout) findViewById(R.id.input_reg_email);
        input_reg_phone = (TextInputLayout) findViewById(R.id.input_reg_phone);
        input_reg_password = (TextInputLayout) findViewById(R.id.input_reg_password);

        edit_reg_name = (EditText) findViewById(R.id.edit_reg_name);
        edit_reg_email = (EditText) findViewById(R.id.edit_reg_email);
        edit_reg_phone = (EditText) findViewById(R.id.edit_reg_phone);
        edit_reg_password = (EditText) findViewById(R.id.edit_reg_password);


    }


    public void regMethod(View view) {
        final String email = edit_reg_email.getText().toString().trim();
        final String password = edit_reg_password.getText().toString().trim();
        final String name = edit_reg_name.getText().toString().trim();
        final String phone = edit_reg_phone.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            input_reg_name.setError("ادخل الاسم");
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            input_reg_phone.setError("ادخل رقم التليفون");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            input_reg_email.setError("ادخل الايميل");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            input_reg_password.setError("ادخل الباسورد");
            return;
        }

        if (password.length() < 6) {
            input_reg_password.setError("الياسورد يجب ان يكون اكثر من 6 احرف");
            return;
        }
//        dialog.show();

//        editor.putString("email", email);  // Saving string
//        editor.putString("password", password);  // Saving string
//
//// Save the changes in SharedPreferences
//        editor.commit(); // commit changes



        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Registration_Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {

                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = currentUser.getUid();

                            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child("mobile").child(uid);
                            mDeviceType = FirebaseDatabase.getInstance().getReference().child("Type").child(uid);

                            String deviceToken = FirebaseInstanceId.getInstance().getToken();

                            HashMap<String,String> userMap = new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("phone",phone);
                            userMap.put("token",deviceToken);

                            mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        mDeviceType.setValue("mobile").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                editor.putString("email", email);  // Saving string
                                                editor.putString("password", password);  // Saving string

                                                // Save the changes in SharedPreferences
                                                editor.commit(); // commit changes
//                                                dialog.dismiss();

                                                startActivity(new Intent(Registration_Activity.this, Home_Activity.class));
                                                finish();
                                            }
                                        });


                                    }else {
                                        Toast.makeText(Registration_Activity.this,"لا يمكنك عمل الحساب, حاول مره اخري.",Toast.LENGTH_LONG).show();
//                                        dialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });


    }
}
