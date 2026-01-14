package com.example.GoSchool.security;

import com.example.GoSchool.constant.Role;
import com.example.GoSchool.service.CustomUserDetailsService;
import com.example.GoSchool.service.TokenBlacklistService;
import com.example.GoSchool.utils.JwtAuthFilter;
import com.example.GoSchool.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableWebSecurity
public class CorsConfig {



    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//
//        @Bean
//        public WebMvcConfigurer corsConfigurer() {
//            return new WebMvcConfigurer() {
//                @Override
//                public void addCorsMappings(CorsRegistry registry) {
//                    registry.addMapping("/**")
//                            .allowedOrigins("http://localhost:4200")
//                            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                            .allowedHeaders("*");
//                }
//            };
//        }


    @Configuration
    public class WebConfig implements WebMvcConfigurer {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            // Serve uploaded files publicly
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:uploads/");
            registry.addResourceHandler("/auth/images/**")
                    .addResourceLocations("file:uploads/"); // or wherever images are stored
        }
    }

    @Configuration
    @EnableWebSecurity
    public static class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {
            http
                    .cors(Customizer.withDefaults())
                    .csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth -> auth
                            // Public endpoints - specific patterns first
                                    // Public endpoints - specific patterns first
                                    .requestMatchers(
                                            "/uploads/**",
                                            "/auth/api/users/admin/register",
                                            "/auth/api/parents/register",
                                            "/auth/api/users/register",
                                            "/auth/api/users/login",
                                            "/auth/api/users/logout",
                                            "/auth/api/users/forgot-password",
                                            "/auth/api/users/reset-password",
                                            "/auth/api/users/change-password",
                                            "/auth/api/drivers/register",
                                            "/auth/api/parents/{parentId}/students",
                                            "/auth/api/payments/student/{studentId}",
                                            "/auth/api/parents/{parentUUID}/students/{studentId}",
                                            "/auth/api/drivers/me/{userUUID}",
                                            "/auth/api/drivers",
                                            "/auth/api/transport/apply",
                                            "auth/api/transport/applications/parent/{parentId}/route/{routeId}",
                                            "/auth/api/drivers/{province}/students",
                                            "/auth/api/users/admin/drivers/*/students",
                                            "/auth/api/drivers/driver/{driverId}/assign/student/{studentId}"

                                    ).permitAll()

                                    .requestMatchers(HttpMethod.GET, "/auth/api/parents/*/children").hasRole("ADMIN")
                                    // Driver sees students who applied
                                    .requestMatchers(HttpMethod.GET,
                                            "/auth/api/transport/*/students"
                                    ).hasAnyRole("DRIVER", "ADMIN")
                                    .requestMatchers(HttpMethod.GET,
                                    "/auth/api/transport/*/applications"
                                     ).hasAnyRole("DRIVER", "ADMIN")

                                   // Admin approves
                                    .requestMatchers(HttpMethod.POST,
                                            "/auth/api/transport/*/applications/*/approve"
                                    ).hasRole("ADMIN")

                                    // Admin rejects
                                    .requestMatchers(HttpMethod.POST,
                                            "/auth/api/transport/*/applications/*/reject"
                                    ).hasRole("ADMIN")

                            .requestMatchers(
                                    HttpMethod.GET,
                                    "/auth/api/driver/notifications",
                                    "/auth/api/driver/notifications/**"
                            ).hasRole("DRIVER")


                            // Parent endpoints - specific to general
                                    .requestMatchers("/auth/api/parents/me/students").authenticated()
                                    .requestMatchers("/auth/api/parents/profile").authenticated()
                                    //.requestMatchers("/auth/api/parents/**").hasRole("PARENT")
                                    .requestMatchers("/auth/api/parents/**").hasAnyRole("PARENT", "ADMIN")
                                    // Driver routes endpoints
                                    .requestMatchers("/auth/api/drivers/*/routes").hasAnyRole("PARENT", "DRIVER")
                                    .requestMatchers("/auth/api/drivers/*/routes/*").hasAnyRole("PARENT", "DRIVER")
                                    .requestMatchers("/auth/api/drivers/*/routes/**").hasAnyRole("PARENT", "DRIVER")

                                    .requestMatchers("/auth/api/drivers/me/routes").hasAnyRole("DRIVER", "PARENT")
                                    .requestMatchers("/auth/api/users/admin/**").hasRole("ADMIN")

                                    .requestMatchers("/auth/api/drivers/profile").authenticated()
                                    .requestMatchers("/auth/api/users/admin/profile").authenticated()

                                   // Payment endpoints - require authentication
                                    .requestMatchers("/auth/api/payments/upload").hasRole("PARENT")
                                    .requestMatchers("/auth/api/payments/**").authenticated()

                                     // Other authenticated endpoints
                                    .requestMatchers("/auth/profile").authenticated()

                                    // Role-based endpoints
                                    .requestMatchers("/ADMIN/**").hasRole("ADMIN")
                                    .requestMatchers("/DRIVER/**").hasAnyRole("DRIVER", "ADMIN")

                                    // Everything else requires authentication
                                    .anyRequest().authenticated()
                    )
                    // Skip JWT filter for public URLs
                    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                    .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

            return http.build();
        }
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authBuilder.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
        return authBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService(CustomUserDetailsService authService) {
        return authService;
    }
    @Bean
    public JwtAuthFilter jwtAuthenticationFilter(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        return new JwtAuthFilter(jwtUtil, tokenBlacklistService);
    }

    @Bean
    public JwtUtil jwtUtil() {
        return new JwtUtil();
    }

    @Bean
    public TokenBlacklistService tokenBlacklistService() {
        return new TokenBlacklistService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Makes a new object called CorsConfiguration.  All the CORS (Cross-Origin Resource Sharing) settings are stored in this object.
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("http://localhost:4200");
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }



}


