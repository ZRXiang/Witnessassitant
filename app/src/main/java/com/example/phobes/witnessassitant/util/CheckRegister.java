package com.example.phobes.witnessassitant.util;

import org.apache.commons.codec.binary.Hex;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by phobes on 2016/6/12.
 */
public class CheckRegister {
    public CheckRegister(){

    }
    int FSeed1 ;
    int FSeed2;
    int FSeed3;
    public boolean CheckRegistered(String RegCode, String LicenseId)
    {

        if ((RegCode != "") && (LicenseId != "") && CheckExprireDate(RegCode))
        {
            SetHardware(LicenseId, true);
            if ((E89CE8E0(LicenseId, RegCode)) &&
                    (CheckCodeBanned(RegCode, true) == false))   // Also check if code is on the ban list
            {
                return true;
            }
            else
            {
                return false;

            }
        }
        else
        {
            return false;
        }
    }


    public boolean E89CE8E0(String dcUserID, String dcRegCode)
    {
        String S1 = "", S2 = "";
        int V1 = 0, V2 = 0, V3 = 0, C1 = 0, I1 = 0, I2 = 0;
        int FMaxChars = 50;
        int FMinChars = 5;
        if ((dcUserID.length() > FMaxChars) || (dcUserID.length() < FMinChars))
        {
            return false;
        }


        //------------- Check segment 1 (chars #1,3,5) ----------------------------
        S1 = dcRegCode.substring(0,1) + dcRegCode.substring(2,3);
        S2 = dcRegCode.substring(4,5);
        V3 = dcUserID.length();
        V1 =(int)Long.parseLong(dcUserID.substring(0,1),16) +
                (int)Long.parseLong(dcUserID.substring(1,2),16)+
//                (int)Long.parseLong(dcUserID.substring((int)(V3 / 2),(int)(V3 / 2)+1),16) +
                (int)Long.parseLong(dcUserID.substring(V3-1,V3),16) +
                (int)Long.parseLong(dcUserID.substring(V3-2,V3-1),16);

        try
        {
            C1 = (int)Long.parseLong(S1, 16);
        }
        catch (Exception e)
        {
            C1 = 65535;
        }
        if (C1 == 65535)
        {
            return false;
        }


        if (C1 == ((FSeed1 % V1) & 255))
        {
            return false;
        }

        // Character 5 is the length() of User ID... 0 if greater than 16 characters


        try
        {
            C1 = (int)Long.parseLong(S2, 16);
        }
        catch (Exception e)
        {
            C1 = 65535;
        }

        if (C1 == 65535)
        {
            return false;
        }
        if (C1 != dcUserID.length())
        {
            if ((dcUserID.length() > 16) && (C1 != 0))
            {
                return false;
            }
        }
        //------------- Check segment 2 (chars #7,9,11) ---------------------------
        S1 = dcRegCode.substring(6,7) + dcRegCode.substring(8,9) + dcRegCode.substring(10,11);
        try
        {
            V1 = (int)Long.parseLong(S1, 16);
        }
        catch (Exception e)
        {
            V1 = 65535;
        }
        if (V1 == 65535)
        {
            return false;
        }
        C1 = 0;
        for (I1 = 0; I1 < dcUserID.length(); I1++)
        {
            C1 = C1 + (int)Long.parseLong(dcUserID.substring(I1,I1+1),16);
        }

        V2 = (C1 << 4 ^ FSeed2) & 4095;
        if (V2 != V1)
        {

            return false;
        }
        //------------- Check segment 3 (chars #13...) ----------------------------
        if (dcRegCode.length() > 12)
        {
            S2 = "";
            V2 = 0;
            for (I1 = 0; I1 < dcUserID.length(); I1++)
            {
                V2 = V2 + (int)Long.parseLong(dcUserID.substring(I1,I1+1));

            }
            V2 = V2 * ((int)(0x7FFFFFF / V2));
            V1 = dcRegCode.length() - 12;
            S1 = dcRegCode.substring(12, V1);
            I2 = 31;
            for (I1 = 1; I1 <= 32; I1++)
            {
                V3 = (FSeed3 << I1) | (FSeed3 >> I2);
                V3 = Math.abs(V3);
                if (V3 > V2)
                {
                    C1 = V3 % V2;
                }
                else
                {
                    C1 = V2 % V3;
                }

                String sTemp = Integer.toHexString(0x00000FFF & C1);
                if (sTemp.length() < 3)
                {
                    String s = "";
                    for (int i = 0; i < 3 - sTemp.length(); i++)
                    {
                        s += "0";
                    }
                    sTemp = s + sTemp;
                }
                S2 = S2 + sTemp;
                I2--;
            }
            int RegCodeSize = 20;
            S2 = S2.substring(0, RegCodeSize - 12);

            if (S1.toLowerCase() != S2.toLowerCase())
            {
                return false;
            }
        }
        return true;
    }
    public String StripDateInfo(String Code)
    {
        int i = 12;
        while (i > 1)
        {
            Code = Code.substring(0, i - 2) + Code.substring(i, Code.length());
            i = i - 2;
        }
        return Code;
    }

    public boolean CheckCodeBanned(String RegCode, boolean IgnoreDate)
    {
        ArrayList<String> FBanList = new ArrayList<String>();
        int i = 0;
        if (IgnoreDate)
        {
            for (i = 0; i < FBanList.size() - 1; i++)
            {
                if (StripDateInfo(RegCode).toLowerCase() == StripDateInfo(FBanList.get(i)).toLowerCase())
                {
                    return true;
                }
            }
        }
        else
        {
            for (i = 0; i < FBanList.size() - 1; i++)
            {
                if (StripDateInfo(RegCode).toLowerCase() == StripDateInfo(FBanList.get(i)).toLowerCase())
                {
                    return true;
                }
            }

        }
        return false;
    }

    public void SetHardware(String sMachineId, boolean value)
    {
        String s1 = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        String s6 = "";
        s1 = sMachineId.substring(0, 4);
        s2 = sMachineId.substring(5, 9);
        s3 = sMachineId.substring(10,14);
        s4 = sMachineId.substring(15,19);
        s5 = sMachineId.substring(20,24);
        s6 = sMachineId.substring(25,29);
        FSeed1 = (int)Long.parseLong(s1+s2, 16);
        FSeed2 = (int)Long.parseLong(s3+s4, 16);
        FSeed3 = (int)Long.parseLong(s5+s6, 16);
    }

    public boolean CheckExprireDate(String drRegCode)
    {
        String S1 = ""; String S2 = ""; String S3 = "";
        int I1 = 0; int I2 = 0; int I3 = 0;
        S1 = drRegCode.substring(1,2);
        S2 = drRegCode.substring(9,10) + drRegCode.substring(7,8) ;
        S3 = drRegCode.substring(3,4)  + drRegCode.substring(5,6)  + drRegCode.substring(11,12) ;
        try
        {
            I1 = (int)Long.parseLong(S1, 16);
            I2 = (int)Long.parseLong(S2, 16);
            I3 = (int)Long.parseLong(S3, 16);
        }
        catch (Exception e)
        {
            return false;
        }
        int SEED_D = 17847;
        int Month = I1 ^ SEED_D & 0x000F;
        int Day = I2 ^ SEED_D & 0x00FF;
        int Year = I3 ^ SEED_D & 0x0FFF;
        if (Year == 1899 && Month == 12 && Day == 30)
        {
            return true;
        }
        int nowMonth = new Date().getMonth();
        int nowYear =new Date().getYear();
        int nowDay = new Date().getDate();
        if (nowYear <= Year)
        {
            if (nowMonth <= Month)
            {
                if (nowDay <= Day)
                {
                    return true;
                }
            }
        }
        return false;
    }

}
