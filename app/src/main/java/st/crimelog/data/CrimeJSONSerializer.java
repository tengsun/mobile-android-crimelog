package st.crimelog.data;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import st.crimelog.Crime;

/**
 * Created by tengsun on 2/23/16.
 */
public class CrimeJSONSerializer {

    private static final String TAG = "CrimeJSONSerializer";

    private Context context;
    private String fileName;

    public CrimeJSONSerializer(Context context, String fileName) {
        this.context = context;
        this.fileName = fileName;
    }

    public List<Crime> loadCrimes() throws Exception {
        List<Crime> crimes = new ArrayList<Crime>();

        BufferedReader reader = null;
        try {
            InputStream in = context.openFileInput(fileName);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            for (int i = 0; i < array.length(); i++) {
                crimes.add(new Crime(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "JSON file not found: ", e);
        } finally {
            if (reader != null) {
                reader.close();
            }
        }

        return crimes;
    }

    public void saveCrimes(List<Crime> crimes) throws IOException, JSONException {
        // build json array
        JSONArray array = new JSONArray();
        for (Crime c : crimes) {
            array.put(c.toJSON());
        }

        // write file to disk
        Writer writer = null;
        try {
            OutputStream out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
