package com.example.phobes.witnessassitant.util;

import android.util.Base64;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//import org.apache.commons.codec.binary.Base64;
/**
 * Created by phobes on 2016/6/10.
 */
public class Base64Util {




    /**
     * Method used for encode the file to base64 binary format
     * @param file
     * @return encoded file format
     */
    public static String encodeFileToBase64Binary(File file){
        String encodedfile = null;
        try {
            FileInputStream fileInputStreamReader = new FileInputStream(file);
            byte[] bytes = new byte[(int)file.length()];
            fileInputStreamReader.read(bytes);
            encodedfile = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }
    public static void main(String[] args){
//        File file = new File("C:\\Users\\phobes\\Desktop\\1234.PNG");//[B@66d3c617
        File file = new File("C:\\Users\\phobes\\Desktop\\define_location.png");
        System.out.println(encodeFileToBase64Binary(file));
    }
}
