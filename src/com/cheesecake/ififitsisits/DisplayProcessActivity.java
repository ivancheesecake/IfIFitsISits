/*
 *	MainActivity.java
 *  Description: This activity performs the digital image processing for deriving anthropometric data and displays the result. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 04/10/14
 *  
 */


package com.cheesecake.ififitsisits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DisplayProcessActivity extends Activity {

	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	public final static String EXTRA_IFIFITS_BITMAPS= "com.cheesecake.ififitsisits.IFIFITS_BITMAPS";
	protected static final String EXTRA_IMAGE_PATH = "com.cheesecake.ififitsisits.IMAGEPATH";
	
	private IfIFitsExtra extra;	
	private Bitmap[] extraBitmaps;
	private Bitmap origBitmap, keyedBitmap,origBitmapCropped;
	private Mat origMat, origMat_copy;
	private Mat keyedMat, keyedMat_copy;
	private ImageView origImageView;
	private ImageView keyedImageView;
	private int flag;
	private double[] measurements;
	private float refObj;
	private String rawCachePath,filename,cachePaths[];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_process);
		
		System.loadLibrary("ififits_native");				//Load the native library
		
		Intent intent = getIntent();			//get the data from the previous activity
		extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);	
		rawCachePath = intent.getStringExtra(EXTRA_IMAGE_PATH); 
		cachePaths = extra.get_cachePaths();
		flag = extra.get_flag();
		measurements = extra.get_measurements();
		
		origBitmap = BitmapFactory.decodeFile(rawCachePath);   
		Bitmap.createScaledBitmap(origBitmap, (int)origBitmap.getWidth()/2, (int)origBitmap.getHeight()/2, false);
		origBitmapCropped = Bitmap.createBitmap(origBitmap, (int)origBitmap.getWidth()/2, 0, (int)origBitmap.getWidth()/2, (int)origBitmap.getHeight());
		
		origImageView = (ImageView) findViewById(R.id.display);
		origImageView.setImageBitmap(origBitmapCropped);
		
		origMat = new Mat();
		origMat_copy = new Mat();
		
		Utils.bitmapToMat(origBitmapCropped, origMat);
		origMat.copyTo(origMat_copy);
		keyedMat = new Mat();
		keyedMat_copy = new Mat();
		
		SimpleKeyer(origMat.getNativeObjAddr(),keyedMat.getNativeObjAddr());	//perform chroma keying

		keyedMat.copyTo(keyedMat_copy);
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(DisplayProcessActivity.this);
        refObj = Float.parseFloat(prefs.getString("refObj", "20.0"));
        
		DeriveData(keyedMat.getNativeObjAddr(), origMat.getNativeObjAddr(),measurements, refObj, flag); //derive measurements 
		Utils.matToBitmap(origMat, origBitmapCropped);
		
		File folder = new File(Environment.getExternalStorageDirectory() + "/ififits");	 //save processed image to sd card
		
		if (!folder.exists()) 
			folder.mkdir();
		
		filename = "cache-processed"+flag+".jpg";
		
		File file = new File(folder, filename);
		
		try{										
			FileOutputStream fOut = new FileOutputStream(file);
			origBitmapCropped.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
		    fOut.flush();
		    fOut.close();
		}
		catch(IOException e){};
		
		cachePaths[flag] = filename;

		
		extra.set_cachePaths(cachePaths);
	
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_process, menu);
		return true;
	}
	
	public void proceed(View view){		//fires intent to next capture or to ViewInsertActivity
		
		if(flag!=2){
			extra.next_flag();
			Intent intent = new Intent(this,CameraActivity.class);
			intent.putExtra(EXTRA_IFIFITS, extra);
			startActivity(intent);
			
		}
		else{
			Intent intent = new Intent(this,ViewInsertActivity.class);
			intent.putExtra(EXTRA_IFIFITS, extra);
			startActivity(intent);
		}
	}
	
	public void cancel(View view){  //fires intent to previous capture
		
		Intent intent = new Intent(this,CameraActivity.class);
		intent.putExtra(EXTRA_IFIFITS, extra);
		startActivity(intent);
		finish();
		
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();

        unbindDrawables(findViewById(R.id.displayProcessRoot));
        System.gc();
    }

    private void unbindDrawables(View view) {
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
	

	public native void SimpleKeyer(long src, long dst);			//Native methods
	public native void DeriveData(long src,long dst, double [] measurements, double actualDimensions, int flag);

}
