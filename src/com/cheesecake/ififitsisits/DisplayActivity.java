package com.cheesecake.ififitsisits;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class DisplayActivity extends ActionBarActivity {
	static Bitmap side,front,sideIntent,sideDraw,frontDraw;
	ImageView imageview,imageview2;
	Mat sideMat,keyedMat,frontMat,keyedMat2, sideMatDisp, frontMatDisp;
	float actualDimensions;
	static Record r;
	Bitmap bmp;
	int flag;
	public final static String EXTRA_RECORD = "com.cheesecake.ififitsisits.RECORD";
	public final static String EXTRA_CAPTURE_FLAG= "com.cheesecake.ififitsisits.CAPTURE_FLAG";
	public final static String EXTRA_SIDE_BMP= "com.cheesecake.ififitsisits.SIDE_BMP";
	public final static String EXTRA_FRONT_BMP= "com.cheesecake.ififitsisits.FRONT_BMP";
	@Override
	
	protected void onCreate(Bundle savedInstanceState){
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display);
		System.loadLibrary("ififits_native");
		Intent intent = getIntent();

		
		bmp = (Bitmap) intent.getParcelableExtra(CaptureActivity.EXTRA_BMP);
		imageview = (ImageView) findViewById(R.id.display);
		//imageview2 = (ImageView) findViewById(R.id.display2);

		imageview.setImageBitmap(bmp);
		
		flag = intent.getIntExtra(MainActivity.EXTRA_CAPTURE_FLAG,0);
		
		sideMat = new Mat();
		keyedMat = new Mat();
			
		side = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.side);
		
		Utils.bitmapToMat(side, sideMat);
		Keyer(sideMat.getNativeObjAddr(),keyedMat.getNativeObjAddr());
		Utils.matToBitmap(keyedMat, side);
		Utils.bitmapToMat(side, sideMat);//
		//imageview2.setImageBitmap(Bitmap.createScaledBitmap(side, 150, 200, false));
			

		if(flag==1){
			
			frontMat = new Mat();
			keyedMat2 = new Mat();
			sideIntent = intent.getParcelableExtra(EXTRA_SIDE_BMP);
			front = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.front);
			
			Utils.bitmapToMat(front, frontMat);
			Keyer(frontMat.getNativeObjAddr(),keyedMat2.getNativeObjAddr());
			Utils.matToBitmap(keyedMat2, front);
			Utils.bitmapToMat(front, frontMat);//
			//imageview2.setImageBitmap(Bitmap.createScaledBitmap(front, 150, 200, false));
			
		}
		SharedPreferences prefs = this.getSharedPreferences("com.cheesecake.ififits", Context.MODE_PRIVATE);
		actualDimensions = prefs.getFloat("com.cheesecake.ififits.refobject", (float)10.0);
		Log.d("Actual Dimensions",actualDimensions+"");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.display, menu);
		return true;
	}
	
	
	
	public void useOther(View view){
		double [] measurements = new double[9];
		//Log.d("Measurements", ""+r.get_sitH());
		
		
		if(flag==0){
			Intent intent = new Intent(this,CaptureActivity.class);
			intent.putExtra(EXTRA_SIDE_BMP, bmp);
			//side = bmp;
			intent.putExtra(EXTRA_CAPTURE_FLAG, 1);
			startActivity(intent);
		}
		else{
		
		r = new Record();
		//MeasureSide(keyedMat.getNativeObjAddr(),sideMat.getNativeObjAddr(), measurements);
		DeriveData(keyedMat.getNativeObjAddr(),sideMat.getNativeObjAddr(), keyedMat2.getNativeObjAddr(), frontMat.getNativeObjAddr(),measurements,actualDimensions);
		
		r.set_sitH(measurements[0]);
		r.set_pH(measurements[1]);
		r.set_tC(measurements[2]);
		r.set_bpL(measurements[3]);
		r.set_kH(measurements[4]);
		r.set_erH(measurements[5]);
		r.set_sH(measurements[6]);
		r.set_hB(measurements[7]);
		r.set_kkB(measurements[8]);
		
		//sideDraw = Bitmap.createBitmap(sideMat.width(), sideMat.height(), Bitmap.Config.ARGB_8888);	//Initialize Bitmap
		//frontDraw = Bitmap.createBitmap(frontMat.width(), frontMat.height(), Bitmap.Config.ARGB_8888);	//Initialize Bitmap
		
		//Log.d("sideMat",sideMat.cols()+"");
		///Log.d("sideMat",sideIntent.getWidth()+"");
		//Utils.matToBitmap(sideMat, sideDraw);
		//Utils.matToBitmap(frontMat, frontDraw);
		
		
		Intent intent = new Intent(this,ViewInsertActivity.class);
		intent.putExtra(EXTRA_SIDE_BMP, sideDraw);
		intent.putExtra(EXTRA_FRONT_BMP, frontDraw);
		intent.putExtra(EXTRA_RECORD,r);
		
		startActivity(intent);
		
		}
	}
	
	public native void Keyer(long src, long dst);
    public native void MeasureSide(long src, long dst,double [] sideArray);
    public native void DeriveData(long src,long dst,long src2,long dst2,double [] measurements, double actualDimensions);
}
