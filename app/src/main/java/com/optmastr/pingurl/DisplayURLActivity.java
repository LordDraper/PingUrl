package com.optmastr.pingurl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class DisplayURLActivity extends ActionBarActivity {

    private Menu _menu = null;

    // Since the webview activity is not tracked, not yet know when to call the following.
    public void setDefaultButtonStates() {
        if (null != _menu) {
            WebView webView = (WebView) findViewById(R.id.display_url_viewer);
            for (int i = 0; i < _menu.size(); ++i) {
                MenuItem menuItem = _menu.getItem(i);
                switch (menuItem.getItemId()) {
                    case R.id.action_back:
                        menuItem.setEnabled(webView.canGoBack());
                        break;
                    case R.id.action_next:
                        menuItem.setEnabled(webView.canGoForward());
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_url);

        Intent intent = getIntent();
        String url = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        WebView webView = (WebView) findViewById(R.id.display_url_viewer);

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSaveFormData(false);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onFormResubmission(WebView view, Message dontResend, Message resend) {
                resend.sendToTarget();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                ((DisplayURLActivity) view.getContext()).setDefaultButtonStates();
                super.onPageFinished(view, url);
            }
        });

        webView.requestFocus(View.FOCUS_DOWN);
        webView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        webView.loadUrl(url);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        WebView webView = (WebView) findViewById(R.id.display_url_viewer);

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                webView.reload();
                break;
            case R.id.action_back:
                if (webView.canGoBack()) {
                    webView.goBack();
                }
                break;
            case R.id.action_next:
                if (webView.canGoForward()) {
                    webView.goForward();
                }
                break;
            case R.id.action_clearcache:
                MyActivity.clearCache(this);
                break;
        }
        setDefaultButtonStates();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_url, menu);
        this._menu = menu;
        setDefaultButtonStates();
        return true;
    }

    @Override
    public void onBackPressed() {
        WebView webView = (WebView) findViewById(R.id.display_url_viewer);
        if (webView.canGoBack()) {
            webView.goBack();
            setDefaultButtonStates();
        } else {
            super.onBackPressed();
        }
    }
}
