package com.optmastr.pingurl;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingFragment()).commit();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


}
