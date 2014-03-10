package com.cheesecake.ififitsisits;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class CaptureActivity2 extends Activity implements CvCameraViewListener2,OnTouchListener{

	private CameraView mOpenCvCameraView;
	private static final String TAG = "CaptureActivity";
	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	public final static String EXTRA_IFIFITS_BITMAPS= "com.cheesecake.ififitsisits.IFIFITS_BITMAPS";
	
	private IfIFitsExtra extra;
	private Bitmap[] extraBitmaps;
	private Mat mIntermediateMat;
	private Mat originalMat;
	private Mat promptMat;
	private Mat croppedMat;
	private Bitmap croppedBitmap,origBitmap;
	private int screen_width,screen_height;
	private int flag;
	private boolean ready;

	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.capture_activity2, menu);
		return true;
	}
	
	private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {		//Async initialization of OpenCV
        
    	@Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    
                    System.loadLibrary("ififits_native");							//Load native library
                    
                    mOpenCvCameraView.enableView();									//Enable camera view
                    mOpenCvCameraView.setOnTouchListener(CaptureActivity2.this);
                    originalMat = new Mat();
                    
                    DisplayMetrics displaymetrics = new DisplayMetrics();				//Get screen dimensions
                    getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                    screen_height = displaymetrics.heightPixels;
                    screen_width = displaymetrics.widthPixels;
                    
                    try{
                    	if(flag==0)
                    		promptMat = Utils.loadResource(getApplicationContext(), R.drawable.prompt_side, Highgui.CV_LOAD_IMAGE_COLOR);
                    	else if(flag==1)
                    		promptMat = Utils.loadResource(getApplicationContext(), R.drawable.prompt_front, Highgui.CV_LOAD_IMAGE_COLOR);
                    	else
                    		promptMat = Utils.loadResource(getApplicationContext(), R.drawable.prompt_back, Highgui.CV_LOAD_IMAGE_COLOR);
                    }catch(IOException e){
                    
                    	promptMat = new Mat();
                    	Log.d("Prompt Image","Loading Failed.");
                    
                    }
                    
                    Imgproc.resize(promptMat,promptMat, new Size(screen_width/2,screen_height));
           
                    
                } break;
               
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
            
        }
    	
    	
    };
	
    public CaptureActivity2() {											//Constructor for CaptureActivity
        Log.i(TAG, "Instantiated new " + this.getClass());
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {		//Override of onCreate()
    	
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        setContentView(R.layout.activity_capture);
        
        mOpenCvCameraView = (CameraView) findViewById(R.id.camera_view);	//Configure view
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
      
        Intent intent = getIntent();
        extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);
        
        flag = extra.get_flag();
        if(extra.get_flag()==0)
        	extraBitmaps = new Bitmap[3];
        else{
        	Parcelable[] ps = intent.getParcelableArrayExtra(EXTRA_IFIFITS_BITMAPS);	
    		extraBitmaps = new Bitmap[ps.length];
    		System.arraycopy(ps, 0, extraBitmaps, 0, ps.length);
        	
        }
        	
        ready = false;
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
        mIntermediateMat.copyTo(originalMat);			//Store current frame	
        
        Overlay(mIntermediateMat.getNativeObjAddr(),promptMat.getNativeObjAddr());
        ready = true;
        
        return mIntermediateMat;

    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {		//Override of onTouch(), triggered when a touch event is detected
    	
    	if(ready){
    		 
    		String fileName = Environment.getExternalStorageDirectory().getPath() +
    	                               "/cache.jpg";
    	    
    	    //mOpenCvCameraView.takePicture(fileName);
    		
    	//	Utils.bitmapToMat(origBitmap, originalMat);
    		
    		croppedMat = new Mat(originalMat, new Rect(new Point(originalMat.width()/2,0), new Point(originalMat.width()-1,originalMat.height()-1)));
    	    croppedBitmap = Bitmap.createBitmap(croppedMat.width(), croppedMat.height(), Bitmap.Config.ARGB_8888);	//Initialize Bitmap
    	    Utils.matToBitmap(croppedMat, croppedBitmap);		//Convert Mat to Bitmap 
    	    //extra.set_bitmap(croppedBitmap,extra.get_flag());
    		extraBitmaps[extra.get_flag()] = croppedBitmap;
    		Log.d("Flag",extra.get_flag()+"");
    	    //extra.next_flag();
    		
    		Intent intent = new Intent(this,DisplayProcessActivity.class);
    		intent.putExtra(EXTRA_IFIFITS, extra);
    		intent.putExtra(EXTRA_IFIFITS_BITMAPS, extraBitmaps);
    		startActivity(intent);
    		
    	}
    	return false;
    }
	
    public native void Overlay(long src, long ovr);

}
