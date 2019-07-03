package com.example.beacontransmitter;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.BeaconTransmitter;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button createButton;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 0x0001;

    private static final String STUDY_IDENTIFIER = "u1s2e3r4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createButton = findViewById(R.id.buttonBeaconCreate);

        // Make sure we have access coarse location enabled, if not, prompt the user to enable it
        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

        //Creating signature for user study...
        String baseUUID = UUID.randomUUID().toString();
        String signForStudy = STUDY_IDENTIFIER+baseUUID.substring(8,baseUUID.length());

        Log.d("demo", "Created ID: "+signForStudy);

        //Transmitting Beacons...
        final Beacon beacon = new Beacon.Builder()
                .setId1(signForStudy)
                .setId2(String.valueOf(0x0001))
                .setId3(String.valueOf(0x0002))
                .setDataFields(Arrays.asList(new Long[]{new Random().nextLong()}))
                .build();

        final BeaconParser beaconParser = new BeaconParser()
                .setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT);



        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BeaconTransmitter beaconTransmitter = new BeaconTransmitter(getApplicationContext(), beaconParser);
                beaconTransmitter.startAdvertising(beacon, new AdvertiseCallback() {
                    @Override
                    public void onStartSuccess(AdvertiseSettings settingsInEffect) {
                        super.onStartSuccess(settingsInEffect);
                        Toast.makeText(getApplicationContext(),"The Beacon started transmitting!!!",Toast.LENGTH_LONG).show();

                        Log.d("demo","Beacon started transmitting: "+beacon.getId1());
//                                +", instnce:"+beacon.getId1().toString());
                    }

                    @Override
                    public void onStartFailure(int errorCode) {
                        super.onStartFailure(errorCode);
                        Toast.makeText(getApplicationContext(),"The Beacon Failed to start!!!",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



    }

}

