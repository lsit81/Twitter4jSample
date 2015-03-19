package com.android.lsit.test.twitter4j.twitter;

/**
 * 에러 코드는 {@link #https://dev.twitter.com/overview/api/response-codes}에서 보시면 됩니다.
 * Created by SeungTaek.Lim on 2015. 3. 13..
 */
public enum TwitterErrorCode {
    /**
     * Your call could not be completed as dialed.
     */
    COULD_NOT_AUTHENTICATE_YOU(32),

    /**
     * Corresponds with an HTTP 404 - the specified resource was not found
     */
    PAGE_DOES_NOT_EXIST(34),

    /**
     * Corresponds with an HTTP 403 — the access token being used belongs to a suspended user and they can’t complete the action you’re trying to take
     */
    YOUR_ACCOUNT_IS_SUSPENDED_AND_IS_NOT_PERMITTED_TO_ACCESS(64),

    /**
     * Corresponds to a HTTP request to a retired v1-era URL.
     */
    THE_TWITTER_REST_API_V1_IS_NO_LONGER_ACTIVE(68),

    /**
     * The request limit for this resource has been reached for the current rate limit window
     */
    RATE_LIMIT_EXCEEDED(88),

    /**
     * The access token used in the request is incorrect or has expired. Used in API v1.1
     */
    INVALID_OR_EXPIRED_TOKEN(89),

    /**
     * Only SSL connections are allowed in the API, you should update your request to a secure connection. See how to connect using SSL
     */
    SSL_IS_REQUIRED(92),

    /**
     * Corresponds with an HTTP 503 - Twitter is temporarily over capacity.
     */
    OVER_CAPACITY(130),

    /**
     * Corresponds with an HTTP 500 - An unknown internal error occurred
     */
    INTERNAL_ERROR(131),

    /**
     * Corresponds with a HTTP 401 - it means that your oauth_timestamp is either ahead or behind our acceptable range
     */
    COULD_NOT_AUTHENTICATE(135),

    /**
     * Corresponds with HTTP 403 — thrown when a tweet cannot be posted due to the user having no allowance remaining to post.
     * Despite the text in the error message indicating that this error is only thrown when a daily limit is reached,
     * this error will be thrown whenever a posting limitation has been reached.
     * Posting allowances have roaming windows of time of unspecified duration
     */
    DAILY_STATUS_UPDATE_LIMIT(185),

    /**
     * The status text has been Tweeted already by the authenticated account
     */
    STATUS_IS_DUPLICATE(187),

    /**
     * Typically sent with 1.1 responses with HTTP code 400. The method requires authentication but it was not presented or was wholly invalid.
     */
    BAD_AUTHENTICATION_DATA(215),

    /**
     * Returned as a challenge in xAuth when the user has login verification enabled on their account
     * and needs to be directed to twitter.com to generate a temporary password.
     */
    USER_MUST_VERIFY_LOGIN(231);

    private final int mErrorCode;
    private TwitterErrorCode(int value) {
        mErrorCode = value;
    }

    public static TwitterErrorCode valueOf(int value) {
        for (TwitterErrorCode status : values()) {
            if (status.mErrorCode == value) {
                return status;
            }
        }

        return null;
    }
}
