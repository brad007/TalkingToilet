package com.software.fire.talkingtoilet.model;

/**
 * Created by Brad on 12/3/2016.
 */

public class StatsModel {
    private long numberOfParticipants;
    private long numberOfFolded;
    private long numberOfCrumpled;

    public StatsModel() {
    }

    public StatsModel(long numberOfParticipants, long numberOfFolded, long numberOfCrumpled) {

        this.numberOfParticipants = numberOfParticipants;
        this.numberOfFolded = numberOfFolded;
        this.numberOfCrumpled = numberOfCrumpled;
    }

    public long getNumberOfParticipants() {

        return numberOfParticipants;
    }

    public void setNumberOfParticipants(long numberOfParticipants) {
        this.numberOfParticipants = numberOfParticipants;
    }

    public long getNumberOfFolded() {
        return numberOfFolded;
    }

    public void setNumberOfFolded(long numberOfFolded) {
        this.numberOfFolded = numberOfFolded;
    }

    public long getNumberOfCrumpled() {
        return numberOfCrumpled;
    }

    public void setNumberOfCrumpled(long numberOfCrumpled) {
        this.numberOfCrumpled = numberOfCrumpled;
    }
}
