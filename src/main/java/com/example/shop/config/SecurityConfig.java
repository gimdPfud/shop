package com.example.shop.config;

import jakarta.servlet.annotation.WebListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration/*환경설정클래스*/
@EnableMethodSecurity/*특정메소드에대한접근제한?*/
@EnableWebSecurity
@WebListener/*web.xml없이도리스너를등록?*/
public class SecurityConfig {
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http
                .authorizeHttpRequests(
                        /*url을 통해서 들어오는 인가,권한에 따른 접속 여부*/
                        (AuthorizeHttpRequests) -> AuthorizeHttpRequests
                                .requestMatchers("/", "/user/login/**", "/user/signUp", "/css/**", "/js/**").permitAll()/*해당 url은 모두 접속 가능*/
                                .requestMatchers("/admin/item/**").hasRole("ADMIN")
                        /*.anyrequest().autho어쩌고?*/
                                .anyRequest().permitAll()
                )
                /*csrf토큰 : 서버에서 생성되는 임의의 값으로
                 * 페이지 요청 시 항상 다른 값으로 생성된다.
                 * 토큰 : 요청을 식별하고 검증하는데 사용하는 특수한 문자열 또는 값
                 * 미지정시 csrf방어기능에 의한 접근 거부*/
                .csrf(csrf -> csrf.disable())

                /*로그인 : 인증*/
                .formLogin(
                        formLogin -> formLogin.loginPage("/user/login")
                                .defaultSuccessUrl("/")/*로그인 성공하면 여기로 이동*/
                                .usernameParameter("email")/*.failureUrl("/user/login")*/
                )

                /*로그아웃*/
                .logout( logout-> logout.logoutUrl("/user/logout")
                        .logoutSuccessUrl("/").invalidateHttpSession(true)/*로그아웃시 이동링크, 로그아웃url, 로그아웃시 세션초기화*/
                )

                /*권한 틀리면...*/
                .exceptionHandling(
                        a -> a.accessDeniedHandler(new CustomAccessDeniedHandler())
                )
        ;

                return http.build();

        /*로그인과 로그아웃 페이지 접속 시 각각 handler지정
        * 기본적으로 제공하는 기능과 다르게 직접 설정*/
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}