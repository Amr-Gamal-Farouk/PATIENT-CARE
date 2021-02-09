package com.example.amrgamal.testwear;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Login extends AppCompatActivity {


    TextInputLayout input_login_email,input_login_password;
    EditText edit_login_email,edit_login_password;

    private FirebaseAuth auth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mCheckType;
    AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dialog = new SpotsDialog(Login.this);
        auth = FirebaseAuth.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mCheckType = FirebaseDatabase.getInstance().getReference().child("Type");
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(Login.this, HomeActivity.class));
            finish();
        }

        input_login_email=(TextInputLayout) findViewById(R.id.input_login_email);
        input_login_password=(TextInputLayout) findViewById(R.id.input_login_password);

        edit_login_email=(EditText) findViewById(R.id.edit_login_email);
        edit_login_password=(EditText) findViewById(R.id.edit_login_password);

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
        final Button b = (Button) view;
        b.setEnabled(false);
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(Login.this,"لا يمكنك الدخول, حاول مره اخري.",Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            b.setEnabled(true);
                        } else {
                            final String currentUserId = auth.getCurrentUser().getUid();


                            mCheckType.child(currentUserId).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {


                                    if (String.valueOf(dataSnapshot.getValue()).equals("wear")){


                                        String deviceToken = FirebaseInstanceId.getInstance().getToken();

                                        mUserDatabase.child("wear").child(currentUserId).child("token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(Login.this, HomeActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(intent);
                                                finish();
                                            }
                                        });
                                    }else {
                                        Toast.makeText(Login.this, "هذا الايميل خاص بالهاتف لا يمكن الدخول من الساعه", Toast.LENGTH_LONG).show();
                                        auth.signOut();
                                        b.setEnabled(true);
                                        dialog.dismiss();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.e("dsdsdsds", "Failed to read app title value.", error.toException());
                                    b.setEnabled(true);
//                                    dialog.dismiss();
                                }
                            });




                        }
                    }
                });

    }
}
