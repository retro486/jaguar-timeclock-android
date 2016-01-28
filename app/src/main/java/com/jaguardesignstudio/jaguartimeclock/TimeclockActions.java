package com.jaguardesignstudio.jaguartimeclock;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by RustyPowerhouse on 1/25/2016.
 */
public class TimeclockActions {
    private static final String LOG_TAG = "TimeclockActions";

    public static StringBuffer createRecord(String pin) throws JSONException, ParseException, TimeclockException, IOException {
        Log.i(LOG_TAG, "Creating badge record...");

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder
                .scheme("http")
                .authority(BuildConfig.TIMECLOCK_SERVER)
                .appendPath("badge_records.json")
                .appendQueryParameter("apikey", BuildConfig.TIMECLOCK_API_KEY)
                .appendQueryParameter("pin", pin);

            URL url = new URL(builder.build().toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            buffer = new StringBuffer();
            if(inputStream == null) {
                throw new TimeclockException("Server response was empty.");
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                throw new TimeclockException("Server response contents were empty.");
            }
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // unable to close stream
                }
            }
        }

        return buffer;
    }

    public static StringBuffer getLastRecord(String pin) throws JSONException, ParseException, TimeclockException, IOException {
        Log.i(LOG_TAG, "Fetching most recent badge record...");

        HttpURLConnection conn = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;

        try {
            Uri.Builder builder = new Uri.Builder();
            builder
                    .scheme("http")
                    .authority(BuildConfig.TIMECLOCK_SERVER)
                    .appendPath("badge_records.json")
                    .appendQueryParameter("apikey", BuildConfig.TIMECLOCK_API_KEY)
                    .appendQueryParameter("pin", pin);

            URL url = new URL(builder.build().toString());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            InputStream inputStream = conn.getInputStream();
            buffer = new StringBuffer();
            if(inputStream == null) {
                throw new TimeclockException("Server response was empty.");
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if(buffer.length() == 0) {
                throw new TimeclockException("Server response contents were empty.");
            }
        }
        finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    // unable to close stream
                }
            }
        }

        return buffer;
    }

    public static ClockStamp parseServerResponse(String responseString) {
        Log.i(LOG_TAG, "Parsing timeclock server response...");

        ClockStamp cs = null;

        try {
            JSONObject response = new JSONObject(responseString);
            String status = response.getString("status");
            String lastTimeString = response.getString("lastTime");
            SimpleDateFormat sdf = new SimpleDateFormat();
            Date lastTime = sdf.parse(lastTimeString);
            cs = new ClockStamp(status, lastTime);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Unable to parse JSON", e);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Unable to parse Date", e);
        }

        return cs;
    }
}
