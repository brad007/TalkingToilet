package com.software.fire.talkingtoilet.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.software.fire.talkingtoilet.R;
import com.software.fire.talkingtoilet.model.StatsModel;
import com.software.fire.talkingtoilet.model.TalkingToiletModel;
import com.software.fire.talkingtoilet.utils.Constants;
import com.software.fire.talkingtoilet.utils.Utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private RadioButton mCrumpleRB;
    private RadioButton mFoldRB;
    private EditText mThinkingET;
    private boolean isCrumpled;
    private double mLongitude;
    private double mLatitude;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


        initialiseView();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgressDialog();
                String thoughts = mThinkingET.getText().toString();

                String UID = Utils.getUID();
                TalkingToiletModel model = new TalkingToiletModel();

                model.setIsCrumpled(isCrumpled + "");
                model.setThoughts(thoughts);
                model.setUid(UID);

                DatabaseReference talkingToiletRef = FirebaseDatabase.getInstance()
                        .getReference(Constants.TALKING_TOILET).child(UID);


                talkingToiletRef.setValue(model)
                        .addOnSuccessListener(MainActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                pd.hide();
                                startActivity(new Intent(MainActivity.this, ViewResultsActivity.class));
                            }
                        }).addOnFailureListener(MainActivity.this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Unable to send information", Toast.LENGTH_SHORT).show();
                    }
                });

                updateStats();
                updateLocationStats();


            }
        });
    }

    private void showProgressDialog() {
        pd = new ProgressDialog(MainActivity.this);
        pd.setMessage("Calculating Results");
        pd.setCancelable(false);
        pd.show();
    }

    private void updateStats() {
        DatabaseReference statsRef = FirebaseDatabase.getInstance()
                .getReference(Constants.STATS);
        statsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                StatsModel model = mutableData.getValue(StatsModel.class);
                if (model == null) {
                    model = new StatsModel();
                    model.setNumberOfParticipants(1);
                    if (isCrumpled) {
                        model.setNumberOfCrumpled(1);
                        model.setNumberOfFolded(0);
                    } else {
                        model.setNumberOfCrumpled(0);
                        model.setNumberOfFolded(1);
                    }
                    mutableData.setValue(model);
                    return Transaction.success(mutableData);
                }

                if (isCrumpled) {
                    model.setNumberOfCrumpled(model.getNumberOfCrumpled() + 1);
                } else {
                    model.setNumberOfFolded(model.getNumberOfFolded() + 1);
                }
                model.setNumberOfParticipants(model.getNumberOfParticipants() + 1);

                mutableData.setValue(model);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private void updateLocationStats() {
        DatabaseReference locationStatsRef = FirebaseDatabase.getInstance()
                .getReference(Constants.LOCATION_STATS)
                .child(getAddress(mLatitude, mLongitude));

        locationStatsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Long count = (Long) mutableData.getValue();
                if (count == null) {
                    count = new Long(1);
                    mutableData.setValue(count);
                    return Transaction.success(mutableData);
                }

                mutableData.setValue(count + 1);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }

    private String getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String address = "";
            if (addresses.get(0) != null) {
                address = addresses.get(0).getAddressLine(0);
            }
            String city = addresses.get(0).getLocality();
            return address + ", " + city;
        } catch (IOException e) {
            Log.v("MainActivity", e.getLocalizedMessage());
            return null;
        }
    }


    private void initialiseView() {
        mCrumpleRB = (RadioButton) findViewById(R.id.crumple_radio);
        mFoldRB = (RadioButton) findViewById(R.id.folded_radio);

        mCrumpleRB.setOnClickListener(this);
        mFoldRB.setOnClickListener(this);

        mThinkingET = (EditText) findViewById(R.id.thinkinget);

    }

    @Override
    public void onClick(View view) {

        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
            case R.id.crumple_radio:
                if (checked)
                    isCrumpled = true;
                break;
            case R.id.folded_radio:
                if (checked)
                    isCrumpled = false;
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest().create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
}
