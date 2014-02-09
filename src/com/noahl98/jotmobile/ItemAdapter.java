package com.noahl98.jotmobile;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter<String>{

	private final Context context;
	private final int rowResourceId;
	private final String[] Ids;
	private final int listNumber;
	
	public ItemAdapter(Context context, int textViewResourceId, String[] objects, int listNumber){
		super(context, textViewResourceId, objects);
		
		this.context= context;
		this.Ids = objects;
		this.rowResourceId = textViewResourceId;
		this.listNumber = listNumber;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View rowView = inflater.inflate(rowResourceId, parent, false);
		ImageView imageView = (ImageView)rowView.findViewById(R.id.icon);
		TextView textView = (TextView)rowView.findViewById(R.id.text1);
		
		int id = Integer.parseInt(Ids[position]);
		String imageFile = Model.getById(id, listNumber).IconFile;
		
		textView.setText(Model.getById(id, listNumber).Name);
		
		//get InputStream
		InputStream ims = null;
		try {
			ims= context.getAssets().open(imageFile);
		}catch(IOException e){
			e.printStackTrace();
		}
		
		//load drawable
		Drawable d =Drawable.createFromStream(ims, null);
		//set image to imageView
		imageView.setImageDrawable(d);
		return rowView;
	}
}
