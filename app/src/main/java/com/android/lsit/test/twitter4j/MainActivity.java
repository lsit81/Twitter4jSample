package com.android.lsit.test.twitter4j;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.android.lsit.test.twitter4j.twitter.TwitterManager;
import com.android.lsit.test.twitter4j.twitter.TwitterOAuthWebView;

import java.io.File;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class MainActivity extends ActionBarActivity implements TwitterOAuthWebView.Listener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private TwitterManager mTwitterManager;

    private Handler mHandler;

    private TwitterOAuthWebView mWebView;
    private ViewGroup mPostLayout;
    private EditText mPostEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHandler = new Handler();
        mTwitterManager = new TwitterManager(getApplicationContext());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        initViews();

        if (mTwitterManager.isNeedLogin()) {
            mWebView.requestLogin(mTwitterManager.getTwitter(), this);
        }
    }

    private void initViews() {
        mWebView = (TwitterOAuthWebView) findViewById(R.id.webview);
        mPostLayout = (ViewGroup) findViewById(R.id.post_layout);
        mPostEdit = (EditText) findViewById(R.id.post_text);

        if (mTwitterManager.isNeedLogin()) {
            mWebView.setVisibility(View.VISIBLE);
            mPostLayout.setVisibility(View.GONE);

        } else {
            mWebView.setVisibility(View.GONE);
            mPostLayout.setVisibility(View.VISIBLE);
        }
    }

    public void onClickPost(View view) {
        Log.d(TAG, "onClickPost");

        new Thread() {
            @Override
            public void run() {
                try {
                    String postData = getPostData();
                    Twitter twitter = mTwitterManager.getTwitter();

                    StatusUpdate status = new StatusUpdate(postData);
                    status.setMedia(new File("/mnt/sdcard/DCIM/100LGDSC/CAM00003.jpg"));
                    twitter.updateStatus(status);

                    showToastMessage("트윗에 성공적으로 전송 되었습니다.\n" + getPostData());

                } catch(TwitterException e) {
                    switch (e.getStatusCode()) {
                        case (-1):
                            Log.d(TAG, "TwitterException (unable to connect)");
                            showToastMessage("unable to connect");
                            break;
                        case(403):
                            Log.d(TAG, "TwitterException (duplicate tweet)");
                            showToastMessage("duplicate tweet");
                            break;
                        default:
                            Log.d(TAG, "TwitterException " + e.toString());
                            showToastMessage("etc");

                    } //end switch
                }
            }
        }.start();
    }

    private void showToastMessage(final String text) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), text, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onSuccess(TwitterOAuthWebView view, AccessToken accessToken) {
        Log.d(TAG, "onSuccess() token : " + accessToken.getToken());
        Log.d(TAG, "onSuccess() getTokenSecret : " + accessToken.getTokenSecret());

        mTwitterManager.setTwitterAccessToken(accessToken);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initViews();
            }
        });
    }

    @Override
    public void onFailure(TwitterOAuthWebView view, TwitterOAuthWebView.Result result) {
        Log.d(TAG, "onFailure() result = " + result);
    }

    public String getPostData() {
        return mPostEdit.getText().toString();
    }
}