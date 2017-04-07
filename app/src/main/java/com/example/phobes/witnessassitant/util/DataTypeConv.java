package com.example.phobes.witnessassitant.util;

import java.io.ByteArrayInputStream;

/**
 * Created by phobes on 2016/6/15.
 */
public class DataTypeConv {
//    public static byte[] intToByteArray(int i) throws Exception {
    public static byte[] intToByteArray(int a) throws Exception {
//        ByteArrayOutputStream buf = new ByteArrayOutputStream();
//        DataOutputStream out = new DataOutputStream(buf);
//        System.out.println("i:" + i);
//        out.writeInt(i);
//        byte[] b = buf.toByteArray();
//        System.out.println("i:" + b);
//        out.close();
//        buf.close();
//        return b;
        byte[] ret = new byte[4];
        ret[3] = (byte) (a & 0xFF);
        ret[2] = (byte) ((a >> 8) & 0xFF);
        ret[1] = (byte) ((a >> 16) & 0xFF);
        ret[0] = (byte) ((a >> 24) & 0xFF);
        return ret;
    }

    public static int ByteArrayToInt(byte b[]) throws Exception {
        int temp = 0, a = 0;
        ByteArrayInputStream buf = new ByteArrayInputStream(b);

        return buf.read();
    }
}
