package com.noahl98.jotmobile;

import android.os.Environment;
import android.widget.Filter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by noahl98 on 2/13/14.
 */
public class Doc extends File{

    public static final File defaultDir = new File(Environment.getExternalStorageDirectory().getPath(), File.separator+ "Jot");
    public static final File docsFolder = new File(defaultDir, File.separator+ "Documents");
    public static final File xmlFolder = new File(defaultDir, File.separator+"XML");

    private String fileName;
    public static File[] files = defaultDir.listFiles();

    public Doc(String name){
        super(name);
        this.fileName = name;

       saveFile(" ");
    }

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

    public void saveFile(String text){
        File file = new File(docsFolder.getAbsolutePath(), File.separator+fileName+".txt");
        try{
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter outStream = new OutputStreamWriter(fOut);
            outStream.write(text);
            outStream.close();
            fOut.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public static File getFiles(int index){
        if(index>files.length){
            return null;
        }
        return files[index];
    }
}