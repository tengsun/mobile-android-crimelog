package st.crimelog.model;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import st.crimelog.data.CrimeJSONSerializer;

/**
 * Created by tengsun on 1/28/16.
 */
public class CrimeLab {

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private static CrimeLab crimeLab;
    private Context appContext;

    private List<Crime> crimes;
    private CrimeJSONSerializer serializer;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        this.serializer = new CrimeJSONSerializer(appContext, FILENAME);
//        this.crimes = new ArrayList<Crime>();
//        for (int i = 0; i < 17; i++) {
//            Crime c = new Crime();
//            c.setTitle("Crime #" + (i + 1));
//            c.setSolved(i % 2 == 0);
//            crimes.add(c);
//        }
        try {
            crimes = serializer.loadCrimes();
            Log.d(TAG, "Load " + crimes.size() + " crimes");
        } catch (Exception e) {
            crimes = new ArrayList<Crime>();
            Log.e(TAG, "Error loading crimes: ", e);
        }
    }

    public static CrimeLab getInstance(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context.getApplicationContext());
        }
        return crimeLab;
    }

    public List<Crime> getCrimes() {
        return crimes;
    }

    public void addCrime(Crime crime) {
        crimes.add(crime);
    }

    public Crime getCrime(UUID id) {
        for (Crime c: crimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

    public void deleteCrime(Crime crime) {
        crimes.remove(crime);
    }

    public boolean saveCrimes() {
        try {
            serializer.saveCrimes(crimes);
            Log.d(TAG, "Save crimes to file");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error saving crimes: ", e);
            return false;
        }
    }

}
