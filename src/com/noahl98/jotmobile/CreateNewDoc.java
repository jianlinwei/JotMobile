package com.noahl98.jotmobile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by noahl98 on 3/20/14.
 */
public class CreateNewDoc extends Fragment{

    private static EditText docTitle;
    public boolean isShowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_create_new, container, false);

        docTitle = (EditText)view.findViewById(R.id.newDocTitle);

        isShowing=true;

        return view;
    }

    public static String getDocTitle(){
        return docTitle.getText().toString();
    }

    public void onBackPressed(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();

        fragmentTransaction.remove(this).commit();
        isShowing=false;

    }
}
