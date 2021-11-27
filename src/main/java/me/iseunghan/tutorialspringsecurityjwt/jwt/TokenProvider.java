package me.iseunghan.tutorialspringsecurityjwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import me.iseunghan.tutorialspringsecurityjwt.account.Account;
import me.iseunghan.tutorialspringsecurityjwt.account.AccountAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 토큰 유효성 검증 및 생성
 */
@Component
public class TokenProvider implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(TokenProvider.class);
    private static final String AUTHORITIES_KEY = "auth";

    private final String secret;
    private final long tokenValidityInMilliseconds;

    private Key key;


    // Bean 생성, 주입될 때 application.yml에서 설정한 secret, 유효시간을 받아옵니다.
    public TokenProvider(@Value("${jwt.secret}") String secret,
                         @Value("${jwt.token-validity-in-seconds}") long tokenValidityInMilliseconds) {
        this.secret = secret;
        this.tokenValidityInMilliseconds = tokenValidityInMilliseconds * 1000;
    }

    @Override
    public void afterPropertiesSet() {
        // secret 값을 HMAC256으로 암호화 한 후 key값에 할당
        byte[] decode = Decoders.BASE64URL.decode(secret);
        this.key = Keys.hmacShaKeyFor(decode);
    }

    /**
     * 넘어온 authentication 객체를 이용하여 JWT를 생성합니다.
     */
    public String createToken(Authentication authentication) {
        AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
        Account account = accountAdapter.getAccount();

        String authorities = accountAdapter.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInMilliseconds);

        return JWT.create()
                .withSubject(authentication.getName())
                .withClaim("id", account.getId())
                .withClaim("username", account.getUsername())
                .withClaim("authorities", authorities)
                .withExpiresAt(validity)
                .sign(Algorithm.HMAC256(key.getEncoded()));
    }

    /**
     * 토큰의 유효성을 검사합니다.
     */
    public boolean verifyToken(String token) {
        try {
            DecodedJWT verify = JWT.require(Algorithm.HMAC256(key.getEncoded())).build().verify(token);
            return true;
        } catch (AlgorithmMismatchException e) {
            logger.info("잘못된 알고리즘 형식입니다.");
        } catch (ExpiredJwtException e) {
            logger.info("만료된 JWT 토큰입니다.");
        } catch (JWTVerificationException e) {
            logger.info("잘못된 JWT 서명입니다.");
        }
        return false;
    }

    /**
     * 유효한 token을 이용해 AuthenticationToken을 생성해줍니다.
     */
    public Authentication getAuthentication(String token) {
        String username = JWT.decode(token).getClaim("username").asString();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(JWT.decode(token).getClaim("authorities").asString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
}
