/*
 *	AkingAdapter.java
 *  Description: This is a customized ArrayAdapter<String> class to be used
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 02/05/2014
 *  
 *  Got help from http://www.vogella.com/articles/AndroidListView/article.html
 */

package com.cheesecake.ififitsisits;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AkingSimpleAdapter extends ArrayAdapter<String> {						//Start of class AkingAdapter
	private Context context;
	private String[] contents;

	public AkingSimpleAdapter(Context context, String[] contents) {	//Constructor for AkingSimpleAdapter
		
		super(context, R.layout.rowlayout, contents);	//Initialize values
		this.context = context;
		this.contents = contents;

	
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		//Override of getView()
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);	//Initialize
		View rowView = inflater.inflate(R.layout.rowlayout, parent, false);
		
		TextView textView = (TextView) rowView.findViewById(R.id.list_text);	//Retrieve views
		
		textView.setText(this.contents[position]);	//Modify subject # textview
		
		return rowView;
		
	}
	
	
	
} 
