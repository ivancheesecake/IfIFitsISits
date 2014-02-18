package com.cheesecake.ififitsisits;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
 
public class InputFragment extends Fragment {

	
    public static Fragment newInstance(Context context) {
        InputFragment f = new InputFragment();
 
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View v = (View) inflater.inflate(R.layout.input_fragment, container,false);
        
        Spinner spinner = (Spinner) v.findViewById(R.id.sex_spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
		        R.array.sex_array, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new SelectedListener());
		
		Spinner spinner2 = (Spinner) v.findViewById(R.id.region_spinner);
		ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(),
		        R.array.region_array, android.R.layout.simple_spinner_item);
		adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner2.setAdapter(adapter2);
		spinner2.setOnItemSelectedListener(new SelectedListener2());
		
		InputMethodManager imgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

		EditText myET=(EditText) v.findViewById(R.id.edit_height);
		myET.requestFocus();
        
		imgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        return v;
    }
    
    public class SelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        MainActivity.selected = parent.getItemAtPosition(pos).toString();
	        //Log.d("Selected",selected);
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	       
	    }
	}
 
    public class SelectedListener2 implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        MainActivity.selected2 = parent.getItemAtPosition(pos).toString();
	        //Log.d("Selected",selected);
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	       
	    }
	}
}