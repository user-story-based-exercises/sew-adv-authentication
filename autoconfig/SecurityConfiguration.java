package server.yousong.autoconfig;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.*;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
// import org.springframework.session.ExpiringSession;
// import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.header.writers.StaticHeadersWriter;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.SessionRepositoryFilter;
import server.yousong.repositories.UserInfoService;

import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;

import static javax.servlet.http.HttpServletResponse.SC_NO_CONTENT;
import static javax.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Configuration
@EnableSpringConfigured
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfiguration<T extends UserInfo<ID>, ID extends Serializable> {

    /** Base path for REST API requests */
    @Value("${spring.data.rest.base-path:}")
    private String restBasePath;

    @Value("${sew.logout:/logout}")
    private String logout;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Resources do not require authentication, only method calls can be authorized
        // http.authorizeRequests()
        //     .anyRequest().permitAll();

        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers("/api/**").permitAll()
            .antMatchers("/login*").permitAll()
            .antMatchers("/logout*").permitAll()
            .antMatchers("/api/auth/**").permitAll()
            .anyRequest()
            .authenticated()
            .and()
            .formLogin()
                .successHandler((request, response, authentication) -> {
                    request
                        .getRequestDispatcher(request.getContextPath() + restBasePath + Me.PATH)
                        .forward(request, response);
                })
                .failureHandler((request, response, exception) -> {
                    response.resetBuffer();
                    response.setStatus(SC_UNAUTHORIZED);
                });

        http
            .logout()
            .logoutUrl(logout)
            .clearAuthentication(true)
            .invalidateHttpSession(true)
            .logoutSuccessHandler((request, response, authentication) ->
            { response.resetBuffer(); response.setStatus(SC_NO_CONTENT); });

        http.exceptionHandling()
            .authenticationEntryPoint(new Http403ForbiddenEntryPoint());

        http
            .headers()
            .addHeaderWriter(new StaticHeadersWriter("x-auth-token", ""));

        return http.build();
    }
}