/*
 *	AkingAdapter.java
 *  Description: This is a customized ArrayAdapter<String> class.
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 02/05/2014
 *  
 *  Got help from http://www.vogella.com/articles/AndroidListView/article.html
 */

package com.cheesecake.ififitsisits;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AkingAdapter extends ArrayAdapter<String> {						//Start of class AkingAdapter
	private Context context;
	private List<Integer> uploaded;
	private String[] ids;

	public AkingAdapter(Context context, String[] ids,  List<Integer> uploaded) {	//Constructor for AkingAdapter
		
		super(context, R.layout.rowlayout, ids);	//Initialize values
		this.context = context;
		this.ids = ids;
		this.uploaded = uploaded;
	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		//Override of getView()
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	//Initialize
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		
		TextView textView = (TextView) rowView.findViewById(R.id.list_text);	//Retrieve views
		ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		
		textView.setText("Subject #"+ids[position]);	//Modify subject # textview
    
		if(this.uploaded.contains(Integer.parseInt(ids[position])))			//Modify icons for upload status
			imageView.setImageResource(R.drawable.ic_action_upload_not);
		else
			imageView.setImageResource(R.drawable.ic_action_accept);
		
		return rowView;
		
	}
	
} 
