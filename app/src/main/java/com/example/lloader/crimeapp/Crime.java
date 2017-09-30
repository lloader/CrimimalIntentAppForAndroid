package com.example.lloader.crimeapp;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class Crime {
    private UUID mUUID;
    private Date mDate;
    private String mName;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
        mUUID = UUID.randomUUID();
        mDate = new Date();
    }

    public Crime(final UUID uuid) {
        mUUID = uuid;
        mDate = new Date();
    }

    public UUID getUUID() {
        return mUUID;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
}
