package com.noahl98.jotmobile;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
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
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends Activity implements RichText.EditTextImeBackListener, OnTouchListener, OnClickListener, TextWatcher{
	private String[] drawerListViewItems1;
	private String[] drawerListViewItems2;
    private ArrayList<Editable> undoStrings;

    private int undoIndex;

	private int styleStart;
	
	private ListView drawerListView1;
	private ListView drawerListView2;
	
	private RelativeLayout formatBar;
	
	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle actionBarDrawerToggle;
	private RichText richText;
	private SaveFragment saveFragment;
    private CreateNewDoc createNewDocFragment;
    private About aboutFragment;
    private Help helpFragement;
	
	private ToggleButton boldButton;
	private ToggleButton emButton;
	private ToggleButton uButton;
	private ToggleButton strikeButton;
	
	private boolean keyboardShown;
	private boolean alreadyShown;
	private boolean isMainContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //finds the left drawer
        drawerListView1 = (ListView) findViewById(R.id.left_drawer);
        //finds right drawer
        drawerListView2 = (ListView)findViewById(R.id.right_drawer);

        //loads the first drawer
        loadDrawer1();

        //loads the second drawer
        loadDrawer2();
 
        //finds the drawer layout
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
 
        //assign ActionBarDrawerToggle
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close);
        
        //sets the drawer listener
        drawerLayout.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
        	@Override
        	public void onDrawerOpened(View drawerLayout){
                //hides format bar when a drawer is opened
        		if(keyboardShown){
        			hideFormatBarAnim();
        		}
        	}
        	
        	@Override
        	public void onDrawerClosed(View drawerLayout){
                //shows format bar when closed
        		showFormatBar();
        	}
		});
 
        //enable and show "up" arrow
        getActionBar().setDisplayHomeAsUpEnabled(true); 
 
        // just styling option for the drawer layout
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        //item click listener for drawer items
        drawerListView1.setOnItemClickListener(new DrawerItemClickListener());
        drawerListView2.setOnItemClickListener(new DrawerItemClickListener());

        //sets a long click listener for the first drawer only
        drawerListView1.setOnItemLongClickListener(new DrawerItemLongClickListener());

        isMainContent= true;
        alreadyShown=false;
        keyboardShown=false;

        undoIndex=0;

        undoStrings = new ArrayList<Editable>();
        
        //assigns formatBar to its XML layout 
        formatBar = (RelativeLayout)findViewById(R.id.formatBar);
        
        //defines save fragment
        saveFragment = new SaveFragment();

        //defines aboutFragment
        aboutFragment= new About();

        //defines helpFragment
        helpFragement= new Help();

        //defines createNewDocFragment
        createNewDocFragment= new CreateNewDoc();
        
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

        //creates the default saving directory
        Doc.makeDefaultDir();
        lookForFiles();
    }


    ////////////////////////////////////////
    //////Funtions for the DawerLayout//////
    ////////////////////////////////////////
	public void loadDrawer1(){
        //sets up the first drawer
        Model.loadModel1();
        //sets the correct items(defined in Model.java)
        drawerListViewItems1 = new String[Model.Items1.size()];

        //sets Id's to strings
        for(int i= 0; i<drawerListViewItems1.length;i++){
            drawerListViewItems1[i]= Integer.toString(i+1);
        }
        //applies the custom array adapter
        ItemAdapter adapter1 = new ItemAdapter(this, R.layout.drawer_listview_item, drawerListViewItems1, 1);
        drawerListView1.setAdapter(adapter1);
    }

    public void loadDrawer2(){
        //sets up the second drawer
        Model.loadModel2();

        //sets the correct items(defined in Model.java)
        drawerListViewItems2 = new String[Model.Items2.size()];

        //changes ids to strings
        for(int i =0; i<drawerListViewItems2.length;i++){
            drawerListViewItems2[i] = Integer.toString(i+1);
        }

        //sets custom adapter
        ItemAdapter adapter2 = new ItemAdapter(this, R.layout.drawer_listview_item, drawerListViewItems2, 2);
        drawerListView2.setAdapter(adapter2);

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

    ///////////////////////////////////////
    //////Functions for the formatBar//////
    ///////////////////////////////////////

    //logic for when to show the formatBar
    private void softKeyboardHook(){
    	final View scrollView= findViewById(R.id.scrollView);
    	scrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
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
                    exists = true;
                }
                if(!exists){
                    s.setSpan(new StrikethroughSpan(), styleStart, position, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                }
            }
        }
    }

    ////////////////////////////////
    //////Functions for saving//////
    ////////////////////////////////

	//called when the save button is clicked. Opens the save layout
	public void onSaveBtnClick(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		
		InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

		isMainContent=false;		
		fragmentTransaction.add(R.id.frame, saveFragment).addToBackStack(null).commit();
	}

    //searches for files in the specified directory and adds them to the drawerLayout
    public void lookForFiles(){
        File[] files = Doc.docsFolder.listFiles();
        try {
            Model.Items1.get(5).setName(files[0].getName());
            Model.Items1.get(6).setName(files[1].getName());
            Model.Items1.get(7).setName(files[2].getName());
            Model.Items1.get(8).setName(files[3].getName());
        }catch(Exception e){
            e.printStackTrace();
        }
        loadDrawer1();
    }
	
	//saves the file and closes the save layout
	public void onSaveDoneClick(View v){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //defines a new Doc
        Doc file = new Doc(SaveFragment.getDocTitle());

        //save the current text to the file
        file.saveFile(richText.getText());

        //updates the list on the drawer layout
        lookForFiles();

        //changes back to the main view
		fragmentTransaction.remove(saveFragment).commit();
		isMainContent= true;
	}

    public void cancelSave(View view){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(saveFragment).commit();
        isMainContent=true;
    }

    ////////////////////////////////////////////////
    //////Functions for creating new documents//////
    ////////////////////////////////////////////////

    public void createNewDoc(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        isMainContent=false;
        fragmentTransaction.add(R.id.frame, createNewDocFragment).addToBackStack(null).commit();
    }

    public void onCreateClicked(View v){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        //defines a new Doc
        Doc file = new Doc(CreateNewDoc.getDocTitle());

        //updates the list on the drawer layout
        lookForFiles();

        //changes back to the main view
        fragmentTransaction.remove(createNewDocFragment).commit();
        isMainContent= true;
    }

    public void cancelCreate(View v){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(createNewDocFragment).commit();
        isMainContent=true;
    }

    ///////////////////////////////////
    //////Various other functions//////
    ///////////////////////////////////
    public void undo(){
        Editable s;
        if(richText.length()>=5){
            s = new SpannableStringBuilder(richText.getEditableText().subSequence(richText.length()-5,richText.length()));
            richText.getEditableText().delete(richText.length()-5,richText.length());
        }else{
            s=new SpannableStringBuilder(richText.getEditableText().subSequence(0,richText.length()));
            richText.getEditableText().delete(0,richText.length());
        }

        if(s==null){
            return;
        }
        undoStrings.add(s);
        undoIndex+=1;
    }

    public void redo(){
        richText.append(undoStrings.get(undoIndex-1));
    }

    public void clearText(){
        richText.setText("");
    }

    @Override
    public void onBackPressed(){
        if(createNewDocFragment.isShowing){
            createNewDocFragment.onBackPressed();
            isMainContent=true;
        }
        if(saveFragment.isShowing){
            saveFragment.onBackPressed();
            isMainContent=true;
        }
        if(helpFragement.isShowing){
            helpFragement.onBackPressed();
            isMainContent=true;
        }
        if(aboutFragment.isShowing){
            aboutFragment.onBackPressed();
            isMainContent=true;
        }
        super.onBackPressed();

    }

    public void launchAbout(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        isMainContent=false;
        fragmentTransaction.add(R.id.frame, aboutFragment).addToBackStack(null).commit();
    }

    public void launchHelp(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        isMainContent=false;
        fragmentTransaction.add(R.id.frame, helpFragement).addToBackStack(null).commit();
    }

    public void openFile(){

    }

    @Override
    public void onResume(){
        super.onResume();
        lookForFiles();
    }

    ////////////////////
    //////NOT USED//////
    ////////////////////
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

    ////////////////////
    ////////////////////

	
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
                clearText();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("New Document")){
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
                launchAbout();
                drawerLayout.closeDrawers();
            }else if(((TextView) nextChild).getText().equals("Help")){
                launchHelp();
                drawerLayout.closeDrawers();
            }
		}
    }

    private class DrawerItemLongClickListener implements ListView.OnItemLongClickListener{
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id){

            View nextChild = ((ViewGroup) view).getChildAt(1);
            final String childText = ((TextView) nextChild).getText().toString();

            if(childText.equals("Save")){
                return false;
            }else if(childText.equals("Open")){
                return false;
            }else if(childText.equals("Export")){
                return false;
            }else if(childText.equals("Clear")){
                return false;
            }else if(childText.equals("New Document")){
                return false;
            }else{
                File files[] = Doc.docsFolder.listFiles();
                /*for(int i=0; i<files.length; i++){
                    if(files[i].equals(childText)){
                        PopupMenu popupMenu = new PopupMenu(MainActivity.this, nextChild);
                        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if(menuItem.getTitle()=="Delete"){
                                    Doc.getFiles(childText).delete();
                                    return true;
                                }
                                return false;
                            }
                        });
                    }
                }*/
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), nextChild);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getTitle().equals("Delete")) {
                            File tempFile = Doc.getFiles(childText);
                            if(Doc.deleteFile(tempFile)){
                                Toast.makeText(getApplicationContext(), "This code is being run", Toast.LENGTH_SHORT).show();
                            }
                            lookForFiles();
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
            return true;
        }
    }
}