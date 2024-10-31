package com.hoanghaidang.social_network.security;

public class Endpoints {
    public static final String[] PUBLIC_POST_ENDPOINTS={
            "/api/user/register",
            "/api/user/login",
            "/api/user/forget_password",
            "/api/user/validate_otp",
            "/v3/api-docs/**",
            "/swagger-ui/**", "/swagger-ui.html",
            "/api/user/sendEmailActive"
    };

    public static final String[] PUBLIC_GET_ENDPOINTS={
            "/v3/api-docs/**",
            "/swagger-ui/**", "/swagger-ui.html",
            "/*",
            "/api/download",
            "/uploads/**",
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS={
            "/api/user/change_password",
            "/api/user/active_account",
    };

    public static final String[] USER_POST_ENDPOINT={
            "/api/friend_ship/send_request",
            "/api/post/create_post",
            "/api/comment/comment_post",
            "/api/like/like_post",
            "/api/like/like_comment",
            "/api/upload",
            "/api/user/refreshToken"
    };

    public static final String[] USER_PUT_ENDPOINTS={
            "/api/friend_ship/accept",
            "/api/friend_ship/decline",
            "/api/user/update_profile",
            "/api/post/edit/*",
            "/api/comment/edit/*",
    };

    public static final String[] USER_GET_ENDPOINTS={
            "/api/user/report",
            "/api/timeline"
    };

    public static final String[] USER_DELETE_ENDPOINTS={
            "/api/friend_ship/delete",
            "/api/post/delete/*",
            "/api/comment/delete/*",
            "/api/like/unlikePost/*",
            "/api/like/unlikeComment/*",
    };
}
