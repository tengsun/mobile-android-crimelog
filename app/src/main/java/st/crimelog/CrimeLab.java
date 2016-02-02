package st.crimelog;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by tengsun on 1/28/16.
 */
public class CrimeLab {

    private static CrimeLab crimeLab;
    private Context appContext;

    private List<Crime> crimes;

    private CrimeLab(Context appContext) {
        this.appContext = appContext;
        this.crimes = new ArrayList<Crime>();
        for (int i = 0; i < 17; i++) {
            Crime c = new Crime();
            c.setTitle("Crime #" + (i + 1));
            c.setSolved(i % 2 == 0);
            crimes.add(c);
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

    public Crime getCrime(UUID id) {
        for (Crime c: crimes) {
            if (c.getId().equals(id)) {
                return c;
            }
        }
        return null;
    }

}
