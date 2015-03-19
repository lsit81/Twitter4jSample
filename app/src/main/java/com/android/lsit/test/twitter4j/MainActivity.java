package com.android.lsit.test.twitter4j;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.lsit.test.twitter4j.twitter.APIConfigurationListener;
import com.android.lsit.test.twitter4j.twitter.TwitterManager;
import com.android.lsit.test.twitter4j.twitter.TwitterOAuthWebView;
import com.android.lsit.test.twitter4j.twitter.TwitterStatusCode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterAPIConfiguration;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;


public class MainActivity extends ActionBarActivity implements TwitterOAuthWebView.Listener {
    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int REQ_CODE_SELECT_IMAGE = 1;

    private TwitterManager mTwitterManager;

    private Handler mHandler;

    private TwitterOAuthWebView mWebView;
    private ViewGroup mPostLayout;
    private EditText mPostEdit;

    private ImageView mSelectedImage;
    private Uri mSelectedImageUri;
    private String mShareMediaFile;

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

        } else {
            /** 트위터 전송시 필요한 각가지 정보를 가져온다. */
            mTwitterManager.requestAPIConfiguration(new APIConfigurationListener() {
                @Override
                public void onSuccess(TwitterAPIConfiguration configuration) {
                    Log.d(TAG, "getCharactersReservedPerMedia = " + configuration.getCharactersReservedPerMedia());
                    Log.d(TAG, "getShortURLLengthHttps = " + configuration.getShortURLLengthHttps());
                    Log.d(TAG, "getShortURLLength = " + configuration.getShortURLLength());
                    Log.d(TAG, "getMaxMediaPerUpload = " + configuration.getMaxMediaPerUpload());
                    Log.d(TAG, "getNonUsernamePaths = " + configuration.getNonUsernamePaths());
                    Log.d(TAG, "getPhotoSizeLimit = " + configuration.getPhotoSizeLimit());
                    Log.d(TAG, "getPhotoSizes = " + configuration.getPhotoSizes());
                    Log.d(TAG, "getAccessLevel = " + configuration.getAccessLevel());
                    Log.d(TAG, "getRateLimitStatus = " + configuration.getRateLimitStatus());
                }

                @Override
                public void onFail(TwitterException exception) {

                }
            });
        }
    }

    private void initViews() {
        mWebView = (TwitterOAuthWebView) findViewById(R.id.webview);
        mPostLayout = (ViewGroup) findViewById(R.id.post_layout);
        mPostEdit = (EditText) findViewById(R.id.post_text);
        mSelectedImage = (ImageView) findViewById(R.id.selected_image);

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
        mShareMediaFile = getImageNameToUri(mSelectedImageUri);

        new Thread() {
            @Override
            public void run() {
                try {
                    String postData = getPostData();
                    Twitter twitter = mTwitterManager.getTwitter();

                    StatusUpdate status = new StatusUpdate(postData);
                    if (TextUtils.isEmpty(mShareMediaFile) == false) {
                        status.setMedia(new File(mShareMediaFile));
                    }
                    twitter.updateStatus(status);

                    showToastMessage("트윗에 성공적으로 전송 되었습니다.\n" + getPostData());

                } catch(TwitterException e) {
                    if (TwitterStatusCode.UNABLE_NETWORK_TO_CONNECT.getStatusCode() == e.getStatusCode()) {
                        Log.d(TAG, "TwitterException (unable to connect)");
                        showToastMessage("unable to connect");
                    } else if (TwitterStatusCode.FORBIDDEN.getStatusCode() == e.getStatusCode()) {
                        Log.d(TAG, "TwitterException (duplicate tweet)");
                        showToastMessage("duplicate tweet");
                    } else {
                        Log.d(TAG, "TwitterException " + e.toString());
                        showToastMessage("etc");
                    }
                }
            }
        }.start();
    }

    public void onClickSelectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (REQ_CODE_SELECT_IMAGE != requestCode) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        mSelectedImageUri = data.getData();
        try {
            //이미지 데이터를 비트맵으로 받아온다.
            Bitmap image_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
            mSelectedImage.setImageBitmap(image_bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImageNameToUri(Uri data) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        stopManagingCursor(cursor);

        return imgPath;
    }
}