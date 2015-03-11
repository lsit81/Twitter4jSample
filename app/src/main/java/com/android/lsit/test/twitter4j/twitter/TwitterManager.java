package com.android.lsit.test.twitter4j.twitter;

import android.content.Context;
import android.text.TextUtils;

import com.android.lsit.test.twitter4j.R;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Created by naver on 2015. 3. 10..
 */
public class TwitterManager {
    private final String mTwitterKey;
    private final String mTwitterSecret;

    private final TwitterPrefs mPrefs;
    private String mToken;

    private String mTokenSecret;

    public TwitterManager(Context context) {
        mPrefs = new TwitterPrefs(context);

        mTwitterKey = context.getResources().getString(R.string.twitter_key);
        mTwitterSecret  = context.getResources().getString(R.string.twitter_secret);

        loadTwitterOAuth();
    }

    public boolean isNeedLogin() {
        loadTwitterOAuth();

        return TextUtils.isEmpty(mToken) || TextUtils.isEmpty(mTokenSecret);
    }

    private void loadTwitterOAuth() {
        mToken = mPrefs.getToken();
        mTokenSecret = mPrefs.getTokenSecret();
    }

    public void setTwitterAccessToken(AccessToken accessToken) {
        mToken = accessToken.getToken();
        mTokenSecret = accessToken.getTokenSecret();

        mPrefs.setToken(mToken);
        mPrefs.setTokenSecret(mTokenSecret);
    }

    public Twitter getTwitter() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(mTwitterKey);
        builder.setOAuthConsumerSecret(mTwitterSecret);
        builder.setOAuthAccessToken(mToken);
        builder.setOAuthAccessTokenSecret(mTokenSecret);

        twitter4j.conf.Configuration configuration = builder.build();
        return new TwitterFactory(configuration).getInstance();
    }
}
