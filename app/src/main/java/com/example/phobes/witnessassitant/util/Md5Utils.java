package com.example.phobes.witnessassitant.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by phobes on 2016/6/1.
 */
public class Md5Utils {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHexString(byte[] b) { //String to byte
        StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
            sb.append(HEX_DIGITS[b[i] & 0x0f]);
        }
        return sb.toString();
    }

    public static String md5(String s) {
        // TODO Auto-generated method stub
        {
            try { // Create MD5 Hash
                MessageDigest digest = MessageDigest.getInstance("MD5");
                digest.update(s.getBytes());
                byte messageDigest[] = digest.digest();
                return toHexString(messageDigest);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return "";
        }
    }
    // test
    public static void main(String[] args) {
        System.out.println("MD5"+md5("sj4-9101030101"));
    }
}
