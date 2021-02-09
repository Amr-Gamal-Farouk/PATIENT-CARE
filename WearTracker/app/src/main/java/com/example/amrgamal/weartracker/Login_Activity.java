package com.example.amrgamal.weartracker;

import android.app.AlertDialog;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import dmax.dialog.SpotsDialog;

public class Login_Activity extends AppCompatActivity {


    TextInputLayout input_login_email,input_login_password;
    EditText edit_login_email,edit_login_password;

    private FirebaseAuth auth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mCheckType;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    AlertDialog dialog ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);

        pref = getSharedPreferences("loginData", MODE_PRIVATE);
        editor = pref.edit();

        dialog = new SpotsDialog(this);

        auth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mCheckType = FirebaseDatabase.getInstance().getReference().child("Type");
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login_Activity.this, Home_Activity.class));
            finish();
        }

        input_login_email=(TextInputLayout) findViewById(R.id.input_login_email);
        input_login_password=(TextInputLayout) findViewById(R.id.input_login_password);

        edit_login_email=(EditText) findViewById(R.id.edit_login_email);
        edit_login_password=(EditText) findViewById(R.id.edit_login_password);


    }

    public void openReg(View view) {
        startActivity(new Intent(Login_Activity.this,Registration_Activity.class));
    }

    public void openHome(View view) {



        final String email = edit_login_email.getText().toString().trim();
        final String password = edit_login_password.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            input_login_email.setError("ادخل الايميل");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            input_login_password.setError("ادخل الباسورد");
            return;
        }

        if (password.length() < 6) {
            input_login_password.setError("الياسورد يجب ان يكون اكثر من 6 احرف");
            return;
        }

        dialog.show();


        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login_Activity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            dialog.dismiss();
                            Toast.makeText(Login_Activity.this,"لا يمكنك الدخول, حاول مره اخري.",Toast.LENGTH_LONG).show();

                        } else {
                            final String currentUserId = auth.getCurrentUser().getUid();


                            mCheckType.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (String.valueOf(dataSnapshot.getValue()).equals("mobile")){

                                        editor.putString("email", email);  // Saving string
                                        editor.putString("password", password);  // Saving string

                                        // Save the changes in SharedPreferences
                                        editor.commit(); // commit changes

                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        mUserDatabase.child("mobile").child(currentUserId).child("token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                                dialog.dismiss();
                                            }
                                        });
                                    }else {
                                        Toast.makeText(Login_Activity.this, "هذا الايميل خاص بالساعه لا يمكن الدخول من موبايل", Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    dialog.dismiss();
                                    Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                                }
                            });

                        }
                    }
                });

    }
}
