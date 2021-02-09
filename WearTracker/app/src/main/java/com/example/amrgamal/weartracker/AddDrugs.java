package com.example.amrgamal.weartracker;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.amrgamal.weartracker.models.Time;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

public class AddDrugs extends AppCompatActivity {

    EditText editDrugsName;
    Button addDrugsTime,deleteDrugsTimes,drugsSave,addImageBu;
    TextView drugsTimeResult;
    private DatabaseReference mDatabase;
    private DatabaseReference myRef;
    private StorageReference mStorageRef;
    List<Time> times ;

    String userID;
    public Uri filePath;
    String drugName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_drugs);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        editDrugsName = (EditText) findViewById(R.id.editDrugsName);
        addDrugsTime = (Button) findViewById(R.id.addDrugsTime);
        deleteDrugsTimes = (Button) findViewById(R.id.deleteDrugsTimes);
        addImageBu=(Button) findViewById(R.id.addImageBu);
        drugsSave = (Button) findViewById(R.id.drugsSave);
        drugsTimeResult = (TextView) findViewById(R.id.drugsTimeResult);
        drugsTimeResult = (TextView) findViewById(R.id.drugsTimeResult);

        times = new ArrayList<>();
        userID = getIntent().getStringExtra("user_id");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Drugs").child(userID);
         myRef = FirebaseDatabase.getInstance().getReference().child("Images").child(userID);


        addDrugsTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(1);
            }
        });


        addImageBu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent galleryIntent = new Intent();
                    galleryIntent.setType("image/*");
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), 1);

            }
        });







        deleteDrugsTimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                times.clear();
                checkTime();
            }
        });

        drugsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drugName = editDrugsName.getText().toString().trim();
                if (!TextUtils.isEmpty(drugName)) {

                    mDatabase.child(drugName).setValue(times).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                try {

                                   uploadImage();
                                    times.clear();

                                }catch (Exception e){}


                                Toast.makeText(AddDrugs.this, "تم اضافه الدواء", Toast.LENGTH_LONG).show();
                                editDrugsName.setText("");
                                drugsTimeResult.setText("لم يتم ادخال ميعاد");


                            } else {
                                Toast.makeText(AddDrugs.this, "لم يتم اضافه الدواء حاول مره اخري", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 1 && resultCode == RESULT_OK){

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }


        }

    }


    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar c = Calendar.getInstance();
        if (id == 1)

            return new TimePickerDialog(AddDrugs.this,TimeMap,c.get(Calendar.HOUR_OF_DAY),c.get(Calendar.MINUTE),false);

            return null;
    }

    Time time;
    Calendar calendar;
    protected TimePickerDialog.OnTimeSetListener TimeMap =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    calendar = Calendar.getInstance();


                    time = new Time();

                    time.setMinute(minute);
                    time.setHour(hourOfDay);

                    times.add(time);

                    checkTime();
                }
            };
String drugsTime = "مواعيد الدواء\n";
    private void checkTime() {
        if (times.size()> 0){

            drugsTime = "مواعيد الدواء\n";

            for (int i = 0; i < times.size();i++){
                time = times.get(i);

                calendar.set(Calendar.HOUR_OF_DAY, time.getHour());
                calendar.set(Calendar.MINUTE, time.getMinute());

                SimpleDateFormat format = new SimpleDateFormat("hh:mm a");
                drugsTime +=format.format(calendar.getTime());
                drugsTime += "\n";

            }
            drugsTimeResult.setText(drugsTime);

        }else {
            drugsTime = "مواعيد الدواء\n";
            drugsTimeResult.setText("لم يتم ادخال ميعاد");
        }
    }

        private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = mStorageRef.child(userID).child(drugName).child(UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                           Uri uri=taskSnapshot.getDownloadUrl();
                            myRef.child(drugName).setValue(uri.toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {
                                                    }
                                            else {
                                                Log.v("aaaaaaaaaaaaaa","_error");
                                            }


                                        }
                                    });


                            progressDialog.dismiss();
                            Toast.makeText(AddDrugs.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            filePath=null

;
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(AddDrugs.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });


        }
    }




}
