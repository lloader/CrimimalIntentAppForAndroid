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
    private boolean mRequiresPolice;
    private int mPosition;

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

    public boolean isRequiresPolice() {
        return mRequiresPolice;
    }

    public void setRequiresPolice(boolean requiresPolice) {
        mRequiresPolice = requiresPolice;
    }

    public boolean getRequiresPolice() {
        return mRequiresPolice;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }
}
