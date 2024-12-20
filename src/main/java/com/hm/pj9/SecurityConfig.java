package com.hm.pj9;

//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
//import org.springframework.security.web.SecurityFilterChain;

//@Configuration
//@EnableWebSecurity
public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeHttpRequests(authorize -> authorize
//                        .requestMatchers("/", "/home", "/login").permitAll() // 누구나 접근 가능
//                        .anyRequest().authenticated() // 그 외의 요청은 인증 필요
//                )
//                .formLogin(form -> form
//                        .loginPage("/login") // 로그인 페이지 설정
//                        .defaultSuccessUrl("http://localhost:8416/", true) // 로그인 성공 후 리다이렉트
//                        .permitAll() // 로그인 페이지는 누구나 접근 가능
//                )
//                .logout(LogoutConfigurer::permitAll // 로그아웃 페이지는 누구나 접근 가능
//                )
//                .sessionManagement(session -> session
//                        .maximumSessions(1) // 동시 세션 수 제한
//                        .maxSessionsPreventsLogin(true) // 이미 로그인한 경우 추가 로그인 방지
//                );
//
//        return http.build();
//    }
}