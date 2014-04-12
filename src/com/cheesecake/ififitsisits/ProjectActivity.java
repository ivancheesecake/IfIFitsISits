/*
 *	ProjectActivity.java
 *  Description: This activity serves as the "project selection screen" of the application. It allows the user to add and select projects to work on. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 104/10/14
 *  
 */

package com.cheesecake.ififitsisits;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProjectActivity extends ActionBarActivity {

	AlertDialog alertDialog;
	View promptsView;
	List<Project> projects;
	DatabaseHelper db;
	String projectNames[];
	Project p;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_project);
		
		db = new DatabaseHelper(this);
		prefs = PreferenceManager.getDefaultSharedPreferences(ProjectActivity.this);
    	
		projects = db.getAllProjects();
			
		projectNames = new String[projects.size()];
		for(int i=0; i<projects.size(); i++){
			
			Project p = projects.get(i);
			projectNames[i] = p.get_projectName();
		}
		
		setTitle("My Projects");
		
		
		String url = prefs.getString("url", "false");
		Editor toEdit;
    	toEdit = prefs.edit();
		
		
		if(mayInternets()){			//if a network connection is available
        
			HttpPostHelper helper = new HttpPostHelper(url+"/project_authenticated.php"); 	
            
        	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            
            Log.d("Projects.size",projects.size()+"");
            
            for(int i=0; i< projects.size(); i++){		//check if all current projects are still valid
            	
            	p = projects.get(i);
            	pairs.add(new BasicNameValuePair("projectkey", p.get_projectId()));
	            
            	if(!helper.post(pairs)){		//if the project is invalid, remove the project and all related data
	            		
	            	toEdit = prefs.edit();
	            	toEdit.putString("onProject", "false");
	    			toEdit.putString("projectId", "null");
	    			toEdit.commit();
	            	Toast.makeText(getApplicationContext(), "Project "+p.get_projectName()+ " is invalid.", Toast.LENGTH_SHORT).show();
	            	
	            	db.deleteProject(p.get_projectId());
	            	Log.d("deleted",p.get_projectName());
	    
	            }
            	
            }
            
            
    	}
		
		
		final ListView listview = (ListView) findViewById(R.id.listview_allprojects);			//display all current valid projects
		
		AkingSimpleAdapter adapter = new AkingSimpleAdapter(this,projectNames);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
		        {
		    	
		    	p = projects.get(itemPosition);
		    	
		    	Editor toEdit;
		    	toEdit = prefs.edit();
			    
			    	toEdit = prefs.edit();				 				//set sharedpreferences that define the project
	    			toEdit.putString("onProject", "true");    
	    			toEdit.putString("projectId", p.get_projectId());
	    			toEdit.putString("projectName", p.get_projectName());
	    			toEdit.putString("otherFields", p.get_otherFields());
	    			toEdit.commit();
	    			
	    			Intent intent = new Intent(ProjectActivity.this,MainActivity.class);	//fire intent to main activity
	    			startActivity(intent);
		    		
		        }
		    });
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project, menu);
		return true;
	}

	public void addProject(View view){			//function that displays an alertdialog for adding projects
		
		LayoutInflater li = LayoutInflater.from(this);
		promptsView = li.inflate(R.layout.project_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setCancelable(false);
		alertDialog = alertDialogBuilder.create();
		
		alertDialog.show();
	}
	
	public void authenticateProject(View view){		//function for adding projects
    	
    	EditText userInput = (EditText) promptsView.findViewById(R.id.projectId_Input);
    	String input =  userInput.getText().toString();
    	
    	if(mayInternets()){		//if a network connection is available
    		
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ProjectActivity.this);
    		String url = prefs.getString("url", "http://192.168.0.101/SP/Main%20Program");
    		String responseRaw;
    		String response[] = new String[2];
    		
    		HttpPostHelper helper = new HttpPostHelper(url+"/project_authenticated.php"); 	
            ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("projectkey", input));
            
            if(helper.post_string(pairs).compareTo("ERR")!=0){		//if the project is valid
            	
            	Project p;							
            	responseRaw = helper.response;			//get the response string
            	
            	response = responseRaw.split("\\[");		//parse for the project name and other field names if they exist
            	
            	if(response.length > 1){
	            	response[1] = response[1].replace("[", "");
	            	response[1] = response[1].replace("]", "");
	            	response[1] = response[1].replace("\"", "");
	            	p = new Project(input,response[0],response[1]);
	            	
            	}
            	else{										//else, create a project with no other field names
            		p = new Project(input,response[0],"OK");	
            	}
    			
    			db.addProject(p);		//insert the new project to the database
    			Toast.makeText(this, "Project \""+response[0]+"\" started!", Toast.LENGTH_SHORT).show();		//notify the user
    			alertDialog.dismiss();
    			
    			finish();				//refresh the activity
    			startActivity(getIntent());
            }
            else{
            	Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show();
            }
    		
    	}
    	else{
    		Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    	}
  
    }
	
	public boolean mayInternets() {
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	 }
	
	 public void exit(View view){
	    	alertDialog.dismiss();
	    }
}
