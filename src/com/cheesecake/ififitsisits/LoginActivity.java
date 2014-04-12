/*
 *	LoginActivity.java
 *  Description: This activity serves as the Login Screen of the application.  
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 04/10/14
 *  
 */

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
	        
	        if(authenticated.equals("true")){			//Check if the user is already logged in
	        	
	        	Toast.makeText(this, "Welcome, Researcher!", Toast.LENGTH_LONG).show();
	        	Intent intent = new Intent(this,ProjectActivity.class);
	        	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);		//fire intent to the next activity
				finish();
				
	        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	public void login(View view){			//callback function when the login button is pressed
		
		Editor toEdit;				//prepare data for request
		
		toEdit = prefs.edit();
		toEdit.putString("url", getString(R.string.url));
		toEdit.commit();
		
		EditText edit_authkey = (EditText) findViewById(R.id.edit_authkey);
		String authkey = edit_authkey.getText().toString();
		EditText edit_password = (EditText) findViewById(R.id.edit_password);
		String password = edit_password.getText().toString();
		
		HttpPostHelper helper = new HttpPostHelper(getString(R.string.url)+"/android_authkey.php"); 	
		
		Log.d("url",getString(R.string.url)+"/android_authkey.php");
		
		ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
		pairs.add(new BasicNameValuePair("authkey", authkey));
		pairs.add(new BasicNameValuePair("password", password));
	
		if(helper.post(pairs)){			//perform request, if login is successful
			
			toEdit.putString("authenticated", "true");
			toEdit.putString("onProject", "false");
			toEdit.putString("authkey", authkey);
			toEdit.commit();
			
			authenticated = prefs.getString("authenticated", "false");
		    Log.d("authenticated",authenticated);
			
		    Toast.makeText(this, "Welcome, Researcher!", Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this,ProjectActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);		//fire intent to next activity
			finish();
			
		}
		else{		
			Toast.makeText(getApplicationContext(), "Login Failed.", Toast.LENGTH_SHORT).show(); 
		}
		
	}
	
}
