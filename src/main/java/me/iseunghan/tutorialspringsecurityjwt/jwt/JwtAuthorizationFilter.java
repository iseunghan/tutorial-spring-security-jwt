package me.iseunghan.tutorialspringsecurityjwt.jwt;

import me.iseunghan.tutorialspringsecurityjwt.account.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenProvider tokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider) {
        super(authenticationManager);
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = extractToken(request);
        String requestURI = request.getRequestURI();

        // Token 값이 비어있지 않고, 유효성 검사까지 통과하면 실행
        if (StringUtils.hasText(token) && tokenProvider.verifyToken(token)) {
            // 인증 정보를 SecurityContext에 넣어줍니다. (인증완료)
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("[Security Context] : '{}' 인증 정보를 저장했습니다, URI : {}", authentication, requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다. URI : {}", requestURI);
        }
        doFilter(request, response, chain);
    }

    /**
     * Bearer Token 추출
     */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        // Bearer 형식 토큰이 존재하는지?
        if(StringUtils.hasText(header) && header.startsWith("Bearer")) {
            return header.replace("Bearer", "").trim();
        }
        return null;
    }
}
