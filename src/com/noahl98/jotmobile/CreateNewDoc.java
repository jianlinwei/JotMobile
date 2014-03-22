package com.noahl98.jotmobile;

import android.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.layout_create_new, container, false);

        docTitle = (EditText)view.findViewById(R.id.newDocTitle);

        return view;
    }

    public static String getDocTitle(){
        return docTitle.getText().toString();
    }
}
