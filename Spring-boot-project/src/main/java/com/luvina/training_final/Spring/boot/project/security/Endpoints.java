package com.luvina.training_final.Spring.boot.project.security;

public class Endpoints {
    public static final String[] PUBLIC_POST_ENDPOINTS={
            "/user/register",
            "/user/login",

    };

    public static final String[] USER_POST_ENDPOINT={
            "/user/validate-otp",
    };

    public static final String[] USER_PUT_ENDPOINTS={
            "/user/active_account/*"
    };
}
