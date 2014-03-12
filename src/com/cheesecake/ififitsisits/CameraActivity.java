package com.cheesecake.ififitsisits;

import static com.cheesecake.ififitsisits.camerautils.CameraHelper.cameraAvailable;
import static com.cheesecake.ififitsisits.camerautils.CameraHelper.getCameraInstance;
import static com.cheesecake.ififitsisits.camerautils.MediaHelper.getOutputMediaFile;
import static com.cheesecake.ififitsisits.camerautils.MediaHelper.saveToFile;

import java.io.File;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.cheesecake.ififitsisits.camerautils.CameraPreview;
 
/**
 * Takes a photo saves it to the SD card and returns the path of this photo to the calling Activity
 * @author paul.blundell
 *
 */
public class CameraActivity extends Activity implements PictureCallback {
 
    protected static final String EXTRA_IMAGE_PATH = "com.cheesecake.ififitsisits.IMAGEPATH";
    private Camera camera;
    private CameraPreview cameraPreview;
    private IfIFitsExtra extra;
    private static int flag;
    public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
    

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {		//Async initialization of OpenCV
        
    	@Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV Async Initialization", "OpenCV loaded successfully");
                    
                    System.loadLibrary("ififits_native");							//Load native library
           
                    
                } break;
               
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
            
        }
    	
    	
    };
    
    @Override
    public void onResume(){							//Override of onResume()
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);	//start Async initialization
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setResult(RESULT_CANCELED);
        // Camera may be in use by another activity or the system or not available at all
        camera = getCameraInstance();
        if(cameraAvailable(camera)){
            initCameraPreview();
        } else {
            finish();
        }
        
        Intent intent = getIntent();
        extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);
        flag = extra.get_flag();
        
    }
 
    
    
    // Show the camera view on the activity
    private void initCameraPreview() {
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        cameraPreview.init(camera);
    }
 
    public void onCaptureClick(View button){
        // Take a picture with a callback when the photo has been created
        // Here you can add callbacks if you want to give feedback when the picture is being taken
        camera.takePicture(null, null, this);
    }
 
    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d("Picture taken","asdasd");
        String path = savePictureToFileSystem(data);
        setResult(path);
        finish();
    }
 
    private static String savePictureToFileSystem(byte[] data) {
        File file = getOutputMediaFile(flag);
        saveToFile(data, file);
        return file.getAbsolutePath();
    }
 
    private void setResult(String path) {
        Intent intent = new Intent(this,DisplayProcessActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, path);
        intent.putExtra(EXTRA_IFIFITS, extra);
       // setResult(RESULT_OK, intent);
        releaseCamera();
        startActivity(intent);
    }
 
    // ALWAYS remember to release the camera when you are finished
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
 
    private void releaseCamera() {
        if(camera != null){
            camera.release();
            camera = null;
        }
    }
}
