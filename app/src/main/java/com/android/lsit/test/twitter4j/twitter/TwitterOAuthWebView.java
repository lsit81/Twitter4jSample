package com.android.lsit.test.twitter4j.twitter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

/**
 * Created by naver on 2015. 3. 10..
 */
public class TwitterOAuthWebView extends WebView {
    private static final String TAG = TwitterOAuthWebView.class.getSimpleName();
    private Twitter mTwitter;
    private RequestToken mRequestToken = null;

    private Listener mListener;

    public TwitterOAuthWebView(Context context) {
        super(context);

        init();
    }

    public TwitterOAuthWebView(Context context, AttributeSet attrs) {
        super(context, attrs);

        init();
    }

    public TwitterOAuthWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        init();
    }

    private void init() {
        settingDefaultWebView();
    }

    private void settingDefaultWebView() {
        if (isInEditMode()) {
            /**
             * android layout tool에서 preview를 통해서 TwitterOAuthWebView를 볼때
             * 발생되는 오류를 해결하기 위해서 사용됨.
             */
            return;
        }

        WebSettings settings = this.getSettings();

        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setBuiltInZoomControls(false);
    }

    public void requestLogin(Twitter twitter, Listener listener) {
        throwExceptionIfNull(listener);
        throwExceptionIfNull(twitter);

        mListener = listener;
        mTwitter = twitter;

        resetWebViewClient();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    /**
                     * OAuthRequestToken은 별도 쓰레드에서 동작되어야 한다.
                     */
                    mRequestToken = mTwitter.getOAuthRequestToken();
                    post(new Runnable() {
                        @Override
                        public void run() {
                            loadUrl(Uri.parse(mRequestToken.getAuthorizationURL()).toString());
                        }
                    });

                } catch (TwitterException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private void resetWebViewClient() {
        TwitterWebViewClient webviewClient = new TwitterWebViewClient();
        addJavascriptInterface(webviewClient, "GetPinNumber");
        setWebViewClient(webviewClient);
    }

    private void throwExceptionIfNull(Object object) {
        if (object == null) {
            throw new NullPointerException(String.format("%s is null", mListener.getClass().getName()));
        }
    }

    private class TwitterWebViewClient extends WebViewClient { // IPhone 쪽 공식 Twitter sdk의 동작을 따르는 WebViewClient
        private boolean mIsFirstLoad = true;
        private boolean mIsPinLoaded = false;

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "onPageFinished : " + url);

            CookieSyncManager.getInstance().sync();
            if (mIsFirstLoad) {
                mIsFirstLoad = false;
                return;
            } else if (!mIsPinLoaded) {
                //Mobile page에서 Pin number를 뽑아오는 방식으로 여기에 대한 script 실행 식 및 script 결과를 파싱하는 로직은 Iphone쪽 공식 sdk의 코드에 따름.
                StringBuffer script = new StringBuffer();
                script.append("javascript:");
                script.append("var d = document.getElementById('oauth-pin'); if (d == null) d = document.getElementById('oauth_pin'); if (d) d = d.innerHTML; if (d == null) {var r = new RegExp('\\\\s[0-9]+\\\\s'); d = r.exec(document.body.innerHTML); if (d.length > 0) d = d[0];} d.replace(/^\\s*/, '').replace(/\\s*$/, ''); d;");
                script.append("window.GetPinNumber.onReceivedPinNumber(document.body.innerText, d); ");
                view.loadUrl(script.toString());

                Log.d(TAG, "onPageFinished : load script ");
            }
        }

        @SuppressWarnings("unused")
        public void onReceivedPinNumber(String aHtml, String aPin) {
            CharSequence pin = parsePinNumber(aHtml, aPin);
            if (pin == null) {
                mListener.onFailure(TwitterOAuthWebView.this, Result.REQUEST_TOKEN_ERROR);
                return;
            }

            mIsPinLoaded = true;
            Log.i(TAG, "PIN : " + pin);

            try {
                AccessToken accessToken = mTwitter.getOAuthAccessToken(mRequestToken, pin.toString());
                mListener.onSuccess(TwitterOAuthWebView.this, accessToken);

            } catch (TwitterException e) {
                Log.e(TAG, e.toString());

                mListener.onFailure(TwitterOAuthWebView.this, Result.AUTHORIZATION_ERROR);
            }
        }

        private CharSequence parsePinNumber(String aHtml, String aPin) {
            if (TextUtils.isEmpty(aHtml) && TextUtils.isEmpty(aPin)) {
                return null;
            }

            /**
             * 2015.01.26 Jinsoo Jang
             * 기존 코드가 html 문자열(aHtml)을 순차로 쭉 읽으면서 7자리 숫자가 발견되면 바로 리턴하는 형태로 구현되어 있어서
             * ID에 7자리 숫자가 포함되어 있으면 그 값을 사용하는 버그가 발견됨. ( 워낙 오래된 코드라서 아마도 html 형태가 바뀌어 문제가 발생한 것으로 추측 됨. )
             * 그래서 pin number 문자열(aPin)에서 숫자 7자리로 된 단어를 찾아 사용하는 로직으로 변경함.
             */
            Pattern pattern = Pattern.compile("\\b\\d{7}\\b");
            Matcher matcher = pattern.matcher(aPin);
            matcher.find();

            String pinNumber = null;
            try {
                pinNumber = matcher.group();
            } catch (IllegalStateException ex) {
                Log.d(TAG, ex.toString());
                Log.d(TAG, "exctption occured!!. parsePinNumber(). aHtml : " + aHtml + ", aPin : " + aPin);
            }

            if (!TextUtils.isEmpty(pinNumber)) {
                return pinNumber;
            } else {
                return null;
            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            Log.d(TAG, "onPageStarted : " + url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.d(TAG, "shouldOverrideUrlLoading : " + url);
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    public interface Listener {
        /**
         * Called when the application has been authorized by the user
         * and got an access token successfully.
         *
         * @param view
         * @param accessToken
         */
        void onSuccess(TwitterOAuthWebView view, AccessToken accessToken);


        /**
         * Called when the OAuth process was not completed successfully.
         *
         * @param view
         * @param result
         */
        void onFailure(TwitterOAuthWebView view, Result result);
    }

    public enum Result {
        /**
         * The application has been authorized by the user and
         * got an access token successfully.
         */
        SUCCESS,


        /**
         * Twitter OAuth process was cancelled. This result code
         * is generated when the internal {@link android.os.AsyncTask}
         * subclass was cancelled for some reasons.
         */
        CANCELLATION,


        /**
         * Twitter OAuth process was not even started due to
         * failure of getting a request token. The pair of
         * consumer key and consumer secret was wrong or some
         * kind of network error occurred.
         */
        REQUEST_TOKEN_ERROR,


        /**
         * The application has not been authorized by the user,
         * or a network error occurred during the OAuth handshake.
         */
        AUTHORIZATION_ERROR,


        /**
         * The application has been authorized by the user but
         * failed to get an access token.
         */
        ACCESS_TOKEN_ERROR
    }
}
