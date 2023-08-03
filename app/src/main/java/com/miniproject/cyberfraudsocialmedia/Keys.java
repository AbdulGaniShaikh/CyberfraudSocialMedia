package com.miniproject.cyberfraudsocialmedia;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Keys {

    public static String USER_COLLECTION = "users";

    public static String USER_NAME = "name";
    public static String USER_DESP = "desp";
    public static String USER_AVATAR = "avatar";
    public static String USER_EMAIL = "email";
    public static String USER_FRIENDS = "friends";
    public static String USER_POSTS = "posts";


    /**************************/

    public static String HASHTAG_COLLECTION = "hashtags";

    public static String HASHTAG_POSTID = "postid";

    /*************************/

    public static String POST_COLLECTION = "posts";

    public static String POST_BY = "postby";
    public static String POST_BYNAME = "postbyname";
    public static String POST_TIMESTAMP = "posttime";
    public static String POST_IMG = "postimg";
    public static String POST_TAGS = "posttag";
    public static String POST_TEXT = "posttext";

    /*************************/

    public static String CHATROOM_COLLECTION = "chatroom";
    public static String CHATROOM_NAME = "name";
    public static String CHATROOM_MESSAGES = "messages";

    public static String CHAT_COLLECTION = "chat";
    public static String CHAT_MESSAGE = "message";
    public static String CHAT_NAME = "name";
    public static String CHAT_TIME = "time";

    /************************/

    public static String EXTRA = "extra";


    public static Map<String,Object> getTags(String s){
        Map<String,Object> tags = new HashMap<>();
        String regexPattern = "(#\\w+)";

        Pattern p = Pattern.compile(regexPattern);
        Matcher m = p.matcher(s);

        while (m.find())
            tags.put(m.group(1),true);

        return tags;
    }

    public static SpannableString getHighlightedText(String s){
        SpannableString ss = new SpannableString(s);
        int start=-1,end=-1;

        for(int i=0;i<s.length();i++){
            if(s.charAt(i)=='#'){
                start=i;
            }else if(s.charAt(i)==' ' && start!=-1){
                end=i;
            }
            if(start!=-1 && end!=-1){
                ss.setSpan(new ForegroundColorSpan(Color.argb(255,0,153,204)),start,end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                start = end = -1;
            }
        }

        if (start!=-1) {
            end = s.length();
            ss.setSpan(new ForegroundColorSpan(Color.argb(255,0,153,204)), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return ss;
    }

    /*
    public static int getAvatar(int i){
        switch (i){
            case 1:
                return R.drawable.ic_avat1;
            case 2:
                return R.drawable.ic_avat2;
            case 3:
                return R.drawable.ic_avat3;
            case 4:
                return R.drawable.ic_avat4;
            case 5:
                return R.drawable.ic_avat5;
            case 6:
                return R.drawable.ic_avat6;
            case 7:
                return R.drawable.ic_avat7;
            case 8:
                return R.drawable.ic_avat8;
            case 9:
                return R.drawable.ic_avat9;
            case 10:
                return R.drawable.ic_avat10;
            case 11:
                return R.drawable.ic_avat11;
            case 12:
                return R.drawable.ic_avat12;
            case 13:
                return R.drawable.ic_avat13;
            case 14:
                return R.drawable.ic_avat14;
            case 15:
                return R.drawable.ic_avat15;
            default:
                return R.drawable.ic_avat0;
        }
    }*/
}
