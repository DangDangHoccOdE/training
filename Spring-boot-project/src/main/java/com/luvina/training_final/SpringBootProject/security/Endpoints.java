package com.luvina.training_final.SpringBootProject.security;

public class Endpoints {
    public static final String[] PUBLIC_POST_ENDPOINTS={
            "/user/register",
            "/user/login",
            "/user/forget_password/*",
            "/v3/api-docs/",
            "/swagger-ui/**", "/swagger-ui.html"
    };

    public static final String[] PUBLIC_GET_ENDPOINTS={
            "/v3/api-docs/",
            "/swagger-ui/**", "/swagger-ui.html","/monitoring","/user/get_all"
    };


    public static final String[] PUBLIC_PUT_ENDPOINTS={
            "/user/change_password/*",
            "/user/active_account/*",
    };

    public static final String[] USER_POST_ENDPOINT={
            "/user/validate-otp",
            "/friend_ship/send_request",
            "/upload",
            "/post/create",
            "/comment/create"
    };

    public static final String[] USER_PUT_ENDPOINTS={
            "/friend_ship/accept/*",
            "/friend_ship/decline/*",
            "/user/update_profile/*",
            "/post/edit/*",
            "/comment/edit/*",
    };

    public static final String[] USER_GET_ENDPOINTS={
            "/upload",
    };

    public static final String[] USER_DELETE_ENDPOINTS={
            "/friend_ship/delete/*",
            "/post/delete/*",
            "/comment/delete/*",
    };
}
