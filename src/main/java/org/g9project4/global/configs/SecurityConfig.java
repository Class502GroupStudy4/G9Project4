package org.g9project4.global.configs;


import org.g9project4.member.services.LoginFailureHandler;
import org.g9project4.member.services.LoginSuccessHandler;
import org.g9project4.member.services.MemberAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        /* 로그인, 로그아웃 S */
        http.formLogin(f -> {
            f.loginPage("/member/login")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .successHandler(new LoginSuccessHandler())
                    .failureHandler(new LoginFailureHandler());
//                    .successForwardUrl("/")
//                    .failureUrl("/member/login?error=true");
        });

        http.logout(logout -> {
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
//                    .logoutSuccessHandler()
                    .logoutSuccessUrl("/member/login");
        });
        /* 로그인, 로그아웃 E */
        /* 인가(접근 통제) 설정 S*/
        http.authorizeRequests(authorizeRequests -> {
            authorizeRequests.requestMatchers("/mypage/**").authenticated()//회원 전용
                    .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")//예외 발생
                    .anyRequest().permitAll();
        });
        http.exceptionHandling(c -> {
            c.authenticationEntryPoint(new MemberAuthenticationEntryPoint())//예외 가
                    .accessDeniedHandler((req, res, e) -> {
                        res.sendError(HttpStatus.UNAUTHORIZED.value());
                    });
        });
        /* 인가(접근 통제) 설정 E*/
        //iframe 자원 출처를 같은 서버 자원으로 한정
        http.headers(c -> c.frameOptions(f -> f.sameOrigin()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
