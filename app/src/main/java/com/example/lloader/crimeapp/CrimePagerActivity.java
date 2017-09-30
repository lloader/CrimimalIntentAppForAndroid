package com.example.lloader.crimeapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.List;
import java.util.UUID;

import static com.example.lloader.crimeapp.SingleFragmentActivity.LOG_TAG;

/**
 * Created by Alexander Garkavenko
 */

public class CrimePagerActivity extends AppCompatActivity {
    private static final String CRIME_ID_EXTRA = "crime_id_intent";
    private ViewPager mCrimePager;
    private PagerAdapter mCrimePagerAdapter;
    private List<Crime> mCrimes;
    private Button mToStartButton;
    private Button mToLastButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        mToLastButton = (Button) findViewById(R.id.go_to_last_button);
        mToStartButton = (Button) findViewById(R.id.go_to_start_button);

        mToLastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCrimePager.setCurrentItem(mCrimes.size() - 1);
            }
        });

        mToStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCrimePager.setCurrentItem(0);
            }
        });

        final FragmentManager fm = getSupportFragmentManager();
        mCrimes = CrimeLab.getInstance(this).getCrimes();

        mCrimePagerAdapter = new FragmentStatePagerAdapter(fm) {
            @Override
            public Fragment getItem(int position) {
                return CrimeFragment.newInstance(mCrimes.get(position).getUUID());
            }

            @Override
            public int getCount() {
                return mCrimes.size();
            }
        };

        mCrimePager = (ViewPager) findViewById(R.id.crime_pager);
        mCrimePager.setAdapter(mCrimePagerAdapter);

        final Crime crime = CrimeLab.getInstance(this).getCrime((UUID) getIntent().getSerializableExtra(CRIME_ID_EXTRA));
        Log.d(LOG_TAG, "устанавливаю на экран Crime(" + crime.getUUID() + ")");
        mCrimePager.setCurrentItem(crime.getPosition());
    }

    public static Intent newIntent(final Context context, final UUID crimeId){
        final Intent intent = new Intent(context, CrimePagerActivity.class);
        intent.putExtra(CRIME_ID_EXTRA, crimeId);
        return intent;
    }
}
