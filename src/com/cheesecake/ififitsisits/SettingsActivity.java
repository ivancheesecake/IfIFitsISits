package com.cheesecake.ififitsisits;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		 SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
		 String refObj = prefs.getString("refObj", "20.0");
		 String url = prefs.getString("url", "http://192.168.0.103/SP/Main%20Program");
		 
		 EditTextPreference refObjPreference = (EditTextPreference) findPreference("refObj");
		 EditTextPreference urlPreference = (EditTextPreference) findPreference("url");
		 
		 refObjPreference.setText(refObj);
		 urlPreference.setText(url);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
