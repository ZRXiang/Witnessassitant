package com.example.phobes.witnessassitant.util;

/**
 * Created by phobes on 2016/6/1.
 */
public class Test {
    public static void main(String[] args){
//        String ser="AC6312AEE24B27E8D4E96B5F3CBB3E87";
//        String reg="BB520DE99A8C02504A09";
//        String ser="32034031036000534d444b32343530300";
//        String reg="2B820DA9CA4CE027B54B";
//        String ser="0003-06C3-BFEB-FBFF-7FDA-FBBF";
//        String reg="AB920D09AAFC437CC6DC";
//        String ser="AC6312AEE24B27E8D4E96B5F3CBB3E87";
//        String reg="FB020DE9AAFC04308510";
        String ser="0061-0F01-178B-FBFF-3E98-320B";
        String reg="8B920D89AAFCD9034648";
        if(new CheckRegister().CheckRegistered(reg,ser)){
            System.out.println("true");
        }else {
            System.out.println("false");
        }
    }
}
