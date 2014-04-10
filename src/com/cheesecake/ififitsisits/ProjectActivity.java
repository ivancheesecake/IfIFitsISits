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
	//int projectIds[];
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
		
		
		//if(projects!=null)
			
		projectNames = new String[projects.size()];
		for(int i=0; i<projects.size(); i++){
			
			Project p = projects.get(i);
			projectNames[i] = p.get_projectName();
		}
		
		setTitle("My Projects");
		
		
		String url = prefs.getString("url", "false");
		Editor toEdit;
    	toEdit = prefs.edit();
		
		
		if(mayInternets()){
        
			HttpPostHelper helper = new HttpPostHelper(url+"/project_authenticated.php"); 	//get url from sharedpreferences
            
        	ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            
            Log.d("Projects.size",projects.size()+"");
            
            for(int i=0; i< projects.size(); i++){
            	
            	p = projects.get(i);
            	pairs.add(new BasicNameValuePair("projectkey", p.get_projectId()));
	            
            	if(!helper.post(pairs)){
	            		
	            	toEdit = prefs.edit();
	            	toEdit.putString("onProject", "false");
	    			toEdit.putString("projectId", "null");
	    			toEdit.commit();
	            	Toast.makeText(getApplicationContext(), "Project "+p.get_projectName()+ " is invalid.", Toast.LENGTH_SHORT).show();
	            	
	            	db.deleteProject(p.get_projectId());
	            	Log.d("deleted",p.get_projectName());
	    
	            }
            	
            	Log.d("Loop","loop");
            }
            
            
    	}
		
		
		final ListView listview = (ListView) findViewById(R.id.listview_allprojects);
		
		
		AkingSimpleAdapter adapter = new AkingSimpleAdapter(this,projectNames);
		listview.setAdapter(adapter);
		
		listview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
		        {
		    	
		    	p = projects.get(itemPosition);
		    	
		    	Editor toEdit;
		    	toEdit = prefs.edit();
		    	boolean tuloy = true;
		    	
		    	if(tuloy){
			    
			    	toEdit = prefs.edit();				 
	    			toEdit.putString("onProject", "true");    
	    			toEdit.putString("projectId", p.get_projectId());
	    			toEdit.putString("projectName", p.get_projectName());
	    			toEdit.putString("otherFields", p.get_otherFields());
	    			toEdit.commit();
	    			
	    			Intent intent = new Intent(ProjectActivity.this,MainActivity.class);
	    			startActivity(intent);
		    		}
		        }
		    });
		
	}
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.project, menu);
		return true;
	}

	public void addProject(View view){
		
		LayoutInflater li = LayoutInflater.from(this);
		promptsView = li.inflate(R.layout.project_dialog, null);
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setView(promptsView);
		alertDialogBuilder.setCancelable(false);
		alertDialog = alertDialogBuilder.create();
		
		alertDialog.show();
	}
	
	public void authenticateProject(View view){
    	
    	EditText userInput = (EditText) promptsView.findViewById(R.id.projectId_Input);
    	String input =  userInput.getText().toString();
    	
    	if(mayInternets()){
    		
    		Editor toEdit;
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ProjectActivity.this);
    		String url = prefs.getString("url", "http://192.168.0.101/SP/Main%20Program");
    		String responseRaw;
    		String response[] = new String[2];
    		
    		HttpPostHelper helper = new HttpPostHelper(url+"/project_authenticated.php"); 	//get url from sharedpreferences
            ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
            pairs.add(new BasicNameValuePair("projectkey", input));
            
            if(helper.post_string(pairs).compareTo("ERR")!=0){		//try ibalik sa helper.post
            	
            	Project p;
            	responseRaw = helper.response;
            	
            	response = responseRaw.split("\\[");
            	
            	
            	if(response.length > 1){
	            	response[1] = response[1].replace("[", "");
	            	response[1] = response[1].replace("]", "");
	            	response[1] = response[1].replace("\"", "");
	            	p = new Project(input,response[0],response[1]);
	            	
            	}
            	else{
            		p = new Project(input,response[0],"OK");	
            	}
    			
    			db.addProject(p);
    			Toast.makeText(this, "Project \""+response[0]+"\" started!", Toast.LENGTH_SHORT).show();
    			alertDialog.dismiss();
    			//Log.d("otherFields",response[1]);
    			
    			finish();
    			startActivity(getIntent());
            }
            else{
            	Toast.makeText(this, "Invalid Project ID", Toast.LENGTH_SHORT).show();
            }
    		
    	}
    	else{
    		Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
    	}
    	//alertDialog.dismiss();
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
