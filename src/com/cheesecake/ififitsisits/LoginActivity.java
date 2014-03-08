package com.cheesecake.ififitsisits;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	SharedPreferences prefs;
	String authenticated;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
		authenticated = prefs.getString("authenticated", "false");
	        
	        Log.d("authenticated",authenticated);
	        
	      //  if(authenticated.equals("true")){
	        	Intent intent = new Intent(this,MainActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
	       // }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void login(View view){
		
		//check if app is authenticated
		
		Editor toEdit;
		
		EditText edit_authkey = (EditText) findViewById(R.id.edit_authkey);
		String authkey = edit_authkey.getText().toString();
		HttpPostHelper helper = new HttpPostHelper("http://192.168.0.103/SP/Main%20Program/android_authkey.php"); 	//get url from sharedpreferences
		
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authkey", authkey));
	
		if(helper.post(pairs)){
			toEdit = prefs.edit();
			toEdit.putString("authenticated", "true");
			toEdit.commit();
			
			authenticated = prefs.getString("authenticated", "false");
		    Log.d("authenticated",authenticated);
			
			Intent intent = new Intent(this,MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			
		}
		else{
			Toast.makeText(getApplicationContext(), "Invalid Authentication Key.", Toast.LENGTH_SHORT).show();
		}
		
	}
}
