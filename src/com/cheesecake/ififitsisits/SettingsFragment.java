/*
 * Got help from: http://androiddev.orkitra.com/?p=80831
 * */

package com.cheesecake.ififitsisits;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
 
public class SettingsFragment extends PreferenceFragment {
 
    /*public static Fragment newInstance(Context context) {
        SettingsFragment f = new SettingsFragment();
 
        return f;
    }
 	*/
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	    View view = super.onCreateView(inflater, container, savedInstanceState);
	    view.setBackgroundColor(getResources().getColor(android.R.color.white));
	    
	        ListView lv = (ListView) view.findViewById(android.R.id.list);
	        lv.setPadding(0,0, 0, 0);
	    
	    return view;

	}
    @Override
    public void onCreate(Bundle savedInstanceState) {
     // TODO Auto-generated method stub
     super.onCreate(savedInstanceState);
     
     // Load the preferences from an XML resource
     addPreferencesFromResource(R.xml.preferences);
     
     MainActivity.actionbar.setSubtitle("Settings");
     
    }
    
   
}
