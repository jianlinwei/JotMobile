package com.noahl98.jotmobile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class SaveFragment extends Fragment{

    private static EditText docTitle;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.layout_save, container, false);

        docTitle=(EditText)view.findViewById(R.id.docTitle);

        return view;
	}

    public void onBackPressed(SaveFragment saveFragment){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();

        fragmentTransaction.remove(saveFragment).commit();
    }

    public static String getDocTitle(){
        return docTitle.getText().toString();
    }
}


