/*
 *	ViewInsertActivity.java
 *  Description: This activity displays the form to be filled up by the surveyor. This also displays the derived measurements and performs data uploads when network connections are present. 
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 04/10/14
 *  
 */

package com.cheesecake.ififitsisits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ViewInsertActivity extends Activity {

	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	public final static String EXTRA_IFIFITS_BITMAPS= "com.cheesecake.ififitsisits.IFIFITS_BITMAPS";
	
	private String selected = "M";
	private String selected2 = "NCR";
	private String selected3 = "Caloocan";
	private String cachePaths[];
	private EditText etArray[];
	private String otherFieldsArray[];
	private Spinner spinner3;
	private ArrayAdapter<CharSequence> adapter3;
	private IfIFitsExtra extra;
	String otherFields,otherFieldsSend;
	
	private static Bitmap bmp,bmp2,bmp3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_insert);

		Intent intent = getIntent();				//get data from previous activity
		
		extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);	
		cachePaths = extra.get_cachePaths();
			
		Log.d("CACHE PATHS",cachePaths[0]);
		String folder = Environment.getExternalStorageDirectory() + "/ififits/";
		
		bmp = BitmapFactory.decodeFile(folder+cachePaths[0]);					//display processed images 
		bmp2 = BitmapFactory.decodeFile(folder+cachePaths[1]);
		bmp3 = BitmapFactory.decodeFile(folder+cachePaths[2]);
		
		ImageView imageview = (ImageView) findViewById(R.id.imageview_side);
		ImageView imageview2 = (ImageView) findViewById(R.id.imageview_back);
		ImageView imageview3 = (ImageView) findViewById(R.id.imageview_front);
		
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		int screen_height = displaymetrics.heightPixels;
		int screen_width = displaymetrics.widthPixels;

		int image_width = (int)screen_width/3;
		int image_height = (int)screen_height/3;
		
		if(image_width >image_height){
			int temp = image_height;
			image_height = image_width;
			image_width = temp;
		}
		
		imageview.setImageBitmap(Bitmap.createScaledBitmap(bmp, image_width, image_height, false));
		imageview2.setImageBitmap(Bitmap.createScaledBitmap(bmp2, image_width, image_height, false));
		imageview3.setImageBitmap(Bitmap.createScaledBitmap(bmp3, image_width, image_height, false));
		
		imageview.setOnClickListener(new OnClickListener() {		//add image enlargement dialogs for every image
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(ViewInsertActivity.this);
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(bmp);
				settingsDialog.show();
				
			}
		});
		
		imageview2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(ViewInsertActivity.this);
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(bmp2);
				settingsDialog.show();
				
			}
		});
		
		imageview3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				Dialog settingsDialog = new Dialog(ViewInsertActivity.this);
				settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
				settingsDialog.setContentView(R.layout.image_dialog);
				ImageView i = (ImageView) settingsDialog.findViewById(R.id.image_view_dialog_01);
				i.setImageBitmap(bmp3);
				settingsDialog.show();
				
			}
		});
		
		
		TextView textview_sitHVal = (TextView) findViewById(R.id.textview_sitHVal);		//get TextViews
		TextView textview_sHVal = (TextView) findViewById(R.id.textview_sHVal);
		TextView textview_erHVal = (TextView) findViewById(R.id.textview_erHVal);
		TextView textview_tCVal = (TextView) findViewById(R.id.textview_tCVal);
		TextView textview_pHVal = (TextView) findViewById(R.id.textview_pHVal);
		TextView textview_kHVal = (TextView) findViewById(R.id.textview_kHVal);
		TextView textview_bpLVal = (TextView) findViewById(R.id.textview_bpLVal);
		TextView textview_hBVal = (TextView) findViewById(R.id.textview_hBVal);
		TextView textview_kkBVal = (TextView) findViewById(R.id.textview_kkBVal);
		
		DecimalFormat df = new DecimalFormat("#.0000"); 
		
		textview_sitHVal.setText(df.format(extra.get_measurements()[0])+" cm");		//set values of TextViews
		textview_pHVal.setText(df.format(extra.get_measurements()[1])+" cm");
		textview_tCVal.setText(df.format(extra.get_measurements()[2])+" cm");
		textview_bpLVal.setText(df.format(extra.get_measurements()[3])+" cm");
		textview_kHVal.setText(df.format(extra.get_measurements()[4])+" cm");
		textview_erHVal.setText(df.format(extra.get_measurements()[5])+" cm");
		textview_sHVal.setText(df.format(extra.get_measurements()[6])+" cm");
		textview_hBVal.setText(df.format(extra.get_measurements()[7])+" cm");
		textview_kkBVal.setText(df.format(extra.get_measurements()[8])+" cm");
		
		Spinner spinner = (Spinner) findViewById(R.id.sex_spinner);					//configure spinner for sex
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	        R.array.sex_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SelectedListener());
		
		Spinner spinner2 = (Spinner) findViewById(R.id.region_spinner);					//configure spinner for region
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.region_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new SelectedListener2());
		
		spinner3 = (Spinner) findViewById(R.id.cityprov_spinner);			//configure spinner for city/municipality, province
		adapter3 = ArrayAdapter.createFromResource(this,
		        R.array.ncr_array, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner3.setAdapter(adapter3);
		spinner3.setOnItemSelectedListener(new SelectedListener3());	
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewInsertActivity.this);
        otherFields = prefs.getString("otherFields", "");
        
        Log.d("otherfields laman",otherFields);
        
        if(otherFields.compareTo("OK")!=0){						//check if other fields exist
      
			LinearLayout ll = (LinearLayout) findViewById(R.id.otherInfoLayout);		
			otherFieldsArray = otherFields.split(",");
			etArray = new EditText[otherFieldsArray.length];			//if other fields exist, generate enough EditText fields
			
			for(int i=0; i<otherFieldsArray.length; i++){		
				etArray[i] = new EditText(this);
				etArray[i].setHint(otherFieldsArray[i]);
				ll.addView(etArray[i]);
			}
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_insert, menu);
		return true;
	}
	
	 public class SelectedListener implements OnItemSelectedListener {				//configure listener for sex spinner

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected = parent.getItemAtPosition(pos).toString();

		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	}
	
	 public class SelectedListener2 implements OnItemSelectedListener {		//configure listener region spinner

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected2 = parent.getItemAtPosition(pos).toString();
		        
		        if(selected2.equals("NCR")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.ncr_array, android.R.layout.simple_spinner_item);
		    		
		        }
		        
		        else if(selected2.equals("CAR")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.car_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region1")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioni_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region2")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region3")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioniii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region4a")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioniva_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region4b")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionivb_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region5")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionv_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region6")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionvi_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region7")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionvii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region8")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionviii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region9")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionix_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region10")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionx_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region11")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionxi_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region12")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionxii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region13")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionxiii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("ARMM")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.armm_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        	
		        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    		spinner3.setAdapter(adapter3);
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	 }
	 
	 public class SelectedListener3 implements OnItemSelectedListener {			//configure listener for city/municipality, province spinner

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected3 = parent.getItemAtPosition(pos).toString();
		        
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	} 
	 
	public void createRecord(View view){			//function for adding data to the records table
	    	
		 	boolean proceed = true;												//retrieve data from the EditText fields
		 	EditText edit_height = (EditText) findViewById(R.id.edit_height);
			EditText edit_weight = (EditText) findViewById(R.id.edit_weight);
			EditText edit_age = (EditText) findViewById(R.id.edit_age);
			double height =0.0, weight =0.0;
			int age = 0;
			
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewInsertActivity.this);
			
			try {																//perform form validation
				height = Double.parseDouble(edit_height.getText().toString());
			} catch (NumberFormatException e) {
				proceed = false;
			}
			
			try{
				weight = Double.parseDouble(edit_weight.getText().toString());
			} catch (NumberFormatException e) {
				proceed = false;
			}
			try{
				age = Integer.parseInt(edit_age.getText().toString());;
			} catch (NumberFormatException e) {
				proceed = false;
			}
			
			if(otherFields.compareTo("OK")!=0){
				for(int i=0; i<etArray.length; i++){
					if(etArray[i].getText().toString().compareTo("")==0){
						proceed = false;
						break;
					}
				}
			}
			
			if(proceed){				//if all fields are filled up, prepare data for insertion
			
			UUID side_id = UUID.randomUUID();			//generate filenames for the captured images
			UUID front_id = UUID.randomUUID();
			UUID back_id = UUID.randomUUID();
			
			File folder = new File(Environment.getExternalStorageDirectory() + "/ififits");
			
			if (!folder.exists()) 
				folder.mkdir();
			
			String filename = side_id+".jpg";
			File file = new File(folder, filename);
			try{
				FileOutputStream fOut = new FileOutputStream(file);			//write the image files on sd card
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			    fOut.flush();
			    fOut.close();
			}
			catch(IOException e){};
			
			String filename2 = back_id+".jpg";
			File file2 = new File(folder, filename2);
			try{
				FileOutputStream fOut = new FileOutputStream(file2);
				bmp2.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			    fOut.flush();
			    fOut.close();
			}
			catch(IOException e){};
			
			String filename3 = front_id+".jpg";
			File file3 = new File(folder, filename3);
			try{
				FileOutputStream fOut = new FileOutputStream(file3);
				bmp3.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			    fOut.flush();
			    fOut.close();
			}
			catch(IOException e){};
			
			DatabaseHelper db = new DatabaseHelper(this);			
			
			Log.d("Side: ",filename);
			Log.d("Front: ",filename2);
			Log.d("Back: ",filename3);
			
			otherFieldsSend = "";
			if(otherFields.compareTo("OK")!=0){						//retrieve other field information
				for(int i=0; i<otherFieldsArray.length; i++){
					otherFieldsSend += otherFieldsArray[i]+":";
					otherFieldsSend += etArray[i].getText().toString();
					if(i!=otherFieldsArray.length-1)
						otherFieldsSend +=",";	
					
				}
			}
			
			String projectId = prefs.getString("projectId", "default");
			
			db.addRecord(new Record(height,weight,age,selected,				//perform database insertion
					selected2,selected3,
					extra.get_measurements()[0],		
					extra.get_measurements()[6],
					extra.get_measurements()[5],
					extra.get_measurements()[2],
					extra.get_measurements()[1],
					extra.get_measurements()[4],
					extra.get_measurements()[3],
					extra.get_measurements()[7],
					extra.get_measurements()[8],
					filename,
					filename3,
					filename2,
					projectId,
					otherFieldsSend));
			
			
			
			if(!mayInternets()){				//if no network connection is available
				db.enqueueUpload();				//enqueue the upload
				Toast.makeText(this, "Data Enqueued for Uploading.", Toast.LENGTH_SHORT).show();
				Log.d("Other Categories",otherFieldsSend);
				
			}
			else{
				
		        String url = prefs.getString("url", "http://192.168.1.100");
		        String authkey = prefs.getString("authkey", "gagraduateako");
			
				HttpPostHelper helper = new HttpPostHelper(url+"/android_add_survey.php"); 		//prepare for making the request
				
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				
				pairs.add(new BasicNameValuePair("authkey", authkey));
				pairs.add(new BasicNameValuePair("project_id", projectId));
				pairs.add(new BasicNameValuePair("survey_info_id", projectId+"-"+db.getLastId()));
				pairs.add(new BasicNameValuePair("gender", selected.substring(0,1)));
				pairs.add(new BasicNameValuePair("age", age+""));
				pairs.add(new BasicNameValuePair("height", height+""));
				pairs.add(new BasicNameValuePair("weight", weight+""));
				pairs.add(new BasicNameValuePair("region", selected2));	
				pairs.add(new BasicNameValuePair("cityprov", selected3));	
				pairs.add(new BasicNameValuePair("body_measurement", "sitH:"+extra.get_measurements()[0]+","+
																	 "pH:"+extra.get_measurements()[1]+","+
																	 "tC:"+extra.get_measurements()[2]+","+
																	 "bpL:"+extra.get_measurements()[3]+","+
																	 "kH:"+extra.get_measurements()[4]+","+
																	 "erH:"+extra.get_measurements()[5]+","+
																	 "sH:"+extra.get_measurements()[6]+","+
																	 "hB:"+extra.get_measurements()[7]+","+
																	 "kkB:"+extra.get_measurements()[8]));	
				
				if(otherFields.compareTo("OK")!=0){
					pairs.add(new BasicNameValuePair("other_category", otherFieldsSend));
					Log.d("Other Categories",otherFieldsSend);
				}
				
				if(helper.post(pairs))				//make the request, prompt user regarding the result
					Toast.makeText(this, "Data Sent to Server.", Toast.LENGTH_SHORT).show();
				else{
					db.enqueueUpload();
					Toast.makeText(this, "Cannot Connect to Server. Data Enqueued for Uploading.", Toast.LENGTH_LONG).show();
					}
				}
			db.close();
			
            Intent intent = new Intent(this, MainActivity.class);			//fire intent to main activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
            startActivity(intent);
            finish();
			}
			else{
				Toast.makeText(this, "Please fill up all input fields.", Toast.LENGTH_SHORT).show();
			}
	    }
	
	public void cancel(View view){				//function that brings the researcher back to the first capture
		
		 Intent intent = new Intent(this, CameraActivity.class);
		 
		 IfIFitsExtra extra = new IfIFitsExtra();
     	 extra.set_flag(0);
     	
     	
     	 intent.putExtra(EXTRA_IFIFITS, extra);
     	 startActivity(intent);
        
         finish();
		
	}
	
	public boolean mayInternets() {			//function for checking the network connection
	    ConnectivityManager connectivityManager 
	          = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	 }
	

}
