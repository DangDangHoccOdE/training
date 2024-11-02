package com.hoanghaidang.social_network.security;

import com.hoanghaidang.social_network.filter.JwtAuthenticationFilter;
import com.hoanghaidang.social_network.service.IUserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {
    private final CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver handlerExceptionResolver;

    @Autowired
    public SecurityConfiguration(CustomBasicAuthenticationEntryPoint customBasicAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customBasicAuthenticationEntryPoint = customBasicAuthenticationEntryPoint;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public JwtAuthenticationFilter jwtFilter(){
        return new JwtAuthenticationFilter(handlerExceptionResolver);
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
                        .requestMatchers(HttpMethod.GET,Endpoints.PUBLIC_GET_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT,Endpoints.PUBLIC_PUT_ENDPOINTS).permitAll()
                        .requestMatchers(HttpMethod.PUT,Endpoints.USER_PUT_ENDPOINTS).authenticated()
                        .requestMatchers(HttpMethod.GET,Endpoints.USER_GET_ENDPOINTS).authenticated()
                        .requestMatchers(HttpMethod.POST,Endpoints.USER_POST_ENDPOINT).authenticated()
                        .requestMatchers(HttpMethod.DELETE,Endpoints.USER_DELETE_ENDPOINTS).authenticated()
        )
                .cors(cors->{
                    cors.configurationSource(request -> {
                        CorsConfiguration configuration = new CorsConfiguration();
                        configuration.addAllowedOrigin((Endpoints.front_end_host)); // Thêm đươờng dẫn Frontend
                        configuration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE"));
                        configuration.addAllowedHeader("*"); // permission sth can access
                        configuration.setAllowCredentials(true);
                        configuration.setMaxAge(3600L);

                        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                        source.registerCorsConfiguration("/**",configuration);
                        return configuration;
                    });
                })
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e->e.accessDeniedHandler(customAccessDeniedHandler))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(httpBasic->httpBasic.authenticationEntryPoint(customBasicAuthenticationEntryPoint));

        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
        return config.getAuthenticationManager();
    }
}
