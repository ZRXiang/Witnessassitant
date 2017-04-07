package com.example.phobes.witnessassitant.util;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * Created by phobes on 2016/5/31.
 */


public class StreamUtils {
    public static byte[] read(InputStream inStream) throws Exception{
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len = inStream.read(buffer)) != -1){
            outputStream.write(buffer, 0, len);
        }
        inStream.close();
        return outputStream.toByteArray();
    }
}
