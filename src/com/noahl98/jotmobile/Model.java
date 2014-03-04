package com.noahl98.jotmobile;

import java.util.ArrayList;

public class Model {

	public static ArrayList<Item> Items1;
	public static ArrayList<Item> Items2;

    public static Item doc1= new Item(6,"ic_current_doc.png", "", true, true);
    public static Item doc2= new Item(7,"ic_current_doc.png", "", false, false);
    public static Item doc3= new Item(8,"ic_current_doc.png", "", false, false);
    public static Item doc4= new Item(9,"ic_current_doc.png", "", false, false);

    public void initDocs(){

    }



	
	public static void loadModel1(){
		Items1= new ArrayList<Item>();
		
		Items1.add(new Item(1, "ic_save.png", "Save", true, true));
		Items1.add(new Item(2, "ic_open.png", "Open", true, true));
		Items1.add(new Item(3, "ic_6-social-share.png", "Export", true, true));
		Items1.add(new Item(4, "ic_clear.png", "Clear", true, true));
		Items1.add(new Item(5, "ic_new_doc.png", "New Document", true, true));
        Items1.add(doc1);
        Items1.add(doc2);
        Items1.add(doc3);
        Items1.add(doc4);


	}
	
	public static void loadModel2(){
		Items2 = new ArrayList<Item>();
		
		Items2.add(new Item(1, "ic_undo.png", "Undo", true, true));
		Items2.add(new Item(2, "ic_redo.png", "Redo", true, true));
		Items2.add(new Item(3, "ic_page_color.png", "Page Color", true, true));
		Items2.add(new Item(4, "ic_bullets.png", "Bullets", true, true));
		Items2.add(new Item(5, "ic_insert_image.png", "Insert Image", true, true));
		Items2.add(new Item(6, "ic_about.png", "About Jot", true, true));
		Items2.add(new Item(7, "ic_help.png", "Help", true, true));
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
