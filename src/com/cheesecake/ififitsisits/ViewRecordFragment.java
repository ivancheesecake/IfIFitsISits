package com.cheesecake.ififitsisits;

import java.io.File;
import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewRecordFragment extends Fragment{

	public static Record r;
	public static Fragment newInstance(Context context) {
        ViewRecordFragment f = new ViewRecordFragment();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.viewrecord_fragment, null);
        MainActivity.actionbar.setSubtitle("View Record");
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		int screen_height = displaymetrics.heightPixels;
		int screen_width = displaymetrics.widthPixels;
		
		File extStore = Environment.getExternalStorageDirectory();
		String path = extStore.getAbsolutePath();
		
		Bitmap side = BitmapFactory.decodeFile(path+"/ififits/"+r.get_sideImg());
		Bitmap back = BitmapFactory.decodeFile(path+"/ififits/"+r.get_backImg());
		
		ImageView imageview_side = (ImageView) v.findViewById(R.id.imageview_side);
		ImageView imageview_back = (ImageView) v.findViewById(R.id.imageview_back);
		
		int image_width = (int)screen_width/2;
		int image_height = (int)screen_height/2;
		
		if(image_width >image_height){
			int temp = image_height;
			image_height = image_width;
			image_width = temp;
		}
		
		imageview_side.setImageBitmap(Bitmap.createScaledBitmap(side, image_width, image_height, false));
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
		TextView textview_locationVal = (TextView) v.findViewById(R.id.textview_locationVal);
		TextView textview_dateVal = (TextView) v.findViewById(R.id.textview_dateVal);
		
		DecimalFormat df = new DecimalFormat("#.00");
		
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
		textview_locationVal.setText(r.get_region());
		textview_dateVal.setText(r.get_date());
        
        return v;
    }
	
}