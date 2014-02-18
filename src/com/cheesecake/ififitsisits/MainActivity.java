/*
 *	MainActivity.java
 *  Description: This activity serves as the "home screen" of the application. It provides navigation to the core functionalities of the application. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 02/05/14
 *  
 *  Got help from http://stackoverflow.com/questions/3141807/android-service-to-check-internet-connectivity
 *                http://www.recursiverobot.com/post/59404388046/implementing-the-new-navigation-drawer-in-android
 */

package com.cheesecake.ififitsisits;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
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
	private static boolean mayInternets=false;
	private int prevPosition =0;

	public final static String EXTRA_CAPTURE_FLAG= "com.cheesecake.ififitsisits.CAPTURE_FLAG";
	
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
	    
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
	    mDrawerList = (ListView) findViewById(R.id.left_drawer);
	    mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menuOptions));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.drawable.ic_drawer,R.string.drawer_open, R.string.drawer_close) {};
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        actionbar  = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeButtonEnabled(true);
        actionbar.setSubtitle("Home");
        
        db = new DatabaseHelper(this);
		ViewRecordsFragment.records = db.getAllRecords();
		
		FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(R.id.main,Fragment.instantiate(MainActivity.this, fragments[0]));
        tx.commit();
        
        SharedPreferences prefs = this.getSharedPreferences("com.cheesecake.ififits", Context.MODE_PRIVATE);
        prefs.edit().putFloat("com.cheesecake.ififits.refobject", (float)20.0);
        
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
 

    private void selectItem(int position) {
       
    	mDrawerList.setItemChecked(position, true);
   
        mDrawerLayout.closeDrawer(mDrawerList);
      
        if(position!=1){
        	FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        	tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[position])).addToBackStack("fragment_"+position);
        	FragmentManager fm = this.getSupportFragmentManager();
            fm.popBackStack ("fragment_"+prevPosition, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        	tx.commit();
        	
        	prevPosition = position;
        }
        else{
        	
        	Intent intent = new Intent(this,CaptureActivity.class);
        	
        	intent.putExtra(EXTRA_CAPTURE_FLAG, 0);
        	
            startActivity(intent);
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
    
    private void installListener() {

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

                    if (state == State.CONNECTED) {
                    	Log.d("Internet Connectivity", "May Internet");
                    	status_internets.setIcon(R.drawable.ic_action_network_wifi);
                    	mayInternets = true;
               		 
                    } else {
                    	Log.d("Internet Connectivity", "Walang Internet");
                    	status_internets.setIcon(R.drawable.ic_action_network_wifi_dark);
                    	mayInternets = false;

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
    

    
    
    /*DB management stuff*/
    
    
    public void upload(View view){
    	
    	if(mayInternets){
	    	for(int i=0; i<ViewRecordsFragment.queue.size(); i++){
	    		
	    		db.dequeue(ViewRecordsFragment.queue.get(i));
	    	}
	    	
	    	//perform upload
	    	
	    	//refresh fragment
	    	FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
	    	tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[2]));
	    	tx.commit();
	    	Toast.makeText(this, "Data Sent to Server.", Toast.LENGTH_SHORT).show();
    	}
    	else
    		Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
    }
    
    public void deleteAll(View view){
    	
    	// 1. Instantiate an AlertDialog.Builder with its constructor
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

    	// 2. Chain together various setter methods to set the dialog characteristics
    	builder.setMessage(R.string.confirm_delete_msg).setTitle(R.string.confirm_delete_title);
    	
    	
    	builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
            	
            	db.deleteAll();
            	
            	FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
            	tx.replace(R.id.main, Fragment.instantiate(MainActivity.this, fragments[2])).addToBackStack("fragment_2");
            	FragmentManager fm = MainActivity.this.getSupportFragmentManager();
                fm.popBackStack ("fragment_2", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            	tx.commit();
            	
            	Toast.makeText(MainActivity.this, "All Records Deleted.", Toast.LENGTH_SHORT).show();
            }
        });
    	
    	builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });
    	
    	// 3. Get the AlertDialog from create()
    	AlertDialog dialog = builder.create();
    	dialog.show();
    }
    
    public static boolean mayInternetsBa(){
    	
    	return mayInternets;
    	
    }
    
}
