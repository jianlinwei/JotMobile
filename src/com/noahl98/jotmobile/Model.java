package com.noahl98.jotmobile;

import java.util.ArrayList;

public class Model {

	public static ArrayList<Item> Items1;
	public static ArrayList<Item> Items2;
	
	public static void loadModel1(){
		Items1= new ArrayList<Item>();
		
		Items1.add(new Item(1, "ic_save.png", "Save"));
		Items1.add(new Item(2, "ic_open.png", "Open"));
		Items1.add(new Item(3, "ic_6-social-share.png", "Export"));
		Items1.add(new Item(4, "ic_clear.png", "Clear"));
		Items1.add(new Item(5, "ic_new_doc.png", "New Document"));
	}
	
	public static void loadModel2(){
		Items2 = new ArrayList<Item>();
		
		Items2.add(new Item(1, "ic_undo.png", "Undo"));
		Items2.add(new Item(2, "ic_redo.png", "Redo"));
		Items2.add(new Item(3, "ic_page_color.png", "Page Color"));
		Items2.add(new Item(4, "ic_bullets.png", "Bullets"));
		Items2.add(new Item(5, "ic_insert_image.png", "Insert Image"));
		Items2.add(new Item(6, "ic_about.png", "About Jot"));
		Items2.add(new Item(7, "ic_help.png", "Help"));
	}
	
	public static Item getById(int id, int listNumber){
		if(listNumber == 1){
			for(Item item:Items1){
				if(item.Id==id){
					return item;
				}
			}
		}else{
			for(Item item:Items2){
				if(item.Id==id){
					return item;
				}
			}
		}
		return null;
	}
}
