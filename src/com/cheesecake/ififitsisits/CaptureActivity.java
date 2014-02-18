/*
 *	CaptureActivity.java
 *  Description: This activity accesses the mobile device's camera and allows the application to capture the current frame. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 02/05/14
 *  
 *  Got help from OpenCV Samples
 *  http://stackoverflow.com/questions/14693558/unsatisfiedlinkerror-n-mat-while-using-opencv2-4-3-with-android-4-0
 */


package com.cheesecake.ififitsisits;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Toast;

public class CaptureActivity extends Activity implements CvCameraViewListener2,OnTouchListener{		//Start of class CaptureActivity

	private static final String TAG = "CaptureActivity";				
	public final static String EXTRA_BMP = "com.cheesecake.ififitsisits.BMP";
	public final static String EXTRA_CAPTURE_FLAG= "com.cheesecake.ififitsisits.CAPTURE_FLAG";
    private CameraView mOpenCvCameraView;
    private Mat mIntermediateMat,overlayMat,rgba,promptMat;
    private int screen_width;
    private int screen_height;
    private int flag;
    private Bitmap overlay,side,prompt;
    private boolean ready = false;
    
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {		//Async initialization of OpenCV
        
    	@Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
                    System.loadLibrary("ififits_native");							//Load native library
                    
                    mOpenCvCameraView.enableView();									//Enable camera view
                    mOpenCvCameraView.setOnTouchListener(CaptureActivity.this);
                    promptMat = new Mat();
                    if(flag == 0)
                    	prompt = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.prompt_side);
                    else
                    	prompt = BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.prompt_front);
                    prompt = Bitmap.createScaledBitmap(prompt, screen_width/2, screen_height, false);
                    Utils.bitmapToMat(prompt, promptMat);
                    
                    
                } break;
               
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public CaptureActivity() {											//Constructor for CaptureActivity
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {		//Override of onCreate()
    	
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_capture);

        Intent intent = getIntent();
        flag = intent.getIntExtra(MainActivity.EXTRA_CAPTURE_FLAG,0);
        
        DisplayMetrics displaymetrics = new DisplayMetrics();				//Get screen dimensions
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screen_height = displaymetrics.heightPixels;
        screen_width = displaymetrics.widthPixels;
        
       
        if(flag==1){
        	
        	side = intent.getParcelableExtra(DisplayActivity.EXTRA_SIDE_BMP);
        }
        
        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_view);	//Configure view
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
       
        
    }

    @Override
    public void onPause(){								//Override of onPause()
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume(){							//Override of onResume()
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);	//start Async initialization
    }
    
    @Override
    public void onDestroy() {					//Override of onDestroy()
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {		
    		
    	mIntermediateMat = new Mat();
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {		
       
    	mIntermediateMat = inputFrame.rgba();	//Retrieve current frame
    	rgba = new Mat();
        mIntermediateMat.copyTo(rgba);			//Store current frame	
        Overlay(mIntermediateMat.getNativeObjAddr(),promptMat.getNativeObjAddr());
        ready = true;
        return mIntermediateMat;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
       
        return true;
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {		//Override of onTouch(), triggered when a touch event is detected
    	if(ready){
        Mat cropped = new Mat(rgba, new Rect(new Point(rgba.width()/2,0), new Point(rgba.width()-1,rgba.height()-1)));
      //  Mat keyed = new Mat(cropped.size(), CvType.CV_8UC3);
        	
        //Keyer(cropped.getNativeObjAddr(),keyed.getNativeObjAddr());		//Perform Chroma Keying
        
        Bitmap bmp = Bitmap.createBitmap(cropped.width(), cropped.height(), Bitmap.Config.ARGB_8888);	//Initialize Bitmap

        Utils.matToBitmap(cropped, bmp);		//Convert Mat to Bitmap
        
        Toast.makeText(this,"Please Wait.", Toast.LENGTH_SHORT).show();
        
        Intent intent = new Intent(this,DisplayActivity.class);		//Intent to DisplayActivity
        intent.putExtra(EXTRA_BMP, bmp);							//Pass Bitmap as Extra
        intent.putExtra(EXTRA_CAPTURE_FLAG, flag);
        if(flag == 1)
        	intent.putExtra(DisplayActivity.EXTRA_SIDE_BMP, side);
        startActivity(intent);
    	}
    	
        return false;
    }
    
    public native void Keyer(long src, long dst);				//Native function for keying
    public native void Overlay(long src, long ovr);
}
