package com.luvina.training_final.Spring.boot.project.security;

public class Endpoints {
    public static final String[] PUBLIC_POST_ENDPOINTS={
            "/user/register",
            "/user/login",
            "/user/forget_password/*"
    };

    public static final String[] PUBLIC_PUT_ENDPOINTS={
            "/user/change_password/*"
    };

    public static final String[] USER_POST_ENDPOINT={
            "/user/validate-otp",
            "/friend_ship/send_request"
    };

    public static final String[] USER_PUT_ENDPOINTS={
            "/user/active_account/*",
            "/friend_ship/accept/*",
            "/friend_ship/decline/*"
    };

    public static final String[] USER_DELETE_ENDPOINTS={
            "/friend_ship/delete/*"
    };
}
