package st.crimelog;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.Date;
import java.util.UUID;

import st.crimelog.model.Crime;
import st.crimelog.model.CrimeLab;
import st.crimelog.model.Photo;
import st.crimelog.util.PictureUtil;
import st.crimelog.util.TimeUtil;

import static android.widget.CompoundButton.OnCheckedChangeListener;

public class CrimeFragment extends Fragment {

    private static final String TAG = "CrimeFragment";

    public static final String EXTRA_CRIME_ID = "st.crimelog.crimeId";
    private static final String DIALOG_DATE = "date";
    private static final String DIALOG_PHOTO = "photo";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private ImageButton photoButton;
    private ImageView photoView;
    private Button suspectButton;
    private Button reportButton;
    private Callbacks callbacks;

    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Log.d(TAG, "onCreate() called");

        UUID crimeId = (UUID) getArguments().getSerializable(EXTRA_CRIME_ID);
        crime = CrimeLab.getInstance(getActivity()).getCrime(crimeId);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.d(TAG, "onCreateView() called");
        View view = inflater.inflate(R.layout.fragment_crime, container, false);

        setChildViews(view);
        setControlActions(view);

        return view;
    }

    private void setChildViews(View view) {
        titleField = (EditText) view.findViewById(R.id.crime_title);
        titleField.setText(crime.getTitle());

        dateButton = (Button) view.findViewById(R.id.crime_date);
        dateButton.setText(TimeUtil.getDisplayDatetime(crime.getDate()));

        solvedCheckBox = (CheckBox) view.findViewById(R.id.crime_solved);
        solvedCheckBox.setChecked(crime.isSolved());

        photoButton = (ImageButton) view.findViewById(R.id.crime_take_photo);
        photoView = (ImageView) view.findViewById(R.id.crime_photo_view);

        suspectButton = (Button) view.findViewById(R.id.crime_choose_suspect);
        if (crime.getSuspect() != null) {
            suspectButton.setText(crime.getSuspect());
        }
        reportButton = (Button) view.findViewById(R.id.crime_send_report);
    }

    private void setControlActions(View view) {
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
                callbacks.onCrimeUpdated(crime);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(crime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        solvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                callbacks.onCrimeUpdated(crime);
            }
        });

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(intent, REQUEST_PHOTO);
            }
        });

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Photo photo = crime.getPhoto();
                if (photo == null) {
                    return;
                }

                FragmentManager fm = getActivity().getSupportFragmentManager();
                String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
                PhotoFragment.newInstance(path).show(fm, DIALOG_PHOTO);
            }
        });

        suspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, REQUEST_CONTACT);
            }
        });

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                intent = Intent.createChooser(intent, getString(R.string.crime_send_report_chooser));
                startActivity(intent);
            }
        });
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // check result code
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        // check request date
        else if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            callbacks.onCrimeUpdated(crime);
            dateButton.setText(TimeUtil.getDisplayDatetime(crime.getDate()));
        }
        // check request photo
        else if (requestCode == REQUEST_PHOTO) {
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (fileName != null) {
                Photo photo = new Photo(fileName);
                crime.setPhoto(photo);
                callbacks.onCrimeUpdated(crime);
                showPhoto();
            }
        }
        // check request contact
        else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();

            // specify the fields want to query
            String[] queryFields = new String[]{ ContactsContract.Contacts.DISPLAY_NAME };
            Cursor cursor = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            if (cursor.getCount() == 0) {
                cursor.close();
                return;
            }
            cursor.moveToFirst();
            String suspect = cursor.getString(0);
            crime.setSuspect(suspect);
            callbacks.onCrimeUpdated(crime);
            suspectButton.setText(suspect);
            cursor.close();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.getInstance(getActivity()).saveCrimes();
    }

    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    @Override
    public void onStop() {
        super.onStop();
        PictureUtil.cleanImageView(photoView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        callbacks = (Callbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    private void showPhoto() {
        Photo photo = crime.getPhoto();
        BitmapDrawable bitmap = null;
        if (photo != null) {
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            bitmap = PictureUtil.getScaledDrawable(getActivity(), path);
        }
        photoView.setImageDrawable(bitmap);
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (crime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }
        String dateString = TimeUtil.getDisplayDatetime(crime.getDate());
        String suspect = crime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }
        String report = getString(R.string.crime_report,
                crime.getTitle(), dateString, solvedString, suspect);
        return report;
    }


}
