package st.crimelog;

import android.support.v4.app.Fragment;

/**
 * Created by tengsun on 2/1/16.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_master_detail;
    }
}
