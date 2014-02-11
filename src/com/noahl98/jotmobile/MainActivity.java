package com.noahl98.jotmobile;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements RichText.EditTextImeBackListener, OnTouchListener, OnClickListener, TextWatcher{
	private String[] drawerListViewItems1;
	private String[] drawerListViewItems2;
    private ArrayList<Editable> undoStrings;

    private int undoIndex;
	
	private EditText title;
	
	private int styleStart;
	
	private ListView drawerListView1;
	private ListView drawerListView2;
	
	private TextView text1;
	
	private RelativeLayout formatBar;
	
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private RichText richText;
	private SaveFragment saveFragment;
	
	private ToggleButton boldButton;
	private ToggleButton emButton;
	private ToggleButton uButton;
	private ToggleButton strikeButton;
	
	private boolean keyboardShown;
	private boolean alreadyShown;
	private boolean isMainContent;
	
	
    //private static enum LocationStatus {NONE, FOUND, NOT_FOUND, SEARCHING}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //sets up the first drawer
        Model.loadModel1();
        
        //finds the left drawer
        drawerListView1 = (ListView) findViewById(R.id.left_drawer);
        //sets the correct items(defined in Model.java)
        drawerListViewItems1 = new String[Model.Items1.size()];
        
        //sets Id's to strings
        for(int i= 0; i<drawerListViewItems1.length;i++){
        	drawerListViewItems1[i]= Integer.toString(i+1);
        }
        
        //applies the custom array adapter
        ItemAdapter adapter1 = new ItemAdapter(this, R.layout.drawer_listview_item, drawerListViewItems1, 1);
        drawerListView1.setAdapter(adapter1);
        
        //sets up the second drawer
        Model.loadModel2();
        
        //finds right drawer
        drawerListView2 = (ListView)findViewById(R.id.right_drawer);
        //sets the correct items(defined in Model.java)
        drawerListViewItems2 = new String[Model.Items2.size()];
        
        //changes ids to strings
        for(int i =0; i<drawerListViewItems2.length;i++){
        	drawerListViewItems2[i] = Integer.toString(i+1);
        }
        
        //sets custom adapter
        ItemAdapter adapter2 = new ItemAdapter(this, R.layout.drawer_listview_item, drawerListViewItems2, 2);
        drawerListView2.setAdapter(adapter2);
 
        // 2. App Icon 
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
 
        // 2.1 create ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close);
        
        //sets the drawer listener
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
        	@Override
        	public void onDrawerOpened(View drawerLayout){
        		if(keyboardShown){
        			//hides format bar when a drawer is opened
        			hideFormatBarAnim();
        		}
        	}
        	
        	@Override
        	public void onDrawerClosed(View drawerLayout){
        		//shows format bar when closed
        		showFormatBar();
        	}
		});
 
        // 2.2 Set actionBarDrawerToggle as the DrawerListener
        //drawerLayout.setDrawerListener(actionBarDrawerToggle);
 
        // 2.3 enable and show "up" arrow
        getActionBar().setDisplayHomeAsUpEnabled(true); 
 
        // just styling option
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        //item click listener for drawer items
        drawerListView1.setOnItemClickListener(new DrawerItemClickListener());
        drawerListView2.setOnItemClickListener(new DrawerItemClickListener());
        
        alreadyShown=false;
        isMainContent= true;
        keyboardShown=false;

        undoIndex=0;

        undoStrings = new ArrayList<Editable>();
        
        //assigns formatBar to its XML layout 
        formatBar = (RelativeLayout)findViewById(R.id.formatBar);
        
        //assigns title to its XML layout
        title = (EditText)findViewById(R.id.title);
        
        //assigns text1 to its XML layout
        text1= (TextView) findViewById(R.id.text1);
        
        //defines save fragment
        saveFragment = new SaveFragment();
        
        //assigns richText to its XML layout and adds a TextChangeListener
        richText= (RichText)findViewById(R.id.edit_text);
        richText.addTextChangedListener(this);
        
        //assigns boldButton to its XML layout and adds an OnClickListener
        boldButton=(ToggleButton)findViewById(R.id.bold);
        boldButton.setOnClickListener(this);
        
        //assigns emButton to its XML layout and adds an OnClickListener
        emButton=(ToggleButton)findViewById(R.id.ital);
        emButton.setOnClickListener(this);
        
        //assigns uButton to its XML layout and adds an OnClickListener
        uButton=(ToggleButton)findViewById(R.id.underline);
        uButton.setOnClickListener(this);
        
        //assigns strikeButton to its XML layout and adds an onClickListener
        strikeButton=(ToggleButton)findViewById(R.id.strike);
        strikeButton.setOnClickListener(this);
        
        //initiates sofKeyboardHook
        softKeyboardHook();
    }
    
	
    @Override
    protected void onPostCreate(Bundle savedInstanceState){
    	super.onPostCreate(savedInstanceState);
    	actionBarDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    	actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    //Functions for DrawerLayout
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
    	if(actionBarDrawerToggle.onOptionsItemSelected(item)){
    		
    		//closes other drawer so they cannot be opened at the same time
        	drawerLayout.closeDrawer(drawerListView2);
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
	public void onImeBack(RichText ctrl, String text) {
		// TODO Auto-generated method stub
		if(formatBar.getVisibility()==View.VISIBLE){
			hideFormatBar();
		}
	}
    
    //logic for when to show the formatBar
    private void softKeyboardHook(){
    	final View scrollView= findViewById(R.id.scrollView);
    	scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			
			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				Rect r = new Rect();
				scrollView.getWindowVisibleDisplayFrame(r);
                //gets the height difference
				int heightDiff= scrollView.getRootView().getHeight()-(r.bottom - r.top);

                //if the height difference is larger than 100px then it is most likely the keyboard
				if (heightDiff>100&& isMainContent){
					if(alreadyShown){
						keyboardShown=true;
					}else{
						alreadyShown=true;
						keyboardShown=true;
						showFormatBar();
					}
					
				}else{
					keyboardShown=false;
					alreadyShown=false;
					hideFormatBar();
				}
			}
		});
    }
    //sets animation for showing the formatBar
    private void showFormatBar(){
    	if(keyboardShown){
    		formatBar.setVisibility(View.VISIBLE);
    		AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
    		fadeInAnimation.setDuration(500);
    		formatBar.startAnimation(fadeInAnimation);
    	}
    }
    //hides the formatBar
    private void hideFormatBar(){
    	formatBar.setVisibility(View.GONE);
    }
    //separate hidFormatBar function w/ animation
    private void hideFormatBarAnim(){
    	AlphaAnimation fadeOutAnimation= new AlphaAnimation(1.0f, 0.0f);
    	fadeOutAnimation.setDuration(200);
    	formatBar.startAnimation(fadeOutAnimation);
    	formatBar.setVisibility(View.GONE);
    }
    
    //more logic for when to show the formatBar
    @Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
    	if(formatBar.getVisibility() != View.VISIBLE){
    		showFormatBar();
    	}
		return false;
	}
    
    //applies spans to body
    public void formatBtnClicked(ToggleButton toggleButton, String tag){

        //tries to get the selection
    	try{
	    	Spannable s = richText.getText();
	    	int selectionStart= richText.getSelectionStart();
	    	styleStart=selectionStart;
	    	int selectionEnd= richText.getSelectionEnd();
	    	
	    	if(selectionStart>selectionEnd){
	    		int temp= selectionEnd;
	    		selectionEnd=selectionStart;
	    		selectionStart=temp;
	    	}
	    	//if there is a selection apply the specified span
	    	if(selectionEnd>selectionStart){
	    		Spannable str = richText.getText();
	    		if(tag.equals("strong")){
	    			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);
	    			
	    			boolean exists= false;
	    			for(int i=0; i<ss.length;i++){
	    				int styleSpan = ((StyleSpan) ss[i]).getStyle();
	    				if(styleSpan== android.graphics.Typeface.BOLD){
	    					str.removeSpan(ss[i]);
		    				exists=true;
	    				}
	    			}
	    			if(!exists){
	    				str.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), selectionStart, selectionEnd, 
	    						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			toggleButton.setChecked(false);
	    		}else if(tag.equals("em")){
	    			StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);
	    			
	    			boolean exists = false;
	    			for(int i=0; i<ss.length;i++){
	    				int style = ss[i].getStyle();
	    				if(style== android.graphics.Typeface.ITALIC){
	    					str.removeSpan(ss[i]);
	    					exists= true;
	    				}
	    			}
	    			if(!exists){
	    				str.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), selectionStart, selectionEnd, 
	    						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			toggleButton.setChecked(false);
	    		}else if(tag.equals("u")){
	    			UnderlineSpan[]ss =str.getSpans(selectionStart, selectionEnd, UnderlineSpan.class);
	    			
	    			boolean exists = false;
	    			for(int i=0; i<ss.length;i++){
	    				str.removeSpan(ss[i]);
	    				exists= true;
	    			}
	    			
	    			if(!exists){
	    				str.setSpan(new UnderlineSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			toggleButton.setChecked(false);
	    			toggleButton.setChecked(false);
	    		}else if(tag.equals("strike")){
	    			StrikethroughSpan[]ss= str.getSpans(selectionStart, selectionEnd, StrikethroughSpan.class);
	    			
	    			boolean exists = false;
	    			for(int i=0; i<ss.length;i++){
	    				str.removeSpan(ss[i]);
	    				exists= true;
	    			}
	    			
	    			if(!exists){
	    				str.setSpan(new StrikethroughSpan(), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    			toggleButton.setChecked(false);
	    		}
                //if there is no selection, adds to the span
	    	}else if(!toggleButton.isChecked()){
	    		if(tag.equals("strong")||tag.equals("em")){
	    			StyleSpan[] ss = s.getSpans(styleStart-1, styleStart, StyleSpan.class);
	    			
	    			for(int i=0; i<ss.length;i++){
	    				int tagStart= s.getSpanStart(ss[i]);
	    				int tagEnd = s.getSpanEnd(ss[i]);
	    				
	    				if(ss[i].getStyle()== android.graphics.Typeface.BOLD && tag.equals("strong")){
	    					tagStart= s.getSpanStart(ss[i]);
	    					tagEnd= s.getSpanEnd(ss[i]);
	    					s.removeSpan(ss[i]);
	    					s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), tagStart, tagEnd,
	    							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    				}
	    				if(ss[i].getStyle()== android.graphics.Typeface.ITALIC &&tag.equals("em")){
	    					tagStart= s.getSpanStart(ss[i]);
	    					tagEnd= s.getSpanEnd(ss[i]);
	    					s.removeSpan(ss[i]);
	    					s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), tagStart, tagEnd,
	    							Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    				}
	    			}
	    		}else if(tag.equals("u")){
	    			UnderlineSpan[] us = s.getSpans(styleStart-1 , styleStart, UnderlineSpan.class);
	    			for (int i = 0; i<us.length; i++){
	    				int tagStart = s.getSpanStart(us[i]);
	    				int tagEnd = s.getSpanEnd(us[i]);
	    				s.removeSpan(us[i]);
	    				s.setSpan(new UnderlineSpan(), tagStart, tagEnd,
	    						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    		}else if(tag.equals("strike")){
	    			StrikethroughSpan[] ss = s.getSpans(styleStart-1, styleStart,
	    					StrikethroughSpan.class);
	    			for (int i =0; i<ss.length;i++){
	    				int tagStart = s.getSpanStart(ss[i]);
	    				int tagEnd = s.getSpanEnd(ss[i]);
	    				s.removeSpan(ss[i]);
	    				s.setSpan(new StrikethroughSpan(), tagStart, tagEnd, 
	    						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	    			}
	    		}
	    	}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	
    }
    
    //overrides OnClick to register formatButton clicks
	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();
		if(id==R.id.bold){
			formatBtnClicked(boldButton, "strong");
		}else if(id==R.id.ital){
			formatBtnClicked(emButton, "em");
		}else if(id==R.id.underline){
			formatBtnClicked(uButton, "u");
		}else if(id==R.id.strike){
			formatBtnClicked(strikeButton, "strike");
		}
	}
	//called when the save button is clicked. Opens the save layout
	public void onSaveBtnClick(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		isMainContent=false;		
		fragmentTransaction.replace(R.id.frame, saveFragment).commit();
	}
	
	//saves the file by calling saveFile and closes the save layout
	public void onDoneClick(View v){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		saveFile("temp");
		
		fragmentTransaction.remove(saveFragment).commit();
		isMainContent= true;
	}
	
	
	public void saveFile(String fileName){
        //Toast.makeText(getApplicationContext(), Environment.getExternalStorageDirectory().toString(), Toast.LENGTH_LONG).show();


//        File sdCard  = Environment.getExternalStorageDirectory();
//
//        File dir1 = new File(sdCard.getAbsolutePath());
//        dir1.mkdirs();
//        File file = new File(dir1, File.separator+fileName+".txt");
        File file = new File(Environment.getExternalStorageDirectory(), "/textFile.txt");
        try{
            file.createNewFile();


        }catch (IOException e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }


	}

    public void createNewDoc(){

    }


    public void undo(){
        Editable s = new SpannableStringBuilder(richText.getEditableText().subSequence(richText.length()-5,richText.length()));
        richText.getEditableText().delete(richText.length()-5,richText.length());

        undoStrings.add(s);
        undoIndex+=1;
    }

    public void redo(){
        richText.append(undoStrings.get(undoIndex-1));
    }

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		int position = Selection.getSelectionStart(s);
		if(position<0){
			position = 0;
		}
		
		if(position>0){
			if(styleStart>position){
				styleStart = position-1;
			}
			
			boolean exists = false;
			if(boldButton.isChecked()){
				StyleSpan[] ss = s.getSpans(styleStart, position, StyleSpan.class);
				exists = false;
				for(int i=0; i<ss.length;i++){
					if(ss[i].getStyle()==android.graphics.Typeface.BOLD){
						//s.removeSpan(ss[i]);
						exists= true;
					}
				}
				if(!exists){
					s.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), styleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
			if(emButton.isChecked()){
				StyleSpan[] ss = s.getSpans(styleStart, position, StyleSpan.class);
				exists =false;
				for(int i=0; i<ss.length;i++){
					if(ss[i].getStyle()==android.graphics.Typeface.ITALIC){
						//s.removeSpan(ss[i]);
						exists= true;
					}
				}
				if(!exists){
					s.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), styleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
			if(uButton.isChecked()){
				UnderlineSpan[] us = s.getSpans(styleStart, position, UnderlineSpan.class);
				exists = false;
				for(int i=0; i<us.length;i++){
					//s.removeSpan(us[i]);
					exists= true;
				}
				if(!exists){
					s.setSpan(new UnderlineSpan(), styleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
			if(strikeButton.isChecked()){
				StrikethroughSpan[] ss = s.getSpans(styleStart, position, StrikethroughSpan.class);
				exists=false;
				for(int i=0; i<ss.length;i++){
					//s.removeSpan(ss[i]);
					exists = true;
				}
				if(!exists){
					s.setSpan(new StrikethroughSpan(), styleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
				}
			}
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

	
	//custom drawerItemClickListener for the DrawerLayout
	private class DrawerItemClickListener implements ListView.OnItemClickListener{

		@Override
		public void onItemClick(AdapterView parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			View nextChild = ((ViewGroup) view).getChildAt(1);
			
			if(((TextView) nextChild).getText().equals("Save")){
				drawerLayout.closeDrawers();
				onSaveBtnClick();
			}else if(((TextView) nextChild).getText().equals("Open")){
                Toast.makeText(getApplicationContext(),"Open", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Export")){
                Toast.makeText(getApplicationContext(),"Export", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Clear")){
                Toast.makeText(getApplicationContext(),"Clear", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("New Document")){
                //Toast.makeText(getApplicationContext(),"New Document", Toast.LENGTH_SHORT).show();
                createNewDoc();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Undo")){
                undo();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Redo")){
                redo();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Page Color")){
                Toast.makeText(getApplicationContext(),"Page Color", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Bullets")){
                Toast.makeText(getApplicationContext(),"Bullets", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Insert Image")){
                Toast.makeText(getApplicationContext(),"Insert Image", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("About Jot")){
                Toast.makeText(getApplicationContext(),"About Jot", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Help")){
                Toast.makeText(getApplicationContext(),"Help", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
		}
    }
}