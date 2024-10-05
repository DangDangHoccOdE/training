package com.luvina.training_final.Spring.boot.project.security;

import com.luvina.training_final.Spring.boot.project.service.IUserSecurityService;
import com.luvina.training_final.Spring.boot.project.service.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final UserSecurityService userSecurityService;
    @Autowired
    public SecurityConfiguration(CustomAccessDeniedHandler customAccessDeniedHandler, UserSecurityService userSecurityService) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
        this.userSecurityService = userSecurityService;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(IUserSecurityService iUserSecurityService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(iUserSecurityService);
        provider.setPasswordEncoder(passwordEncoder()); // Đảm bảo bạn đã định nghĩa PasswordEncoder
        return provider;
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(
                configurer -> configurer
                        .requestMatchers(HttpMethod.POST,Endpoints.PUBLIC_POST_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT,Endpoints.USER_PUT_ENDPOINTS).hasAnyRole("USER","ADMIN")
                        .requestMatchers(HttpMethod.POST,Endpoints.USER_POST_ENDPOINT).hasAnyRole("USER","ADMIN")
        )
                .exceptionHandling(e->e.accessDeniedHandler(customAccessDeniedHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(Customizer.withDefaults());

        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
