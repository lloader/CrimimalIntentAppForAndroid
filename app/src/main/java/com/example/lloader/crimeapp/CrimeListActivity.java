package com.example.lloader.crimeapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.CallBacks, CrimeFragment.CallBacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(final UUID crime, int position) {
        if(findViewById(R.id.fragment_detail_layout) == null) {
            final Intent intent = CrimePagerActivity.newIntent(this, position);
            startActivity(intent);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction()
                    .replace(R.id.fragment_detail_layout, CrimeFragment.newInstance(crime))
                    .commit();
        }
    }

    @Override
    public void onUpdateCrime() {
        final CrimeListFragment crimeListFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_frame_layout);
        crimeListFragment.updateUI();
    }
}
