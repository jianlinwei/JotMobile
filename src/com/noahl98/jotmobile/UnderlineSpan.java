package com.noahl98.jotmobile;

import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.text.style.UpdateAppearance;

public class UnderlineSpan extends CharacterStyle implements UpdateAppearance, ParcelableSpan{

	public UnderlineSpan(){
	}
	
	public UnderlineSpan(Parcel src){
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getSpanTypeId() {
		// TODO Auto-generated method stub
		return 6;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
		// TODO Auto-generated method stub
		ds.setUnderlineText(true);
	}

}
