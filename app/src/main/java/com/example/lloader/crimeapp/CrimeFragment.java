package com.example.lloader.crimeapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private static final String ARG_CRIME_ID = "Crime_arg_id";
    private static final String CRIME_POS_EXTRA = "crime position extra in intent result";
    private static final String DATE_PICKER_TAG = "date picker tag in fragment manager";
    private static final String TIME_PICKER_TAG = "time picker tag";
    private static final int DATE_PICKER_REQUEST_CODE = 0x123;
    private static final int TIME_PICKER_REQUEST_CODE = 0x124;
    private Button mDateButton;
    private Button mTimeButton;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == DATE_PICKER_REQUEST_CODE) {
            if(data != null) {
                final Date date = (Date) data.getSerializableExtra(DatePickerFragment.DATE_EXTRA);
                mCrime.setDate(date);
                setDateOnDateButton();
            }
        }

        if(requestCode == TIME_PICKER_REQUEST_CODE) {
            if(data != null) {
                final Date date = (Date) data.getSerializableExtra(TimePickerFragment.DATE_TIME_EXTRA);
                mCrime.setDate(date);
                mTimeButton.setText(DateFormat.format("hh:mm", mCrime.getDate()));
            }
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getInstance(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_crime, container, false);
        final EditText mName = (EditText) view.findViewById(R.id.crime_name);
        mName.setText(mCrime.getName());
        mName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mCrime.setName(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDateButton = (Button) view.findViewById(R.id.crime_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FragmentManager fragmentManager = getFragmentManager();
                final DatePickerFragment datePickerFragment = DatePickerFragment.newInstance(mCrime.getDate());
                datePickerFragment.setTargetFragment(CrimeFragment.this, DATE_PICKER_REQUEST_CODE);
                datePickerFragment.show(fragmentManager, DATE_PICKER_TAG);
            }
        });

        setDateOnDateButton();

        final CheckBox checkBox = view.findViewById(R.id.solve_box);
        checkBox.setChecked(mCrime.isSolved());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mCrime.setSolved(b);
            }
        });

        mTimeButton = (Button) view.findViewById(R.id.crime_time);
        mTimeButton.setText(DateFormat.format("hh:mm", mCrime.getDate()));
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(mCrime.getDate());
                timePickerFragment.setTargetFragment(CrimeFragment.this, TIME_PICKER_REQUEST_CODE);
                timePickerFragment.show(getFragmentManager(), TIME_PICKER_TAG);
            }
        });

        return view;
    }

    public static Fragment newInstance(final UUID idCrime) {
        final Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, idCrime);
        final CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    public static int getCrimePosFromIntent(final Intent data) {
        final int crimePosition = data.getIntExtra(CRIME_POS_EXTRA, 0);
        return crimePosition;
    }

    private void setDateOnDateButton() {
        final String day = DateFormat.format("EEEE, MMM d, yyyy", mCrime.getDate()).toString();
        mDateButton.setText(day);
    }
}
