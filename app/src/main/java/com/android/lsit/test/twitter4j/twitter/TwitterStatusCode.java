package com.android.lsit.test.twitter4j.twitter;

/**
 * 에러 코드는 {@link #https://dev.twitter.com/overview/api/response-codes}에서 보시면 됩니다.
 * Created by SeungTaek.Lim on 2015. 3. 13..
 */
public enum TwitterStatusCode {
    /** ERROR HTTP Status Codes */
    SUCCESS(200),
    UNABLE_NETWORK_TO_CONNECT(-1),

    /**
     * There was no new data to return.
     */
    NOT_MODIFIED(304),

    /**
     * The request was invalid or cannot be otherwise served. An accompanying error message will explain further.
     * In API v1.1, requests without authentication are considered invalid and will yield this response.
     */
    BAD_REQUEST(400),

    /**
     * Authentication credentials({@link #https://dev.twitter.com/oauth}) were missing or incorrect.
     * Also returned in other circumstances, for example all calls to API v1 endpoints now return 401 (use API v1.1 instead).
     */
    UNAUTHORIZED(401),

    /**
     * he request is understood, but it has been refused or access is not allowed.
     * An accompanying error message will explain why.
     * This code is used when requests are being denied due to update limits.
     * {@link #https://support.twitter.com/articles/15364-about-twitter-limits-update-api-dm-and-following}
     */
    FORBIDDEN(403),

    /**
     * The URI requested is invalid or the resource requested, such as a user, does not exists.
     * Also returned when the requested format is not supported by the requested method.
     */
    NOT_FOUND(404),

    /**
     * Returned by the Search API when an invalid format is specified in the request.
     */
    NOT_ACCEPTABLE(406),

    /**
     * This resource is gone. Used to indicate that an API endpoint has been turned off.
     * For example: “The Twitter REST API v1 will soon stop functioning. Please migrate to API v1.1.”
     */
    GONE(410),

    /**
     * Returned by the version 1 Search and Trends APIs when you are being rate limited.
     * {@link #https://dev.twitter.com/rest/public/rate-limiting}
     */
    ENHANCE_YOUR_CALM(420),

    /**
     * Returned when an image uploaded to POST account / update_profile_banner is unable to be processed.
     */
    UNPROCESSABLE_ENTITY(422),

    /**
     * Returned in API v1.1 when a request cannot be served due to the application’s rate limit having been exhausted for the resource.
     * See Rate Limiting in API v1.1.
     */
    TOO_MANY_REQUESTS(429),

    /**
     * Something is broken. Please post to the developer forums so the Twitter team can investigate.
     */
    INTERNAL_SERVER_ERROR(500),

    /**
     * Twitter is down or being upgraded.
     */
    BAD_GATEWAY(502),

    /**
     * The Twitter servers are up, but overloaded with requests. Try again later
     */
    SERVICE_UNAVAILABLE(503),

    /**
     * The Twitter servers are up, but the request couldn’t be serviced due to some failure within our stack. Try again later
     */
    GATEWAY_TIMEOUT(504);


    private final int mStatusCode;
    private TwitterStatusCode(int value) {
        mStatusCode = value;
    }

    public static TwitterStatusCode valueOf(int value) {
        for (TwitterStatusCode status : values()) {
            if (status.mStatusCode == value) {
                return status;
            }
        }

        return null;
    }

    public int getStatusCode() {
        return mStatusCode;
    }
}
