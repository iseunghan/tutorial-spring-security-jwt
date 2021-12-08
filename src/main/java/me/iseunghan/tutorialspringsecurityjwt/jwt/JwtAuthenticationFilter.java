package me.iseunghan.tutorialspringsecurityjwt.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.iseunghan.tutorialspringsecurityjwt.account.Account;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, ObjectMapper objectMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.objectMapper = objectMapper;
    }

    /**
     * POST "/login" 들어온 요청은 attemptAuthentication 메소드를 타게 된다.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if(!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication is not allow method : " + request.getMethod());
        }
        // username, password 추츌 (Form data 파싱)
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // username, password 추출 (body json 파싱)
        if(username == null || password == null) {
            try {
                Account account = objectMapper.readValue(request.getInputStream(), Account.class);
                username = account.getUsername();
                password = account.getPassword();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);    // 인증하기 위해 token 생성

        return authenticationManager.authenticate(token);   // authenticationManager에게 인증 책임을 떠넘긴다.
    }

    /**
     * authenticate 성공 시 실행
     * 사용자 인증에 성공했다면, JWT 토큰을 생성하여 응답 헤더에 담아줍니다.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        // TokenProvider 토큰 생성 요청
        String jwtToken = tokenProvider.createToken(authentication);
        // 응답 헤더에 토큰 담기
        response.addHeader("Authorization", "Bearer " + jwtToken);
        // 쿠키 생성 및 보안적용
        Cookie cookie = new Cookie("Authorization", jwtToken);

        // set HttpOnly
        cookie.setHttpOnly(true);
        cookie.setSecure(true);

        // expired in 30m
        cookie.setMaxAge(30 * 60);

        // add Cookie
        response.addCookie(cookie);
    }

    /**
     * authenticate 실패 시 실행
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
