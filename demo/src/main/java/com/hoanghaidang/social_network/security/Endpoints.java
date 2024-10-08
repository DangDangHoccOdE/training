package com.hoanghaidang.social_network.security;

public class Endpoints {
    public static final String[] PUBLIC_POST_ENDPOINTS={
            "/api/user/register",
            "/api/user/login",
            "/api/user/forget_password/*",
            "/v3/api-docs/**",
            "/swagger-ui/**", "/swagger-ui.html"
    };

    public static final String[] PUBLIC_GET_ENDPOINTS={
            "/v3/api-docs/**",
            "/swagger-ui/**", "/swagger-ui.html"
    };


    public static final String[] PUBLIC_PUT_ENDPOINTS={
            "/api/user/change_password/*",
            "/api/user/active_account/*",
    };

    public static final String[] USER_POST_ENDPOINT={
            "/api/user/validate-otp",
            "/api/friend_ship/send_request",
            "/api/upload",
            "/api/post/create",
            "/api/comment/create"
    };

    public static final String[] USER_PUT_ENDPOINTS={
            "/api/friend_ship/accept/*",
            "/api/friend_ship/decline/*",
            "/api/user/update_profile/*",
            "/api/post/edit/*",
            "/api/comment/edit/*",
    };

    public static final String[] USER_GET_ENDPOINTS={
            "/api/upload",
    };

    public static final String[] USER_DELETE_ENDPOINTS={
            "/api/friend_ship/delete/*",
            "/api/post/delete/*",
            "/api/comment/delete/*",
    };
}
