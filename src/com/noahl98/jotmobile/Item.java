package com.noahl98.jotmobile;

public class Item {

	public int Id;
	public String IconFile;
	public String Name;
	
	public Item (int id, String iconFile, String name, boolean showIcon, boolean showLabel){



		this.Id = id;
		this.IconFile= iconFile;
		this.Name= name;

        init(showIcon, showLabel);
	}

    private void init(boolean showIcon, boolean showLabel){
        if(!showIcon){
            IconFile="";
        }
        if(!showLabel){
            Name="";
            IconFile="";
        }
    }

    public void setName (String name){
        this.Name = name;
    }

    public void setAsCurrentDoc(){
        this.IconFile="ic_current_doc.png";
    }

    public String getName(){
        return this.Name;
    }
}
