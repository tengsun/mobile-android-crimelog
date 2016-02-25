package st.crimelog;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import st.crimelog.model.Crime;

/**
 * Created by tengsun on 2/1/16.
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_master_detail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.fragment_container_details) != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment oldDetail = fm.findFragmentById(R.id.fragment_container_details);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.fragment_container_details, newDetail);
            ft.commit();
        } else {
            Intent intent = new Intent(this, CrimePagerActivity.class);
            intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment =
                (CrimeListFragment) fm.findFragmentById(R.id.fragment_container);
        listFragment.updateListUI();
    }
}
