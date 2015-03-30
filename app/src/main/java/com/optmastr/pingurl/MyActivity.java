package com.optmastr.pingurl;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

public class MyActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.optmastr.pingurl.URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Enable Chrome Debugging
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                0 != (getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
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
        if (id == R.id.action_settings) {
            return true;
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
            if (resultCode == 1) {
                Intent intent = new Intent(this, DisplayURLActivity.class);
                intent.putExtra(EXTRA_MESSAGE, link);
                startActivity(intent);
            } else {
            /* Malformed URL, need to notify */
                TextView textView = (TextView) findViewById(R.id.invalid_url_text);
                textView.setText("Invalid URL: " + link + ". Please check and re-enter!");
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
