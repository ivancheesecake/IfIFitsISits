/*
 *	AboutFragment.java
 *  Description: This fragment serves as the About screen of the application. 
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
 
public class AboutFragment extends Fragment {		//Start of class AboutFragment
 
    public static Fragment newInstance(Context context) {
       AboutFragment f = new AboutFragment();
       MainActivity.actionbar.setSubtitle("About");
        return f;
    }
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.about_fragment, null);
        return root;
    }
 
}
