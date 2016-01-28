package com.jaguardesignstudio.jaguartimeclock;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;

// Feature idea: keep track of clock ins/outs locally and sync with server separately once internet is restored.

public class MainActivity extends ActionBarActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private ClockStamp mClockStamp; // TODO store this locally for caching

    public boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Log.i(LOG_TAG, "Clock In/Out tapped...");
        new FetchTimeclockTask().execute("1234"); // TODO put this in a preference to make it user-configurable

        Button btn = (Button) findViewById(R.id.clockStatusButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(LOG_TAG, "Clock In/Out tapped...");
                new CreateTimeclockTask().execute("1234");
            }
        });
    }

    public class CreateTimeclockTask extends AsyncTask<String, Void, ClockStamp> {
        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        protected ClockStamp doInBackground(String... params) {
            Log.i(LOG_TAG, "Running clock task...");

            if(params.length != 1) {
                return null;
            }

            ClockStamp cs = null;
            try {
                StringBuffer buffer = TimeclockActions.createRecord(params[0]);
                if(buffer != null) {
                    cs = TimeclockActions.parseServerResponse(buffer.toString());
                } else {
                    Log.e(LOG_TAG, "Server response string buffer was null.");
                }
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Unable to parse given date", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Unable to parse returned JSON", e);
            } catch (TimeclockException e) {
                Log.e(LOG_TAG, "Timeclock server error", e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "A problem occured.", e);
            }

            return cs;
        }

        @Override
        protected void onPostExecute(ClockStamp cs) {
            super.onPostExecute(cs);

            if(cs != null) {
                mClockStamp = cs;
                // TODO refresh UI
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public class FetchTimeclockTask extends AsyncTask<String, Void, ClockStamp> {
        private final String LOG_TAG = this.getClass().getSimpleName();

        @Override
        protected ClockStamp doInBackground(String... params) {
            Log.i(LOG_TAG, "Running clock task...");

            if(params.length != 1) {
                return null;
            }

            ClockStamp cs = null;
            try {
                StringBuffer buffer = TimeclockActions.getLastRecord(params[0]);
                if(buffer != null) {
                    cs = TimeclockActions.parseServerResponse(buffer.toString());
                } else {
                    Log.e(LOG_TAG, "Server response string buffer was null.");
                }
            } catch (ParseException e) {
                Log.e(LOG_TAG, "Unable to parse given date", e);
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Unable to parse returned JSON", e);
            } catch (TimeclockException e) {
                Log.e(LOG_TAG, "Timeclock server error", e);
            } catch (IOException e) {
                Log.e(LOG_TAG, "A problem occured.", e);
            }

            return cs;
        }

        @Override
        protected void onPostExecute(ClockStamp cs) {
            super.onPostExecute(cs);

            if(cs != null) {
                mClockStamp = cs;
                // TODO refresh UI
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.fetch_error, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
