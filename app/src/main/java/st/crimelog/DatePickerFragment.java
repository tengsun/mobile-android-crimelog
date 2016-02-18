package st.crimelog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;

public class DatePickerFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_date, null);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.crime_date_picker)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }
}
