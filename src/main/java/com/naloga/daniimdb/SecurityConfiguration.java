package com.naloga.daniimdb;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Profile("test")
@Configuration
public class SecurityConfiguration {

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("sa")
                .password("password")
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    @Profile("test")
    public SecurityFilterChain securityFilterChainTest(HttpSecurity http) throws Exception {
        // [milosevic85], I noticed a lot of deprecated changes from spring 5.7 up to 6.1 (authorizeRequests and antMatchers are RequestMatchers), this bean is only active for the "test" profile
        http.authorizeRequests(authorizeRequests ->
                        authorizeRequests.anyRequest().permitAll())
                .httpBasic(withDefaults())
                .csrf(csrf -> csrf.disable());

        return http.build();
    }
}

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.authorizeRequests(authorizeRequests ->
//                        authorizeRequests
//                                .requestMatchers(HttpMethod.PUT, "/actors/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.PUT, "/movies/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.PUT, "/actors/**").hasRole("USER")
//                                .requestMatchers(HttpMethod.PUT, "/movies/**").hasRole("USER")
//                                .requestMatchers(HttpMethod.POST, "/actors/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.POST, "/movies/**").hasRole("ADMIN")
//                                .requestMatchers(HttpMethod.POST, "/actors/**").hasRole("USER")
//                                .requestMatchers(HttpMethod.POST, "/movies/**").hasRole("USER")
//                                .anyRequest().permitAll()
//                )
//                .httpBasic(withDefaults())
//                .csrf(csrf -> csrf.disable());
//
//        return http.build();
//    }