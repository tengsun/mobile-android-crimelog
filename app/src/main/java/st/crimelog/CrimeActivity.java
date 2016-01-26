package st.crimelog;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class CrimeActivity extends AppCompatActivity {

    private static final String TAG = "CrimeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate() called");

        setContentView(R.layout.activity_crime);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);
        if (fragment == null) {
            fragment = new CrimeFragment();
            // use fm to add, remove, attach, detach or replace fragment
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }
}
