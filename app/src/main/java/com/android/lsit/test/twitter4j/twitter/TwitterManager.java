package com.android.lsit.test.twitter4j.twitter;

import android.content.Context;
import android.text.TextUtils;

import com.android.lsit.test.twitter4j.R;

import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
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
    private Twitter mTwitter;
    private TwitterAPIConfiguration mAPIConfiguration;

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

    public void requestAPIConfiguration(final APIConfigurationListener listener) {
        throwExceptionIfIsNull(listener);

        if (mAPIConfiguration != null) {
            listener.onSuccess(mAPIConfiguration);
            return;
        }

        new Thread() {
            @Override
            public void run() {
                Twitter twitter = getTwitter();

                try {
                    mAPIConfiguration = twitter.getAPIConfiguration();
                    listener.onSuccess(mAPIConfiguration);

                } catch (TwitterException e) {
                    listener.onFail(e);

                } catch (IllegalStateException e) {
                    listener.onFail(new TwitterException("requestAPIConfiguration IllegalStateException", e, TwitterStatusCode.UNAUTHORIZED.getStatusCode()));
                }
            }
        }.start();
    }

    private void throwExceptionIfIsNull(Object object) {
        if (object == null) {
            throw new NullPointerException(object.getClass().getName() + "is Null");
        }
    }

    public Twitter getTwitter() {
        if (mTwitter == null) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(mTwitterKey);
            builder.setOAuthConsumerSecret(mTwitterSecret);
            builder.setOAuthAccessToken(mToken);
            builder.setOAuthAccessTokenSecret(mTokenSecret);

            twitter4j.conf.Configuration configuration = builder.build();
            mTwitter = new TwitterFactory(configuration).getInstance();
        }

        return mTwitter;
    }

    public void logout() {
        mToken = null;
        mTokenSecret = null;
        mTwitter = null;

        mPrefs.removeToken();
        mPrefs.removeSecret();
    }
}
