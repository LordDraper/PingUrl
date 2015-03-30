package com.optmastr.pingurl;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class URLCheckerIntentService extends IntentService {

    public static final String PENDING_RESULT_EXTRA = "pending_result";
    public static final int URL_CHECK_CODE = 1000;
    public static final String URL_EXTRA = "url";

    private static Boolean checkURLConnection(String url) {
        Boolean connected = false;

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setRequestMethod("HEAD");
            int responseCode = connection.getResponseCode();
            connected = (200 <= responseCode && responseCode <= 399);
        } catch (final MalformedURLException ex) {
        } catch (final Exception ex) {
        } finally {
            if (null != connection) {
                connection.disconnect();
            }
        }
        return connected;
    }

    public URLCheckerIntentService() {
        super(URLCheckerIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PendingIntent reply = intent.getParcelableExtra(PENDING_RESULT_EXTRA);
        try {
            String link = intent.getStringExtra(URL_EXTRA);

            Boolean validLink = checkURLConnection(link);
            if (!validLink) {
                ArrayList<String> prefixes = new ArrayList<>();
                prefixes.add("http://");
                prefixes.add("http://www.");
                prefixes.add("https://");
                prefixes.add("https://www.");

                for (String prefix : prefixes) {
                    validLink = checkURLConnection(prefix + link);
                    if (validLink) {
                        link = prefix + link;
                        break;
                    }
                }
            }

            Intent result = new Intent();
            result.putExtra(URL_EXTRA, link);
            reply.send(this, validLink ? 1 : 0, result);
        } catch (PendingIntent.CanceledException ex) {
            // Do not know how to handle.
        }
    }
}
