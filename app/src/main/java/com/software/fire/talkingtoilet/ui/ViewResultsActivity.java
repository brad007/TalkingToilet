package com.software.fire.talkingtoilet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.software.fire.talkingtoilet.R;
import com.software.fire.talkingtoilet.model.StatsModel;
import com.software.fire.talkingtoilet.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class ViewResultsActivity extends AppCompatActivity {

    private TextView mFoldedResultTV;
    private TextView mCrumpledResultTV;
    private TextView mThinkingResultTV;
    private TextView mLocationResultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialiseScreen();
        setupMethod();
        setupThinking();
    }

    //preset the radio buttons
    private void setupMethod() {
        DatabaseReference methodRef = FirebaseDatabase.getInstance()
                .getReference(Constants.STATS);
        methodRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    StatsModel model = dataSnapshot.getValue(StatsModel.class);
                    mFoldedResultTV.setText("Folded " + ((model.getNumberOfFolded() * 100) / model.getNumberOfParticipants()) + "%");
                    mCrumpledResultTV.setText("Crumpled " + ((model.getNumberOfCrumpled() * 100) / model.getNumberOfParticipants()) + "%");
                    setupLocation(model.getNumberOfParticipants());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //presets the thinking editText
    private void setupThinking() {
        DatabaseReference thinkingRef = FirebaseDatabase.getInstance()
                .getReference(Constants.TALKING_TOILET);
        thinkingRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String result = "";
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, HashMap<String, String>> map = (HashMap<String, HashMap<String, String>>) dataSnapshot.getValue();
                    for (Map.Entry<String, HashMap<String, String>> entry : map.entrySet()) {
                        HashMap<String, String> model = entry.getValue();
                        result += model.get(Constants.THOUGHTS) + ", ";
                    }

                    result += "etc";
                } else {
                    Toast.makeText(ViewResultsActivity.this, "null", Toast.LENGTH_SHORT).show();
                }
                mThinkingResultTV.setText(result);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //sets up the location text
    private void setupLocation(final long n) {
        DatabaseReference locationRef = FirebaseDatabase.getInstance()
                .getReference(Constants.LOCATION_STATS);
        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String result = "";
                if (dataSnapshot.getValue() != null) {
                    HashMap<String, Long> map = (HashMap<String, Long>) dataSnapshot.getValue();
                    for (Map.Entry<String, Long> entry : map.entrySet()) {
                        // System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        result += ((entry.getValue() * 100) / n + "% " + entry.getKey() + "\n\n");
                    }

                    mLocationResultTV.setText(result.substring(0, result.length() - 2));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void initialiseScreen() {
        mFoldedResultTV = (TextView) findViewById(R.id.folded_result_tv);
        mCrumpledResultTV = (TextView) findViewById(R.id.crumpled_result_tv);
        mThinkingResultTV = (TextView) findViewById(R.id.thinking_result_tv);
        mLocationResultTV = (TextView) findViewById(R.id.location_result_tv);
    }


}
