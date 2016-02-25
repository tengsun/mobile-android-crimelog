package st.crimelog;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import st.crimelog.model.Crime;
import st.crimelog.model.CrimeLab;
import st.crimelog.util.TimeUtil;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeListFragment extends ListFragment {

    private static final String TAG = "CrimeListFragment";

    private List<Crime> crimes;
    private Callbacks callbacks;

    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        getActivity().setTitle(R.string.crime_list);
        crimes = CrimeLab.getInstance(getActivity()).getCrimes();

        // build array adapter
        CrimeAdapter adapter = new CrimeAdapter(crimes);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // register context menu
        ListView listView = (ListView) view.findViewById(android.R.id.list);
        registerForContextMenu(listView);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.crime_menu_new_item:
                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);

//                Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
//                intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
//                startActivityForResult(intent, 0);
                ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
                callbacks.onCrimeSelected(crime);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.context_crime_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info
                = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter) getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.crime_menu_delete_item:
                CrimeLab.getInstance(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
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
//        Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
//        intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
//        startActivity(intent);
        callbacks.onCrimeSelected(c);
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

    public void updateListUI() {
        ((CrimeAdapter) getListAdapter()).notifyDataSetChanged();
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
