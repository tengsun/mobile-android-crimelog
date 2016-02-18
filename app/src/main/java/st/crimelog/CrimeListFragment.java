package st.crimelog;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import st.crimelog.util.TimeUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends ListFragment {

    private static final String TAG = "CrimeListFragment";

    private List<Crime> crimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.crime_list);
        crimes = CrimeLab.getInstance(getActivity()).getCrimes();

        // build array adapter
        CrimeAdapter adapter = new CrimeAdapter(crimes);
        setListAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Crime c = ((CrimeAdapter) getListAdapter()).getItem(position);
        // Log.d(TAG, c.getTitle() + " was clicked");

        // start crime activity
        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(intent);
    }

    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(List<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // inflate one if convert view is null
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_crime, null);
            }

            // configure the view for crime
            Crime c = getItem(position);

            TextView title = (TextView) convertView.findViewById(R.id.crime_list_title);
            title.setText(c.getTitle());
            TextView date = (TextView) convertView.findViewById(R.id.crime_list_date);
            date.setText(TimeUtil.getDisplayDatetime(c.getDate()));
            CheckBox solved = (CheckBox) convertView.findViewById(R.id.crime_list_solved);
            solved.setChecked(c.isSolved());

            return convertView;
        }
    }

}
