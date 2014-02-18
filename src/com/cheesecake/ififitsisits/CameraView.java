package com.cheesecake.ififitsisits;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class CameraView extends JavaCameraView implements PictureCallback {
	public final static String EXTRA_MAT = "com.cheesecake.ififitsisits.MAT";
	 private static final String TAG = "Sample::Tutorial3View";

	    public CameraView(Context context, AttributeSet attrs) {
	        super(context, attrs);
	    }

	    public List<String> getEffectList() {
	        return mCamera.getParameters().getSupportedColorEffects();
	    }

	    public boolean isEffectSupported() {
	        return (mCamera.getParameters().getColorEffect() != null);
	    }

	    public String getEffect() {
	        return mCamera.getParameters().getColorEffect();
	    }

	    public void setEffect(String effect) {
	    	
	        Camera.Parameters params = mCamera.getParameters();
	        params.setColorEffect(effect);
	        mCamera.setParameters(params);
	
	    }

	    public List<Size> getResolutionList() {
	    	
	        return mCamera.getParameters().getSupportedPreviewSizes();
	
	    }

	    public void setResolution(Size resolution) {
	      
	    	disconnectCamera();
	        mMaxHeight = resolution.height;
	        mMaxWidth = resolution.width;
	        connectCamera(getWidth(), getHeight());
	    
	    }

	    public Size getResolution() {
	        return mCamera.getParameters().getPreviewSize();
	    }

	    public void takePicture(final String fileName) {
	        
	    	Log.i(TAG, "Taking picture");
	        mCamera.setPreviewCallback(null);
	        mCamera.takePicture(null, null, this);
	   
	    }
	    
	    @Override
	    public void onPictureTaken(byte[] data, Camera camera) {
	        
	    	/*
	    	//Log.i(TAG, "Saving a bitmap to file");
	        // The camera preview was automatically stopped. Start it again.
	        //mCamera.startPreview();
	        //mCamera.setPreviewCallback(this);

	        // Write the image in a file (in jpeg format)
	        
	        try {
	            FileOutputStream fos = new FileOutputStream(mPictureFileName);

	            fos.write(data);
	            fos.close();

	        } catch (java.io.IOException e) {
	            Log.e("PictureDemo", "Exception in photoCallback", e);
	        }
	    	
	    	//Bitmap bmp = Bitmap.createBitmap(mIntermediateMat.width(), mIntermediateMat.height(), Bitmap.Config.ARGB_8888);
	        //Utils.matToBitmap(mIntermediateMat,bmp);
	    	//Bitmap bmp = BitmapFactory.decodeByteArray(data,0,data.length);
	    	*/
	    }
	    
}