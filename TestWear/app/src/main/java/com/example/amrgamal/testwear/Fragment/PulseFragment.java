package com.example.amrgamal.testwear.Fragment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.amrgamal.testwear.R;

/**
 * Created by amrga on 05/02/2018.
 */

public class PulseFragment extends Fragment {

//    private TextView mTextView;
//    private Button btnStart;
//    private Button btnPause;
//    private Drawable imgStart;
//
//    private SensorManager mSensorManager;
//    private Sensor mHeartRateSensor;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pulse_fragment, container, false);

//        mTextView = (TextView) view.findViewById(R.id.heartRateText);
//        btnStart = (Button) view.findViewById(R.id.btnStart);
//        btnPause = (Button) view.findViewById(R.id.btnPause);
//
//        btnStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnStart.setVisibility(ImageButton.GONE);
//                btnPause.setVisibility(ImageButton.VISIBLE);
//                mTextView.setText("Please wait...");
//                startMeasure();
//            }
//        });
//
//        btnPause.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                btnPause.setVisibility(ImageButton.GONE);
//                btnStart.setVisibility(ImageButton.VISIBLE);
//                mTextView.setText("--");
//                stopMeasure();
//            }
//        });
//        setAmbientEnabled();
//
//        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
//        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
//
//
//        return view;
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent sensorEvent) {
//
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//
//    }
        return view;
    }
}
