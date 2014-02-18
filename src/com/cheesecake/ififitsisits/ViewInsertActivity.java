package com.cheesecake.ififitsisits;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


public class ViewInsertActivity extends Activity {

	public static Record r;
	private String selected = "Male";
	private String selected2 = "NCR";
	static Bitmap bmp,bmp2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_insert);
		
		System.loadLibrary("ififits_native");
		
		//Log.d("LOG","Nasa View Insert na.");
		Intent intent = getIntent();
		r = (Record) intent.getSerializableExtra(DisplayActivity.EXTRA_RECORD);
		
		bmp = (Bitmap) intent.getParcelableExtra(DisplayActivity.EXTRA_SIDE_BMP_2);
		bmp2 = (Bitmap) intent.getParcelableExtra(DisplayActivity.EXTRA_FRONT_BMP_2);
		ImageView imageview = (ImageView) findViewById(R.id.imageview_side);
		ImageView imageview2 = (ImageView) findViewById(R.id.imageview_back);
		
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		
		int screen_height = displaymetrics.heightPixels;
		int screen_width = displaymetrics.widthPixels;

		int image_width = (int)screen_width/2;
		int image_height = (int)screen_height/2;
		
		if(image_width >image_height){
			int temp = image_height;
			image_height = image_width;
			image_width = temp;
		}
		
		imageview.setImageBitmap(Bitmap.createScaledBitmap(bmp, image_width, image_height, false));
		imageview2.setImageBitmap(Bitmap.createScaledBitmap(bmp2, image_width, image_height, false));
		
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
		textview_sitHVal.setText(df.format(r.get_sitH())+" cm");
		textview_sHVal.setText(df.format(r.get_sH())+" cm");
		textview_erHVal.setText(df.format(r.get_erH())+" cm");
		textview_tCVal.setText(df.format(r.get_tC())+" cm");
		textview_pHVal.setText(df.format(r.get_pH())+" cm");
		textview_kHVal.setText(df.format(r.get_kH())+" cm");
		textview_bpLVal.setText(df.format(r.get_bpL())+" cm");
		textview_hBVal.setText(df.format(r.get_hB())+" cm");
		textview_kkBVal.setText(df.format(r.get_kkB())+" cm");
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
		        //Log.d("Selected",selected);
		    }

		    public void onNothingSelected(AdapterView<?> parent) {
		       
		    }
	}
	
	 public class SelectedListener2 implements OnItemSelectedListener {

		    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		        selected2 = parent.getItemAtPosition(pos).toString();
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
			
			String filename2 = front_id+".jpg";
			File file2 = new File(folder, filename2);
			try{
				FileOutputStream fOut = new FileOutputStream(file2);
				bmp2.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			    fOut.flush();
			    fOut.close();
			}
			catch(IOException e){};
			
			DatabaseHelper db = new DatabaseHelper(this);
			
			db.addRecord(new Record(height,weight,age,selected,
					selected2,
					r.get_sitH(),
					r.get_sH(),
					r.get_erH(),
					r.get_tC(),
					r.get_pH(),
					r.get_kH(),
					r.get_bpL(),
					r.get_hB(),
					r.get_kkB(),
					filename,
					filename2));
			
			if(!MainActivity.mayInternetsBa()){
				db.enqueueUpload();
				Toast.makeText(this, "Data Enqueued for Uploading.", Toast.LENGTH_SHORT).show();
				
			}
			else{
					
				//do uploading here
				Toast.makeText(this, "Data Sent to Server.", Toast.LENGTH_SHORT).show();
			
			}
			
			//ViewRecordsFragment.records = db.getAllRecords();
			//.makeText(MainActivity.this, "Record Added.", Toast.LENGTH_LONG).show();
			
			
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
