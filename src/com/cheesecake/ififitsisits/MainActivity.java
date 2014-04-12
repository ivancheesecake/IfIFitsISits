/*
 *	MainActivity.java
 *  Description: This activity serves as the "home screen" of the application. It provides navigation to the core functionalities of the application. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 02/05/14
 *  
 *  Got help from http://stackoverflow.com/questions/3141807/android-service-to-check-internet-connectivity
 *                http://www.recursiverobot.com/post/59404388046/implementing-the-new-navigation-drawer-in-android
 *                http://www.mkyong.com/android/android-prompt-user-input-dialog-example/
 */

package com.cheesecake.ififitsisits;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {		//Start of class MainActivity

    private String[] menuOptions;
	private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private CharSequence mTitle;
	private ActionBarDrawerToggle mDrawerToggle;
	private MenuItem status_internets;
	private BroadcastReceiver broadcastReceiver;
	private DatabaseHelper db;
	public static ActionBar actionbar;
	private int prevPosition = 0;
	public static List<Integer> queue;
	public static List<Record> records;
	private String url,projectId,authkey;
	AlertDialog alertDialog;
	View promptsView;

	public final static String EXTRA_CAPTURE_FLAG= "com.cheesecake.ififitsisits.CAPTURE_FLAG";
	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	
	public final static String[] fragments = {
            "com.cheesecake.ififitsisits.HomeFragment",
            "com.cheesecake.ififitsisits.makakagraduateakohihi",
            "com.cheesecake.ififitsisits.ViewRecordsFragment",
            "com.cheesecake.ififitsisits.HelpFragment",
            "com.cheesecake.ififitsisits.SettingsFragment",
            "com.cheesecake.ififitsisits.AboutFragment",
            "com.cheesecake.ififitsisits.ViewRecordFragment"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main);
		
	    mTitle = getResources().getString(R.string.app_name);					
		menuOptions= getResources().getStringArray(R.array.drawer_options); 
	    
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);		//setup navigation drawer
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);
	    mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open, R.string.drawer_close) {};
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        actionbar  = getSupportActionBar();				//setup action bar
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setSubtitle("Home");
        
      
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();				//initialize user interface
        tx.replace(R.id.main,Fragment.instantiate(MainActivity.this, fragments[0]));
        tx.commit();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        
        url = prefs.getString("url", "http://192.168.0.101/SP/Main%20Program");		//get sharedpreferences
        projectId = prefs.getString("projectId", "default");
        authkey = prefs.getString("authkey", "default");
        
        db = new DatabaseHelper(this);						//setup records
      	ViewRecordsFragment.records = db.getAllRecords(projectId);
      		  
	}
	
	@Override
	protected void onResume() {

		super.onResume();
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	
		 MenuInflater inflater = getMenuInflater();
		 inflater.inflate(R.menu.main_activity_actions, menu);
		 status_internets = (MenuItem) menu.findItem(R.id.status_internet);
		 installListener();
		 
		 return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
     
        mDrawerToggle.syncState();
        
    }
 
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
 
        return super.onOptionsItemSelected(item);
    }
 

    private void selectItem(int position) {			//provides navigation to the functionalities of the application
       
    	mDrawerList.setItemChecked(position, true);
   
        mDrawerLayout.closeDrawer(mDrawerList);
      
        if(position==1){					
        	
        	Intent intent = new Intent(this,CameraActivity.class);
        	IfIFitsExtra extra = new IfIFitsExtra();
        	extra.set_flag(0);
    
        	intent.putExtra(EXTRA_IFIFITS, extra);
        	
            startActivity(intent);
        }
        else if(position==4){
        	
        	Intent intent = new Intent(this,SettingsActivity.class);
        	startActivity(intent);
        }
        else{
        	FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        	tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[position])).addToBackStack("fragment_"+position);
        	FragmentManager fm = this.getSupportFragmentManager();
            fm.popBackStack ("fragment_"+prevPosition, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        	tx.commit();
        	
        	prevPosition = position;
        }
        
        
    }
 
    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }
 
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
          
        }
    }
    
    
    private void installListener() {		//detects the state of the network connection of the mobile device

        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {

                    Bundle extras = intent.getExtras();

                    NetworkInfo info = (NetworkInfo) extras
                            .getParcelable("networkInfo");

                    State state = info.getState();
                    Log.d("InternalBroadcastReceiver", info.toString() + " "
                            + state.toString());

                    if (state == State.CONNECTED) {			//updates the icon on the top right of the action bar
                    	Log.d("Internet Connectivity", "May Internet");
                    	status_internets.setIcon(R.drawable.ic_action_network_wifi);

                    	
               		 
                    } else {
                    	Log.d("Internet Connectivity", "Walang Internet");
                    	status_internets.setIcon(R.drawable.ic_action_network_wifi_dark);

                    }

                }
            };

            final IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(broadcastReceiver, intentFilter);
        }
    }
    
    @Override
    public void onDestroy(){
    	
    	unregisterReceiver(broadcastReceiver);

    	super.onDestroy();
    }
    
    
    /*
     * Database management stuff 
     * 
     * */
    
    
    public void upload(View view){		//implementation of the bulk upload functionality
    	
    	HttpPostHelper helper = new HttpPostHelper(url+"/android_add_survey.php"); 	
    	ArrayList<NameValuePair> pairs;
 		boolean fail = false;
    	queue = db.getQueue();
 		
    	
    	if(mayInternets()){				//if a network connection is available
	    	for(int i=0; i<queue.size(); i++){		//run through the upload queue
	    		
	    		Record r = db.getRecord(queue.get(i));
	    		
	    		pairs = new ArrayList<NameValuePair>();			//prepare for request
	    		pairs.add(new BasicNameValuePair("authkey", authkey));
	    		pairs.add(new BasicNameValuePair("project_id", projectId));
				pairs.add(new BasicNameValuePair("survey_info_id", projectId+"-"+r.get_id()));
				pairs.add(new BasicNameValuePair("gender",  r.get_sex().substring(0,1)));
				pairs.add(new BasicNameValuePair("age",  r.get_age()+""));
				pairs.add(new BasicNameValuePair("height",  r.get_height()+""));
				pairs.add(new BasicNameValuePair("weight", r.get_weight()+""));
				pairs.add(new BasicNameValuePair("region",  r.get_region()));	
				pairs.add(new BasicNameValuePair("cityprov", r.get_cityprov()));	
				pairs.add(new BasicNameValuePair("body_measurement", "sitH:"+r.get_sitH()+","+
																	 "pH:"+r.get_pH()+","+
																	 "tC:"+r.get_tC()+","+
																	 "bkL:"+r.get_bpL()+","+
																	 "kH:"+r.get_kH()+","+
																	 "sH:"+r.get_sH()+","+
																	 "erH:"+r.get_erH()+","+
																	 "hB:"+r.get_hB()+","+
																	 "kkB:"+r.get_kkB()));
				pairs.add(new BasicNameValuePair("other_category",r.get_otherFields()));
			
				if(helper.post(pairs)){					//perform request
					Log.d("Uploaded",queue.get(i)+"");
					db.dequeue(queue.get(i));
			
			    	FragmentTransaction tx = getSupportFragmentManager().beginTransaction();		//refresh fragment
		        	tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[2])).addToBackStack("fragment_2");
		        	FragmentManager fm = MainActivity.this.getSupportFragmentManager();
		            fm.popBackStack ("fragment_2", FragmentManager.POP_BACK_STACK_INCLUSIVE);
		        	tx.commit();
			    	
					}
			
				else{
					fail = true;
				}
	    	}
	    	
	    	if(fail)			//display error messages if necessary
	    		Toast.makeText(this, "Not all data was successfully uploaded.", Toast.LENGTH_SHORT).show();
	    	else
	    		Toast.makeText(this, "Data Sent to Server.", Toast.LENGTH_SHORT).show();
    	}
    	else{
    		Toast.makeText(this, "Upload failed. No Internet Connection.", Toast.LENGTH_SHORT).show();
    		Log.d("AUTHKEY",authkey);
    		}
    	}
   
    
    public void exit(View view){
    	finish();
    }
    
    
    
    public boolean mayInternets() {			//function for checking network connection status
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	 }
    
}
