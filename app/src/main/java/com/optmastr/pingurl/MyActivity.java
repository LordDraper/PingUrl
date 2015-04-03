package com.optmastr.pingurl;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MyActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.optmastr.pingurl.URL";
    public final static String SAVED_URLS = "com.optmastr.pingurl.SAVED_URLS";

    public final static int SAVED_URLS_SIZE = 5;

    private ArrayList<String> savedURLs = new ArrayList<>();
    private ArrayAdapter<String> listAdapter = null;
    private ListView saveURLListView = null;

    public static void clearCache(ActionBarActivity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());
        SharedPreferences.Editor pe = prefs.edit();
        pe.clear();
        pe.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable Chrome Debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my);

        saveURLListView = (ListView) findViewById(R.id.save_urls);
        saveURLListView.clearChoices();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Set<String> saveURLValues = prefs.getStringSet(SAVED_URLS, null);
        if (null != saveURLValues) {
            // Restore the list in the ListView.
            savedURLs.addAll(saveURLValues);
            listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, savedURLs);

            saveURLListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String link = (String) saveURLListView.getItemAtPosition(position);

                    Intent intent = new Intent(saveURLListView.getContext(), DisplayURLActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, link);
                    startActivity(intent);
                }
            });

            saveURLListView.setAdapter(listAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_clearcache:
                MyActivity.clearCache(this);
                saveURLListView.clearChoices();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendMessage(View view) {
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String link = editText.getText().toString().trim();

        PendingIntent pendingResult = createPendingResult(URLCheckerIntentService.URL_CHECK_CODE, new Intent(), 0);

        Intent url_intent = new Intent(getApplicationContext(), URLCheckerIntentService.class);
        url_intent.putExtra(URLCheckerIntentService.URL_EXTRA, link);
        url_intent.putExtra(URLCheckerIntentService.PENDING_RESULT_EXTRA, pendingResult);

        startService(url_intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (URLCheckerIntentService.URL_CHECK_CODE == requestCode) {
            String link = data.getStringExtra(URLCheckerIntentService.URL_EXTRA);

            TextView textView = (TextView) findViewById(R.id.invalid_url_text);
            if (resultCode == 1) {
                textView.setText("");

                ListView saveURLListView = (ListView) findViewById(R.id.save_urls);

                ArrayList<String> newSavedURLs = new ArrayList<>();
                newSavedURLs.add(link);
                for (int i = 0; i < Math.min(savedURLs.size(), SAVED_URLS_SIZE - 1); ++i) {
                    String val = savedURLs.get(i);
                    if (!link.toLowerCase().equals(val.toLowerCase())) {
                        newSavedURLs.add(val);
                    }
                }
                savedURLs = newSavedURLs;

                listAdapter = new ArrayAdapter<>(this, R.layout.simplerow, savedURLs);
                saveURLListView.setAdapter(listAdapter);

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor pe = prefs.edit();
                pe.putStringSet(SAVED_URLS, new HashSet<>(savedURLs));
                pe.apply();

                Intent intent = new Intent(this, DisplayURLActivity.class);
                intent.putExtra(EXTRA_MESSAGE, link);
                startActivity(intent);
            } else {
            /* Malformed URL, need to notify */
                textView.setText("Invalid URL: " + link + ". Please check and re-enter!");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
