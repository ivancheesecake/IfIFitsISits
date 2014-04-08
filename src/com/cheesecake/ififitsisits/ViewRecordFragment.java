/*
 * http://stackoverflow.com/questions/7693633/android-image-dialog-popup
 * http://www.dreamincode.net/forums/topic/331525-full-screen-dialog-with-scaled-image/
 * 
 * */

package com.cheesecake.ififitsisits;

import java.io.File;
import java.text.DecimalFormat;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewRecordFragment extends Fragment{

	public static Record r;
	Bitmap side,front,back;
	String path;
	View v,v2;
	public static Fragment newInstance(Context context) {
        ViewRecordFragment f = new ViewRecordFragment();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        v = (View) inflater.inflate(R.layout.viewrecord_fragment,container, false);
        v2 = (View) inflater.inflate(R.layout.image_dialog,container, false);
        
        MainActivity.actionbar.setSubtitle("View Record");
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		int screen_height = displaymetrics.heightPixels;
		int screen_width = displaymetrics.widthPixels;
		
		File extStore = Environment.getExternalStorageDirectory();
		path = extStore.getAbsolutePath();
		
		Log.d("Side: ",r.get_sideImg());
		Log.d("Front: ",r.get_frontImg());
		Log.d("Back: ",r.get_backImg());
		
		side = BitmapFactory.decodeFile(path+"/ififits/"+r.get_sideImg());
		front = BitmapFactory.decodeFile(path+"/ififits/"+r.get_frontImg());
		back = BitmapFactory.decodeFile(path+"/ififits/"+r.get_backImg());
		
		ImageView imageview_side = (ImageView) v.findViewById(R.id.imageview_side);
		ImageView imageview_front = (ImageView) v.findViewById(R.id.imageview_front);
		ImageView imageview_back = (ImageView) v.findViewById(R.id.imageview_back);
		
		imageview_side.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(getActivity());
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(side);
				settingsDialog.show();
				
			}
		});
		
		imageview_front.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(getActivity());
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(front);
				settingsDialog.show();
				
			}
		});
		
		imageview_back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(getActivity());
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(back);
				settingsDialog.show();
				
			}
		});
		
		
		
		int image_width = (int)screen_width/3;
		int image_height = (int)screen_height/3;
		
		if(image_width >image_height){
			int temp = image_height;
			image_height = image_width;
			image_width = temp;
		}
		
		imageview_side.setImageBitmap(Bitmap.createScaledBitmap(side, image_width, image_height, false));
		imageview_front.setImageBitmap(Bitmap.createScaledBitmap(front, image_width, image_height, false));
		imageview_back.setImageBitmap(Bitmap.createScaledBitmap(back, image_width, image_height, false));
        
		
		
		TextView textview_subjectId = (TextView) v.findViewById(R.id.textview_subjectId);
		TextView textview_heightVal = (TextView) v.findViewById(R.id.textview_heightVal);
		TextView textview_weightVal = (TextView) v.findViewById(R.id.textview_weightVal);
		TextView textview_ageVal = (TextView) v.findViewById(R.id.textview_ageVal);
		TextView textview_sexVal = (TextView) v.findViewById(R.id.textview_sexVal);
		TextView textview_sitHVal = (TextView) v.findViewById(R.id.textview_sitHVal);
		TextView textview_sHVal = (TextView) v.findViewById(R.id.textview_sHVal);
		TextView textview_erHVal = (TextView) v.findViewById(R.id.textview_erHVal);
		TextView textview_tCVal = (TextView) v.findViewById(R.id.textview_tCVal);
		TextView textview_pHVal = (TextView) v.findViewById(R.id.textview_pHVal);
		TextView textview_kHVal = (TextView) v.findViewById(R.id.textview_kHVal);
		TextView textview_bpLVal = (TextView) v.findViewById(R.id.textview_bpLVal);
		TextView textview_hBVal = (TextView) v.findViewById(R.id.textview_hBVal);
		TextView textview_kkBVal = (TextView) v.findViewById(R.id.textview_kkBVal);
		TextView textview_cityprovVal = (TextView) v.findViewById(R.id.textview_cityprovVal);
		TextView textview_regionVal = (TextView) v.findViewById(R.id.textview_regionVal);
		TextView textview_dateVal = (TextView) v.findViewById(R.id.textview_dateVal);
		
		DecimalFormat df = new DecimalFormat("#.0000");
		
		textview_subjectId.setText("Subject #"+r.get_id());
		textview_heightVal.setText(df.format(r.get_height())+"cm");
		textview_weightVal.setText(df.format(r.get_weight())+"kg");
		textview_ageVal.setText(r.get_age()+" y/o");
		textview_sexVal.setText(r.get_sex());
		textview_sitHVal.setText(df.format(r.get_sitH())+" cm");
		textview_sHVal.setText(df.format(r.get_sH())+" cm");
		textview_erHVal.setText(df.format(r.get_erH())+" cm");
		textview_tCVal.setText(df.format(r.get_tC())+" cm");
		textview_pHVal.setText(df.format(r.get_pH())+" cm");
		textview_kHVal.setText(df.format(r.get_kH())+" cm");
		textview_bpLVal.setText(df.format(r.get_bpL())+" cm");
		textview_hBVal.setText(df.format(r.get_hB())+" cm");
		textview_kkBVal.setText(df.format(r.get_kkB())+" cm");
		textview_cityprovVal.setText(r.get_cityprov());
		textview_regionVal.setText(r.get_region());
		textview_dateVal.setText(r.get_date());
		
		LinearLayout ll = (LinearLayout) v.findViewById(R.id.other_fields);
		
		Log.d("Other Fields",r.get_otherFields());
		String otherFields[] = r.get_otherFields().split(",");
		TextView[] otherTvsHeader = new TextView[otherFields.length]; 
		TextView[] otherTvsContent = new TextView[otherFields.length]; 
		String otherFieldSplit[] = new String[2];
		
		for(int i=0; i< otherFields.length; i++){
			otherTvsHeader[i] = new TextView(getActivity());
			otherTvsContent[i] = new TextView(getActivity());
			
			otherFieldSplit = otherFields[i].split(":");
			otherTvsHeader[i].setText(otherFieldSplit[0]);
			otherTvsHeader[i].setTextSize(16);
			otherTvsHeader[i].setTextColor(getResources().getColor(R.color.black));
			otherTvsHeader[i].setPadding(5,5,5,5);
			
			otherTvsContent[i].setText(otherFieldSplit[1]);
			otherTvsContent[i].setTextSize(14);
			otherTvsContent[i].setTextColor(getResources().getColor(17170432));
			otherTvsContent[i].setPadding(5,0,0,0);
			
			ll.addView(otherTvsHeader[i]);
			ll.addView(otherTvsContent[i]);
			
		}
        
        return v;
    }
	
}
