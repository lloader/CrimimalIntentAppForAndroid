package com.example.lloader.crimeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeListFragment extends Fragment {

    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mCrimeHolderAdapter;
    private static final int CRIME_ACTIVITY_REQUEST_CODE = 1;
    private static final String CRIME_LIST_FRAGMENT_TAG = "CrimeListFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCrimeRecyclerView.animate();

        setHasOptionsMenu(true);

        updateUI();

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_crime:
                final CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
                final Crime crime = new Crime();
                crimeLab.addCrime(crime);
                Log.d(SingleFragmentActivity.LOG_TAG, "Создан новый объект Crime(" + crime.getUUID() + ")");
                final Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getUUID());
                startActivity(intent);
                return true;
            case R.id.show_subtitle:
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }
    }

    private void updateUI() {
        final List<Crime> crimes = CrimeLab.getInstance(getActivity()).getCrimes();
        if(mCrimeHolderAdapter == null) {
            mCrimeHolderAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mCrimeHolderAdapter);
        } else {
            mCrimeHolderAdapter.setCrimes(crimes);
            mCrimeHolderAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void updateSubtitle() {
        final CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        final int crimeCount = crimeLab.getCrimes().size();
        final String subtitle = getString(R.string.subtitle_format, crimeCount);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mCrimeNameTextView;
        private TextView mCrimeDateTextView;
        private ImageView mCrimeSolvedImageView;
        private UUID mCrimeId;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.crime_list_item, parent, false));

            mCrimeNameTextView = itemView.findViewById(R.id.crime_name);
            mCrimeDateTextView = itemView.findViewById(R.id.crime_date);
            mCrimeSolvedImageView = itemView.findViewById(R.id.crime_solved_image);
            itemView.setOnClickListener(this);
        }

        public void bind(final Crime crime) {
            mCrimeNameTextView.setText(crime.getName());
            final String day = DateFormat.format("EEEE, MMM d, yyyy", crime.getDate()).toString();
            mCrimeDateTextView.setText(day);
            mCrimeSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.INVISIBLE);
            mCrimeId = crime.getUUID();
        }

        @Override
        public void onClick(View view) {
            startActivity(CrimePagerActivity.newIntent(getActivity(), mCrimeId));
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            final Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        public void setCrimes(final List<Crime> crimes) {
            mCrimes = crimes;
        }
    }
}
