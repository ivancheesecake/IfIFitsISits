/*
 * ViewRecordFragment.java
 * Description: This fragment allows the researcher to view a list of saved data.
 * Author: Escamos, Ivan Marc H. 
 * Date last modified: 04/10/14
 * 
 * */

package com.cheesecake.ififitsisits;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
 
public class ViewRecordsFragment extends Fragment {		//start of class ViewRecordsFragment
	
	public String[] stringIds;
	public Integer ids[];
	public static List<Record> records;
	public static List<Integer> queue;
	public static DatabaseHelper db;
	
    public static Fragment newInstance(Context context) {
        ViewRecordsFragment f = new ViewRecordsFragment();
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {	
        View v = (View) inflater.inflate(R.layout.viewrecords_fragment, container,false);
        MainActivity.actionbar.setSubtitle("Manage Database");
        final ListView listview = (ListView) v.findViewById(R.id.listview_allrecords);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        String projectId = prefs.getString("projectId", "default");
        
        db = new DatabaseHelper(getActivity());			//retrieve necessary data
        records = db.getAllRecords(projectId);
		queue = db.getQueue();
        
        stringIds = new String[records.size()];
		ids = new Integer[records.size()];
		
		for(int i=0; i<records.size(); i++){			//prepare data needed for adapter
			
			ids[i] = records.get(i).get_id();
			stringIds[i] =""+ids[i];
			
			}
		
		AkingAdapter adapter = new AkingAdapter(getActivity(),stringIds,queue);		//set the adapter
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {				//handle list selections
		    public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
		        {
		    	ViewRecordFragment.r = records.get(itemPosition);			//set the record to be viewed
		    	FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
		    	tx.replace(R.id.main, Fragment.instantiate(getActivity(), MainActivity.fragments[6])).addToBackStack( "tag" );		//change fragments
		    	tx.commit();
		        }
		    });
		
		
        return v;
    }
    
  
    
 
}
