package com.cheesecake.ififitsisits;

import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
 
public class ViewRecordsFragment extends Fragment {
	
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
        
        db = new DatabaseHelper(getActivity());
        records = db.getAllRecords();
		queue = db.getQueue();
        
        stringIds = new String[records.size()];
		ids = new Integer[records.size()];
		
		for(int i=0; i<records.size(); i++){
			
			ids[i] = records.get(i).get_id();
			stringIds[i] =""+ids[i];
			
			}
		
		//ArrayAdapter<?> adapter = new ArrayAdapter<String>(this.getActivity(), R.layout.list_item,names);
		AkingAdapter adapter = new AkingAdapter(getActivity(),stringIds,queue);
		
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> listView, View itemView, int itemPosition, long itemId)
		        {
		    	ViewRecordFragment.r = records.get(itemPosition);
		    	FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
		    	tx.replace(R.id.main, Fragment.instantiate(getActivity(), MainActivity.fragments[6])).addToBackStack( "tag" );
		    	tx.commit();
		        }
		    });
		
		
        return v;
    }
    
  
    
 
}
