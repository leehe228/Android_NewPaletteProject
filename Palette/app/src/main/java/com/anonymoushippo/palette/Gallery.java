package com.anonymoushippo.palette;

import java.util.ArrayList;
import java.util.Arrays;

public class Gallery {
    private String CODE;
    private String TITLE;
    private String INFORMATION;
    private String CREATOR;
    private int NUMBER;
    private String CATEGORY;
    private String DUE_DAY;
    private ArrayList<String> ART_TITLES;
    private ArrayList<String> ART_CONTENT;

    private Boolean isREADY = false;

    public Gallery(String Code) {

        CODE = Code;

        new Thread() {
            public void run() {
                String result = HttpPostData.POST("gallery/getExhibition/", new String[]{"code"}, new String[]{CODE});

                if (result.equals("-1") || result.equals("SEND_FAIL") || result.equals("NO_DATA_RECEIVED")) {
                    TITLE = "";
                    INFORMATION = "";
                    CREATOR = "";
                    CATEGORY = "";
                    DUE_DAY = "";
                } else {
                    TITLE = result.split("&")[0];
                    CREATOR = result.split("&")[1];
                    INFORMATION = result.split("&")[2];
                    NUMBER = Integer.parseInt(result.split("&")[3]);
                    ART_TITLES = new ArrayList<>(Arrays.asList(result.split("&")[4].split("-")));
                    ART_CONTENT = new ArrayList<>(Arrays.asList(result.split("&")[5].split("-")));
                    DUE_DAY = result.split("&")[6];
                    CATEGORY = result.split("&")[7];
                }
            }
        }.start();
    }

    public String getCODE() {
        return CODE;
    }

    public ArrayList<String> getART_TITLES() {
        return ART_TITLES;
    }

    public ArrayList<String> getART_CONTENT() {
        return ART_CONTENT;
    }

    public Boolean getIsREADY(){
        return isREADY;
    }

    public String getTITLE(){
        return TITLE;
    }

    public String getCREATOR(){
        return CREATOR;
    }

    public String getINFORMATION(){
        return INFORMATION;
    }

    public String getCATEGORY(){
        return CATEGORY;
    }

    public String getDUE_DAY(){
        return DUE_DAY;
    }

    public int getNUMBER(){
        return NUMBER;
    }
}
