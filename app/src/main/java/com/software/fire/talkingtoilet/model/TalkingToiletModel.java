package com.software.fire.talkingtoilet.model;

/**
 * Created by Brad on 12/5/2016.
 */

public class TalkingToiletModel {

    /*
    All parameters are Strings so that when we call the database we can manipulate the data
    without creating a ListView or RecyclerView
     */
    private String uid;
    private String thoughts;
    private String isCrumpled;

    public TalkingToiletModel() {
    }

    public TalkingToiletModel(String uid, String thoughts, String isCrumpled) {

        this.uid = uid;
        this.thoughts = thoughts;
        this.isCrumpled = isCrumpled;
    }

    public String getUid() {

        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getIsCrumpled() {
        return isCrumpled;
    }

    public void setIsCrumpled(String isCrumpled) {
        this.isCrumpled = isCrumpled;
    }
}
