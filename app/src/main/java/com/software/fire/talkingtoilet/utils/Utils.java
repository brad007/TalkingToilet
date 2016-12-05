package com.software.fire.talkingtoilet.utils;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Brad on 12/5/2016.
 */

public class Utils {
    public static String getUID(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.TALKING_TOILET)
                .push();
        String URL = databaseReference.toString();
        String[]URL_array = URL.split("/");
        return URL_array[URL_array.length-1];
    }
}
