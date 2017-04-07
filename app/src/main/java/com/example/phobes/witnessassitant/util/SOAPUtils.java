package com.example.phobes.witnessassitant.util;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;

import java.io.InputStream;

/**
 * Created by phobes on 2016/6/1.
 */
public class SOAPUtils {
    public static String parseSOAP(InputStream xml, String sufixName)throws Exception {
        XmlPullParser pullParser = Xml.newPullParser();
         int i=  pullParser.getColumnNumber();
        pullParser.setInput(xml, "UTF-8");
        int event = pullParser.getEventType();
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {
                case XmlPullParser.START_TAG:
                    if (sufixName.equals(pullParser.getName())) {
                        return pullParser.nextText();
                    }
                    break;
            }
            event = pullParser.next();
        }
        return null;
    }

}
