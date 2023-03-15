package hk.pipgamestudio.dailybibleverse.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.InvalidKeyException;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
	
	@Value("${jwt.secret.key}")
    private String jwtSecretKey;
	
	long expirationInMillis = 3600000; // token will expire in 1 hour

    public String generateToken(String email) {

        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        String result = null;
        
        try {
			result = Jwts.builder()
			        .claim("email", email)
			        .setIssuedAt(now)
			        .setExpiration(new Date(System.currentTimeMillis() + expirationInMillis))
			        .signWith(SignatureAlgorithm.HS256, jwtSecretKey)
			        .compact();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return result;
    }

    public String verifyToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(jwtSecretKey).parseClaimsJws(token);
            String email = claims.getBody().get("email", String.class);
            return email;
        } catch (Exception e) {
            return null;
        }
    }
}