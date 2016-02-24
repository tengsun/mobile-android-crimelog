package st.crimelog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
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

    private Crime crime;
    private EditText titleField;
    private Button dateButton;
    private CheckBox solvedCheckBox;
    private ImageButton photoButton;
    private ImageView photoView;

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
    }

    private void setControlActions(View view) {
        titleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
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
        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            crime.setDate(date);
            dateButton.setText(TimeUtil.getDisplayDatetime(crime.getDate()));
        }

        // check request photo
        if (requestCode == REQUEST_PHOTO) {
            String fileName = data.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (fileName != null) {
                Photo photo = new Photo(fileName);
                crime.setPhoto(photo);
                showPhoto();
            }
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

    private void showPhoto() {
        Photo photo = crime.getPhoto();
        BitmapDrawable bitmap = null;
        if (photo != null) {
            String path = getActivity().getFileStreamPath(photo.getFileName()).getAbsolutePath();
            bitmap = PictureUtil.getScaledDrawable(getActivity(), path);
        }
        photoView.setImageDrawable(bitmap);
    }

}
