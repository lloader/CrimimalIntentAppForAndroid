package com.example.lloader.crimeapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.lloader.crimeapp.database.CrimeCursorWrapper;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Alexander Garkavenko
 */

public class CrimeFragment extends Fragment {

    /*
        Activity-host musts implement this interface
     */
    public interface CallBacks {
        void onUpdateCrime();
    }

    private Crime mCrime;
    private static final String ARG_CRIME_ID = "Crime_arg_id";
    private static final String CRIME_POS_EXTRA = "crime position extra in intent result";
    private static final String DATE_PICKER_TAG = "date picker tag in fragment manager";
    private static final String TIME_PICKER_TAG = "time picker tag";
    private static final int DATE_PICKER_REQUEST_CODE = 0x123;
    private static final int TIME_PICKER_REQUEST_CODE = 0x124;
    private static final int CHOOSE_SUSPECT_REQUEST_CODE = 0x125;
    private static final int CREATE_PHOTO_REQUEST_CODE = 0x126;
    private Button mDateButton;
    private Button mTimeButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallButton;
    private ImageView mPhotoView;
    private ImageButton mPhotoButton;
    private File mPhotoFile;
    private CallBacks mActivityHostCallBack;

    @Override
    public void onDetach() {
        super.onDetach();
        mActivityHostCallBack = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityHostCallBack = (CallBacks) context;
    }

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

        if(requestCode == CHOOSE_SUSPECT_REQUEST_CODE && data != null) {
            final Uri uri = data.getData();

            final String[] queryFields = {ContactsContract.Contacts.DISPLAY_NAME};
            try(final Cursor c = getActivity().getContentResolver()
                    .query(uri, queryFields, null, null, null)) {
                if(c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                mCrime.setSuspect(c.getString(0));
                mSuspectButton.setText(mCrime.getSuspect());
            } catch (NullPointerException e) {
                return;
            }


            String id = null;
            try(final Cursor c = getActivity().getContentResolver()
                    .query(uri, new String[] {ContactsContract.Contacts._ID}, null, null, null)) {
                if(c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                id = c.getString(0);
            } catch (NullPointerException e) {}
            try(final Cursor c = getActivity().getContentResolver()
                    .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            new String[] {ContactsContract.CommonDataKinds.Phone.NUMBER},
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " =?", new String[] {id}, null)) {
                if(c.getCount() == 0) {
                    return;
                }
                c.moveToFirst();
                final String phone = c.getString(0);
                mCrime.setPhoneNumber(phone);
                mCallButton.setEnabled(true);
                Log.d(SingleFragmentActivity.LOG_TAG, "phone is " + phone);
            } catch (NullPointerException e) {}
        }

        if(requestCode == CREATE_PHOTO_REQUEST_CODE) {
            final Uri uri = FileProvider.getUriForFile(getContext(), CrimeLab.FILES_PATH, mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.getInstance(getContext()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        updateCrime();
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                updateCrime();
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
                updateCrime();
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

        mReportButton = (Button) view.findViewById(R.id.report_button);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain")
                        .setSubject(getString(R.string.report_subject))
                        .setText(getReport())
                        .setChooserTitle(R.string.send_report)
                        .getIntent();
                startActivity(intent);
            }
        });

        final Intent pickPersonIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) view.findViewById(R.id.suspect_button);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickPersonIntent, CHOOSE_SUSPECT_REQUEST_CODE);
            }
        });

        mCallButton = (Button) view.findViewById(R.id.call_button);
        mCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + mCrime.getPhoneNumber()));
                startActivity(intent);
            }
        });

        if(mCrime.getPhoneNumber() == null) {
            mCallButton.setEnabled(false);
        }
        if(mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        final PackageManager manager = getActivity().getPackageManager();
        if(manager.resolveActivity(pickPersonIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) view.findViewById(R.id.make_photo_button);
        mPhotoView = (ImageView) view.findViewById(R.id.crime_photo);

        final Intent createPhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        final boolean canTakePhoto = mPhotoFile != null &&
                createPhotoIntent.resolveActivity(manager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Uri uri = FileProvider.getUriForFile(getContext(), CrimeLab.FILES_PATH, mPhotoFile);
                createPhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager().queryIntentActivities(createPhotoIntent, PackageManager.MATCH_DEFAULT_ONLY);

                for(ResolveInfo resolveInfo : cameraActivities) {
                    getActivity().grantUriPermission(
                            CrimeLab.FILES_PATH,
                            uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(createPhotoIntent, CREATE_PHOTO_REQUEST_CODE);
            }
        });
        updatePhotoView();
        return view;
    }

    private void updateCrime() {
        CrimeLab.getInstance(getContext()).updateCrime(mCrime);
        mActivityHostCallBack.onUpdateCrime();
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

    private String getReport() {
        String solved = null;
        if(mCrime.isSolved()) {
            solved = getString(R.string.case_solved);
        } else {
            solved = getString(R.string.case_not_solved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = null;
        if(mCrime.getSuspect() == null) {
            suspect = getString(R.string.no_suspect);
        } else {
            suspect = getString(R.string.exist_suspect, mCrime.getSuspect());
        }

        return getString(R.string.crime_report, mCrime.getName(), dateString, solved, suspect);
    }

    private void updatePhotoView() {
        if(mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
        } else {
            mPhotoView.setImageBitmap(PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity()));
        }
    }
}
