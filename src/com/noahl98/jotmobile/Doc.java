package com.noahl98.jotmobile;

import android.os.Environment;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by noahl98 on 2/13/14.
 */
public class Doc extends File{

    public static final File defaultDir = new File(Environment.getExternalStorageDirectory().getPath(), File.separator+ "Jot");
    public static final File docsFolder = new File(defaultDir, File.separator+ "Documents");
    public static final File xmlFolder = new File(defaultDir, File.separator+"Data");

    private String fileName;
    public static File[] files = docsFolder.listFiles();

    public static int styleSpanInt;
    public static int strikethoughSpanInt;
    public static int underlineSpanInt;

    public Doc(String name){
        super(name);
        this.fileName = name;

       saveFile(new SpannableStringBuilder(" "));
    }
    //function to make sure all directories are created
    public static void makeDefaultDir(){
        if(!defaultDir.exists()){
            defaultDir.mkdirs();
        }
        if(!docsFolder.exists()){
            docsFolder.mkdirs();
        }
        if(!xmlFolder.exists()){
            xmlFolder.mkdirs();
        }
    }
    //function for creating the necessary files while saving
    public void saveFile(Editable text){
        File textFile = new File(docsFolder.getAbsolutePath(), File.separator+fileName+".txt");
        File xmlFile = new File(xmlFolder.getAbsolutePath(), File.separator+fileName+ ".txt");

        StyleSpan[] styleSpans =text.getSpans(0, text.length(), StyleSpan.class);
        StrikethroughSpan[] strikethroughSpans = text.getSpans(0, text.length(), StrikethroughSpan.class);
        UnderlineSpan[] underlineSpans = text.getSpans(0,text.length(), UnderlineSpan.class);

        //writes current text to the specified file
        try{
            textFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(textFile);
            OutputStreamWriter outStream = new OutputStreamWriter(fOut);
            outStream.write(text.toString());
            outStream.close();
            fOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        //creates the XML file for saving the spans
        try{
            xmlFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(xmlFile);
            fOut.write(convertToXML(styleSpans, "styleSpan", "item").getBytes());
            styleSpanInt = styleSpans.length;
            fOut.flush();
            fOut.write(convertToXML(strikethroughSpans, "strikethroughSpan", "item").getBytes());
            strikethoughSpanInt=strikethroughSpans.length;
            fOut.flush();
            fOut.write(convertToXML(underlineSpans, "underlineSpan", "item").getBytes());
            underlineSpanInt= underlineSpans.length;
            fOut.flush();
            fOut.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    //function for getting a specific file
    public static File getFiles(String fileName){
        int index=-1;
        for (int i=0; i<files.length;i++){
            if(files[i].getName().equals(fileName)){
                index = i;
            }
        }
        return files[index];
    }

    //for use while creating the XML file
    private String convertToXML(Object[] args, String rootName, String elemName){
        String xmlString = "<"+rootName+">\n";

        for(int i=0; i<args.length; i++){
            xmlString += "  <"+ elemName+">" + args[i].toString() + "<"+ elemName+">\n";
        }
        xmlString += "<"+rootName+">";

        return xmlString;
    }

    public static boolean deleteFile (File file){
        if(file.exists()){
            file.delete();
            return true;
        }
        return false;
    }
}