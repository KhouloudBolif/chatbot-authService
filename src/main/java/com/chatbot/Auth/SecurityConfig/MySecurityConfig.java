package com.chatbot.Auth.SecurityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class MySecurityConfig   {

   /* @Bean
    UserDetailsService userDetailsService(){
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        UserDetails user  = User.withUsername("khouloud").password(passwordEncoder().encode("1234")).authorities("read").build();
        userDetailsManager.createUser(user);
        return userDetailsManager;
    }*/
   /* @Bean
     BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
      http.httpBasic();
      //http.formLogin();
      http.authorizeHttpRequests().anyRequest().authenticated();
        return http.build();
    }*/



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/login", "/api/users/**","/welcom","/api/auth/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
