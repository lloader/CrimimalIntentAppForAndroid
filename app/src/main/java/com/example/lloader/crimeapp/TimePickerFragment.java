package com.example.lloader.crimeapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexander Garkavenko
 */
public class TimePickerFragment extends DialogFragment{

    private TimePicker mTimePicker;
    private final Calendar mCalendar = Calendar.getInstance();
    private Date mDate;
    private static final String DATE_TIME_ARG = "date time arg";
    public static final String DATE_TIME_EXTRA = "date time extra";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_time, null);
        mTimePicker = (TimePicker) view.findViewById(R.id.time_picker);

        mDate = (Date) getArguments().getSerializable(DATE_TIME_ARG);
        mCalendar.setTime(mDate);
        int hour = mCalendar.get(Calendar.HOUR);
        int minute = mCalendar.get(Calendar.MINUTE);


        if(Build.VERSION.SDK_INT >= 23) {
            mTimePicker.setHour(hour);
            mTimePicker.setMinute(minute);
        } else {
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
        }

        return new AlertDialog.Builder(getActivity())
                .setTitle("Set time of crime: ")
                .setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Build.VERSION.SDK_INT >= 23) {
                            mCalendar.set(Calendar.HOUR, mTimePicker.getHour());
                            mCalendar.set(Calendar.MINUTE, mTimePicker.getMinute());
                        } else {
                            mCalendar.set(Calendar.HOUR, mTimePicker.getCurrentHour());
                            mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
                        }
                        mDate = new GregorianCalendar(mCalendar.get(Calendar.YEAR),
                                mCalendar.get(Calendar.MONTH),
                                mCalendar.get(Calendar.DAY_OF_MONTH),
                                mCalendar.get(Calendar.HOUR),
                                mCalendar.get(Calendar.MINUTE)).getTime();
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static TimePickerFragment newInstance(Date date) {
        final Bundle args = new Bundle();
        args.putSerializable(DATE_TIME_ARG, date);

        final TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.setArguments(args);
        return timePickerFragment;
    }

    private void sendResult(final int activityResult) {
        final Intent intent = new Intent();
        intent.putExtra(DATE_TIME_EXTRA, mDate);

        final Fragment fragment = getTargetFragment();
        fragment.onActivityResult(getTargetRequestCode(), activityResult, intent);
    }
}
