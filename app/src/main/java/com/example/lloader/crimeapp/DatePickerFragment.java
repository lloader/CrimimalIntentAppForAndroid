package com.example.lloader.crimeapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Alexander Garkavenko
 */

public class DatePickerFragment extends DialogFragment{

    private static final String DATE_ARG = "date_arg";
    public static String DATE_EXTRA = "DatePickerFragment date extra in intent";
    private DatePicker mDatePicker;
    private Date mDate;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mDate = (Date) getArguments().getSerializable(DATE_ARG);

        final View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);
        mDatePicker = (DatePicker) v.findViewById(R.id.date_picker);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity())
                .setTitle("Date of crime:")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final int year = mDatePicker.getYear();
                        final int month = mDatePicker.getMonth();
                        final int day = mDatePicker.getDayOfMonth();
                        final Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);
                    }
                })
                .setView(v)
                .create();
    }

    public static DatePickerFragment newInstance(final Date date) {
        final Bundle args = new Bundle();
        args.putSerializable(DATE_ARG, date);

        final DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.setArguments(args);

        return datePickerFragment;
    }

    private void sendResult(final int activityResult, final Date date) {
        final Intent intent = new Intent();
        intent.putExtra(DATE_EXTRA, date);
        final Fragment fragment = getTargetFragment();
        fragment.onActivityResult(getTargetRequestCode(), activityResult, intent);
    }
}
