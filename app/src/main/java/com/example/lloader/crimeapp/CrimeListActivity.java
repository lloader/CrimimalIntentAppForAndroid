package com.example.lloader.crimeapp;

import android.support.v4.app.Fragment;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
