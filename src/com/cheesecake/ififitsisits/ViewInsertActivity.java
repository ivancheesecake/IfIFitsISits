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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ViewInsertActivity extends Activity {

	public final static String EXTRA_IFIFITS= "com.cheesecake.ififitsisits.IFIFITS";
	public final static String EXTRA_IFIFITS_BITMAPS= "com.cheesecake.ififitsisits.IFIFITS_BITMAPS";
	
	private static Record r;
	private String selected = "M";
	private String selected2 = "NCR";
	private String selected3 = "Caloocan";
	private String cachePaths[];
	private Spinner spinner3;
	private ArrayAdapter<CharSequence> adapter3;
	private IfIFitsExtra extra;
	//private Bitmap[] extraBitmaps;
	
	private static Bitmap bmp,bmp2,bmp3;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_insert);
		
		System.loadLibrary("ififits_native");
		
		//Log.d("LOG","Nasa View Insert na.");
		Intent intent = getIntent();
		
		extra = (IfIFitsExtra) intent.getSerializableExtra(EXTRA_IFIFITS);	
		cachePaths = extra.get_cachePaths();
		
		//Parcelable[] ps = intent.getParcelableArrayExtra(EXTRA_IFIFITS_BITMAPS);	
		//extraBitmaps = new Bitmap[ps.length];
		//System.arraycopy(ps, 0, extraBitmaps, 0, ps.length);
		
		//r = (Record) intent.getSerializableExtra(DisplayActivity.EXTRA_RECORD);
		
		//bmp = extraBitmaps[0];
		//bmp2 = extraBitmaps[1];
		//bmp3 = extraBitmaps[2];
		
		Log.d("CACHE PATHS",cachePaths[0]);
		String folder = Environment.getExternalStorageDirectory() + "/ififits/";
		
	
		
		bmp = BitmapFactory.decodeFile(folder+cachePaths[0]);
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
		
		imageview.setOnClickListener(new OnClickListener() {
			
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
		//TextView textview_subjectId = (TextView) findViewById(R.id.textview_subjectId);
		TextView textview_sitHVal = (TextView) findViewById(R.id.textview_sitHVal);
		TextView textview_sHVal = (TextView) findViewById(R.id.textview_sHVal);
		TextView textview_erHVal = (TextView) findViewById(R.id.textview_erHVal);
		TextView textview_tCVal = (TextView) findViewById(R.id.textview_tCVal);
		TextView textview_pHVal = (TextView) findViewById(R.id.textview_pHVal);
		TextView textview_kHVal = (TextView) findViewById(R.id.textview_kHVal);
		TextView textview_bpLVal = (TextView) findViewById(R.id.textview_bpLVal);
		TextView textview_hBVal = (TextView) findViewById(R.id.textview_hBVal);
		TextView textview_kkBVal = (TextView) findViewById(R.id.textview_kkBVal);
		//TextView textview_locationVal = (TextView) findViewById(R.id.textview_locationVal);

		DecimalFormat df = new DecimalFormat("#.00"); 
		
		//textview_subjectId.setText("Subject #"+r.get_id());
		textview_sitHVal.setText(df.format(extra.get_measurements()[0])+" cm");		//double check mo yung indices
		textview_pHVal.setText(df.format(extra.get_measurements()[1])+" cm");
		textview_tCVal.setText(df.format(extra.get_measurements()[2])+" cm");
		textview_bpLVal.setText(df.format(extra.get_measurements()[3])+" cm");
		textview_kHVal.setText(df.format(extra.get_measurements()[4])+" cm");
		textview_erHVal.setText(df.format(extra.get_measurements()[5])+" cm");
		textview_sHVal.setText(df.format(extra.get_measurements()[6])+" cm");
		textview_hBVal.setText(df.format(extra.get_measurements()[7])+" cm");
		textview_kkBVal.setText(df.format(extra.get_measurements()[8])+" cm");
		//textview_locationVal.setText(r.get_region());
		
		Spinner spinner = (Spinner) findViewById(R.id.sex_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
	        R.array.sex_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SelectedListener());
		
		Spinner spinner2 = (Spinner) findViewById(R.id.region_spinner);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
		        R.array.region_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new SelectedListener2());
		
		spinner3 = (Spinner) findViewById(R.id.cityprov_spinner);
		adapter3 = ArrayAdapter.createFromResource(this,
		        R.array.ncr_array, android.R.layout.simple_spinner_item);
		adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		spinner3.setAdapter(adapter3);
		spinner3.setOnItemSelectedListener(new SelectedListener3());	
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_insert, menu);
		return true;
	}
	
	 public class SelectedListener implements OnItemSelectedListener {

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected = parent.getItemAtPosition(pos).toString();
		        //selected = selected.substring(0,1);
		        //Log.d("Selected",selected);
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	}
	
	 public class SelectedListener2 implements OnItemSelectedListener {

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
		        else if(selected2.equals("Region I")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioni_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region II")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region III")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioniii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region IV-A")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regioniva_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region IV-B")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionivb_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region V")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionv_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region VI")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionvi_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region VII")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionvii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region VIII")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionviii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region IX")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionix_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region X")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionx_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region XI")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionxi_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region XII")){
		        	adapter3 = ArrayAdapter.createFromResource(getApplicationContext(),
		    		        R.array.regionxii_array, android.R.layout.simple_spinner_item);
		        	
		        }
		        else if(selected2.equals("Region XIII")){
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
	 
	 public class SelectedListener3 implements OnItemSelectedListener {

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected3 = parent.getItemAtPosition(pos).toString();
		        
		        
		        //Log.d("Selected",selected);
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	} 
	 
	public void createRecord(View view){
	    	
		 	boolean proceed = true;
		 	EditText edit_height = (EditText) findViewById(R.id.edit_height);
			EditText edit_weight = (EditText) findViewById(R.id.edit_weight);
			EditText edit_age = (EditText) findViewById(R.id.edit_age);
			double height =0.0, weight =0.0;
			int age = 0;
			try {
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
			
			if(proceed){
			
			UUID side_id = UUID.randomUUID();
			UUID front_id = UUID.randomUUID();
			UUID back_id = UUID.randomUUID();
			
			File folder = new File(Environment.getExternalStorageDirectory() + "/ififits");
			
			if (!folder.exists()) 
				folder.mkdir();
			
			String filename = side_id+".jpg";
			File file = new File(folder, filename);
			try{
				FileOutputStream fOut = new FileOutputStream(file);
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
			
			db.addRecord(new Record(height,weight,age,selected,
					selected2,selected3,
					extra.get_measurements()[0],		//double check indices
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
					filename2));
			
			if(!MainActivity.mayInternetsBa()){
				db.enqueueUpload();
				Toast.makeText(this, "Data Enqueued for Uploading.", Toast.LENGTH_SHORT).show();
				
			}
			else{
				
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ViewInsertActivity.this);
		        String url = prefs.getString("url", "http://192.168.1.100");
		        String authkey = prefs.getString("authkey", "gagraduateako");
				String projectId = prefs.getString("projectId", "default");
		        
				HttpPostHelper helper = new HttpPostHelper(url+"/android_add_survey.php"); 	//get url from sharedpreferences
				
				ArrayList<NameValuePair> pairs = new ArrayList<NameValuePair>();
				
				pairs.add(new BasicNameValuePair("authkey", authkey));
				pairs.add(new BasicNameValuePair("project_id", projectId));
				//pairs.add(new BasicNameValuePair("project_name", "Armchair Anthropometry"));
				//pairs.add(new BasicNameValuePair("description", "Anthropometry for Armchairs. Duh."));
				pairs.add(new BasicNameValuePair("survey_info_id", projectId+"-"+db.getLastId()));
				pairs.add(new BasicNameValuePair("gender", selected.substring(0,1)));
				pairs.add(new BasicNameValuePair("age", age+""));
				pairs.add(new BasicNameValuePair("height", height+""));
				pairs.add(new BasicNameValuePair("weight", weight+""));
				pairs.add(new BasicNameValuePair("region", selected2));	//add for cityprov
				pairs.add(new BasicNameValuePair("cityprov", selected3));	//add for cityprov
				pairs.add(new BasicNameValuePair("body_measurement", "sitH:"+extra.get_measurements()[0]+","+
																	 "pH:"+extra.get_measurements()[1]+","+
																	 "tC:"+extra.get_measurements()[2]+","+
																	 "bpL:"+extra.get_measurements()[3]+","+
																	 "kH:"+extra.get_measurements()[4]+","+
																	 "erH:"+extra.get_measurements()[5]+","+
																	 "sH:"+extra.get_measurements()[6]+","+
																	 "hB:"+extra.get_measurements()[7]+","+
																	 "kkB:"+extra.get_measurements()[8]));	
				
				
				if(helper.post(pairs))
					Toast.makeText(this, "Data Sent to Server.", Toast.LENGTH_SHORT).show();
				else{
					db.enqueueUpload();
					Toast.makeText(this, "Cannot Connect to Server. Data Enqueued for Uploading.", Toast.LENGTH_LONG).show();
					}
				}
			db.close();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP); 
            startActivity(intent);
            finish();
			}
			else{
				Toast.makeText(this, "Please fill up all input fields.", Toast.LENGTH_SHORT).show();
			}
	    }

}
