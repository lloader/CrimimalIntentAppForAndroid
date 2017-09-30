package com.example.lloader.crimeapp.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.example.lloader.crimeapp.Crime;
import com.example.lloader.crimeapp.database.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeCursorWrapper extends CursorWrapper {

    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() {
        final String uuid = getString(getColumnIndex(CrimeTable.Cols.UUID));
        final String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        final long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        final boolean isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED)) == 1;
        final String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));
        final String phone = getString(getColumnIndex(CrimeTable.Cols.PHONE));

        final Crime crime = new Crime(UUID.fromString(uuid));
        crime.setName(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved);
        crime.setSuspect(suspect);
        crime.setPhoneNumber(phone);

        return crime;
    }
}
