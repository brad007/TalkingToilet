package com.software.fire.talkingtoilet.model;

import java.util.HashMap;

/**
 * Created by Brad on 12/3/2016.
 */

public class TalkingToiletModel extends HashMap<String, Boolean> {
    private boolean isCrumpled;
    private String thinking;

    public TalkingToiletModel() {
    }

    public TalkingToiletModel(boolean isCrumpled, String thinking) {
        this.isCrumpled = isCrumpled;
        this.thinking = thinking;
    }

    public boolean isCrumpled() {
        return isCrumpled;
    }

    public void setCrumpled(boolean crumpled) {
        isCrumpled = crumpled;
    }

    public String getThinking() {
        return thinking;
    }

    public void setThinking(String thinking) {
        this.thinking = thinking;
    }
}
