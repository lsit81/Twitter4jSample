package com.android.lsit.test.twitter4j.twitter;

import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;

/**
 * Created by naver on 2015. 3. 13..
 */
public interface APIConfigurationListener {
    public void onSuccess(TwitterAPIConfiguration configuration);
    public void onFail(TwitterException exception);
}
