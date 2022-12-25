package com.melody.melody.config;

import com.melody.melody.adapter.web.security.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(){
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .cors().disable()
                .csrf().ignoringAntMatchers("/h2-console/**").disable()
                .httpBasic().disable()
                .formLogin().disable()
                .logout().disable()

                .headers().frameOptions().disable()
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll()

                .antMatchers(HttpMethod.GET, "/users/search/**").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{userId}").hasRole("USER")
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.PATCH, "/users/{userId}/password").hasRole("USER")
                .antMatchers(HttpMethod.PATCH, "/users/{userId}").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/users").hasRole("USER")

                .antMatchers(HttpMethod.GET,"/users/{userId}/music").permitAll()
                .antMatchers(HttpMethod.GET, "/music/**").hasRole("USER")
                .antMatchers(HttpMethod.POST,"/music/**").hasRole("USER")

                .antMatchers(HttpMethod.GET, "/users/{userId}/posts").permitAll()
                .antMatchers(HttpMethod.GET, "/posts/**").permitAll()
                .antMatchers(HttpMethod.POST, "/posts").hasRole("USER")
                .antMatchers(HttpMethod.PATCH, "/posts/**").hasRole("USER")
                .antMatchers(HttpMethod.DELETE, "/posts/**").hasRole("USER")

                .antMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
                .and()

                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler)
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()

                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/h2-console");
    }
}
