/*
 *	HelpFragment.java
 *  Description: This fragment serves as the "help screen" of the application.  
 *  Author: Escamos, Ivan Marc H. 
 *  Date last modified: 04/10/14
 *  
 */


package com.cheesecake.ififitsisits;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
 
public class HelpFragment extends Fragment {
 
    public static Fragment newInstance(Context context) {
        HelpFragment f = new HelpFragment();
        
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.help_fragment, null);
        MainActivity.actionbar.setSubtitle("Help");
        
        return root;
    }
 
}
