package com.cheesecake.ififitsisits;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class DisplayProcessActivity extends Activity {

	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	public final static String EXTRA_IFIFITS_BITMAPS= "com.cheesecake.ififitsisits.IFIFITS_BITMAPS";
	private IfIFitsExtra extra;	
	private Bitmap[] extraBitmaps;
	private Bitmap keyedBitmap;
	private Mat origMat, origMat_copy;
	private Mat keyedMat, keyedMat_copy;
	private ImageView origImageView;
	private ImageView keyedImageView;
	private int flag;
	private double[] measurements;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_process);
		
		System.loadLibrary("ififits_native");
		Intent intent = getIntent();

		extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);	
		
		Parcelable[] ps = intent.getParcelableArrayExtra(EXTRA_IFIFITS_BITMAPS);	
		extraBitmaps = new Bitmap[ps.length];
		System.arraycopy(ps, 0, extraBitmaps, 0, ps.length);
		flag = extra.get_flag();
		
		measurements = extra.get_measurements();
		
		origImageView = (ImageView) findViewById(R.id.display);
		origImageView.setImageBitmap(extraBitmaps[flag]);
		
		origMat = new Mat();
		keyedMat = new Mat();
		origMat_copy = new Mat();
		keyedMat_copy = new Mat();
		
		Utils.bitmapToMat(extraBitmaps[flag], origMat);
		origMat.copyTo(origMat_copy);
		Keyer(origMat.getNativeObjAddr(),keyedMat.getNativeObjAddr());	//perform keying
		keyedMat.copyTo(keyedMat_copy);
		DeriveData(keyedMat.getNativeObjAddr(), origMat.getNativeObjAddr(),measurements, 3.0, flag); //use sharedpreferences for refobj dimensions
		Utils.matToBitmap(origMat, extraBitmaps[flag]);
		
		keyedBitmap = Bitmap.createBitmap(origMat.width(), origMat.height(), Bitmap.Config.ARGB_8888);	//Initialize Bitmap
		Utils.matToBitmap(keyedMat_copy,keyedBitmap);
		keyedImageView = (ImageView) findViewById(R.id.display2);
		keyedImageView.setImageBitmap(keyedBitmap);
	}
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_process, menu);
		return true;
	}
	
	public void proceed(View view){
		
		if(flag!=2){
			extra.next_flag();
			Intent intent = new Intent(this,CaptureActivity2.class);
			intent.putExtra(EXTRA_IFIFITS, extra);
			intent.putExtra(EXTRA_IFIFITS_BITMAPS, extraBitmaps);
			startActivity(intent);
			
		}
		else{
			//extra.set_measurements(new double[] {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0});
			Intent intent = new Intent(this,ViewInsertActivity.class);
			intent.putExtra(EXTRA_IFIFITS, extra);
			intent.putExtra(EXTRA_IFIFITS_BITMAPS, extraBitmaps);
			startActivity(intent);
		}
	}
	
	public native void Keyer(long src, long dst);
	public native void DeriveData(long src,long dst, double [] measurements, double actualDimensions, int flag);
	
	

}
