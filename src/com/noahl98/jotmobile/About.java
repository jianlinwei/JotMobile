package com.noahl98.jotmobile;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

/**
 * Created by noahl98 on 3/24/14.
 */
public class About extends Fragment {

    private WebView page;
    public boolean isShowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.about_page, container, false);

        page = (WebView)view.findViewById(R.id.about_web_page);

        page.loadUrl("http://htjot.weebly.com/about-jot-for-android.html");

        isShowing=true;

        return view;
    }

    public void onBackPressed(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction= fragmentManager.beginTransaction();

        isShowing=false;
        fragmentTransaction.remove(this).commit();
    }
}
