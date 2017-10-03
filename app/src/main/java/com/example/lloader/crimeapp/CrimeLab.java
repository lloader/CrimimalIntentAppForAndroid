package com.example.lloader.crimeapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lloader.crimeapp.database.CrimeBaseHelper;
import com.example.lloader.crimeapp.database.CrimeCursorWrapper;
import com.example.lloader.crimeapp.database.CrimeDbSchema.CrimeTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private final Context mContext;
    private final SQLiteDatabase mCrimeBase;
    public static String FILES_PATH = "com.example.lloader.crimeapp.fileprovider";


    private CrimeLab(final Context context) {
        mContext = context;
        mCrimeBase = new CrimeBaseHelper(context)
                .getWritableDatabase();
    }

    public static CrimeLab getInstance(final Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
            return sCrimeLab;
        } else {
            return sCrimeLab;
        }
    }

    public Crime getCrime(final UUID uuid) {
        final Crime crime;

        try(final CrimeCursorWrapper cursorWrapper = queryCrimes(CrimeTable.Cols.UUID + " = ?",
                uuid.toString())) {
            cursorWrapper.moveToFirst();
            if(cursorWrapper.getCount() > 0) {
                crime = cursorWrapper.getCrime();
            } else {
                return null;
            }
        }
        return crime;
    }

    public long addCrime(final Crime crime) {
        final ContentValues values = getContentValues(crime);

        return mCrimeBase.insert(CrimeTable.NAME, null, values);
    }

    public List<Crime> getCrimes() {
        final List<Crime> crimes = new ArrayList<>();

        try (CrimeCursorWrapper crimeCursorWrapper = queryCrimes(null)) {
            crimeCursorWrapper.moveToFirst();
            while (!crimeCursorWrapper.isAfterLast()) {
                crimes.add(crimeCursorWrapper.getCrime());
                crimeCursorWrapper.moveToNext();
            }
        }

        return crimes;
    }

    public int updateCrime(final Crime crime) {
        final String uuid = crime.getUUID().toString();
        final ContentValues values = getContentValues(crime);

        return mCrimeBase.update(CrimeTable.NAME,
                values, CrimeTable.Cols.UUID + " = ?",
                new String[] {uuid});
    }

    private static ContentValues getContentValues(final Crime crime) {
        final ContentValues values = new ContentValues();

        values.put(CrimeTable.Cols.UUID, crime.getUUID().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getName());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());
        values.put(CrimeTable.Cols.PHONE, crime.getPhoneNumber());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String... args) {
        final Cursor cursor = mCrimeBase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                args,
                null,
                null,
                null
        );

        return new CrimeCursorWrapper(cursor);
    }

    public File getPhotoFile(final Crime crime) {
        final File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFileName());
    }
}
