package st.crimelog.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tengsun on 2/24/16.
 */
public class Photo {

    private static final String JSON_FILENAME = "filename";

    private String fileName;

    public Photo(String fileName) {
        this.fileName = fileName;
    }

    public Photo(JSONObject json) throws JSONException {
        fileName = json.getString(JSON_FILENAME);
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(JSON_FILENAME, fileName);
        return json;
    }

    public String getFileName() {
        return fileName;
    }

}
