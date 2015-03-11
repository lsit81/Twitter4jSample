package com.android.lsit.test.twitter4j.twitter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by naver on 2015. 3. 11..
 */
class TwitterPrefs {
    static private final String TWITTERID = "twitterPreferences";
    static private final String TWITTER_TOKEN = "access_token";
    static private final String TWITTER_TOKEN_SECRET = "token_secret";

    private final Context mContext;

    public TwitterPrefs(Context context) {
        mContext = context;
    }

    public void setToken(String token) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TWITTERID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TWITTER_TOKEN, token);
        editor.commit();
    }

    public void setTokenSecret(String tokenSecret) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TWITTERID, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(TWITTER_TOKEN_SECRET, tokenSecret);
        editor.commit();
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TWITTERID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TWITTER_TOKEN, null);
    }

    public String getTokenSecret() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(TWITTERID, Context.MODE_PRIVATE);
        return sharedPreferences.getString(TWITTER_TOKEN_SECRET, null);
    }
}
