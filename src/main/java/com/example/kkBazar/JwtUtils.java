package com.example.kkBazar;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.example.kkBazar.entity.admin.AdminLogin;
import com.example.kkBazar.entity.admin.User;
@Component
public class JwtUtils {

private static final String SECRET = "TE7koozHpSsNbW2P3COvvWC7umIzVqOIO/6RNLmWVl4=";
private static final long EXPIRATION_TIME = 864_000_000; 

public static String generateToken(AdminLogin admin) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, admin.getEmail());
}

public static String generateUserToken(User user) {
    Map<String, Object> claims = new HashMap<>();
    return createToken(claims, user.getEmailId());
}
public static Date getExpirationDate(String token) {
    return extractExpiration(token);
}

private static String createToken(Map<String, Object> claims, String subject) {
    Date now = new Date();
    Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

    return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, SECRET)
            .compact();
}

public static boolean validateToken(String token, AdminLogin admin) {
    final String email = extractUsername(token);
    return (email.equals(admin.getEmail()) && !isTokenExpired(token));
}
public static boolean validateUserToken(String token, User user) {
    final String email = extractUsername(token);
    return (email.equals(user.getEmailId()) && !isTokenExpired(token));
}

public static String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
}

private static Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
}

private static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
    return claimsResolver.apply(claims);
}

private static boolean isTokenExpired(String token) {
    final Date expiration = extractExpiration(token);
    return expiration.before(new Date());
}
}





//	///////////////////////////////////////////////
//	  private static final String SECRET = "TE7koozHpSsNbW2P3COvvWC7umIzVqOIO/6RNLmWVl4=";
//
//	    public String generateToken(AdminLogin admin) {
//	        Map<String, Object> claims = new HashMap<>();
//	        Date expirationDate = getExpirationDate("21-11-2023");
//	        return createToken(claims, admin.getEmail(), expirationDate);
//	    }
//
//	    public String generateUserToken(User user) {
//	        Map<String, Object> claims = new HashMap<>();
//	        Date expirationDate = getExpirationDate("19-11-2023");
//	        return createToken(claims, user.getEmailId(), expirationDate);
//	    }
//
//	    public boolean validateToken(String token, AdminLogin admin) {
//	        final String email = extractUsername(token);
//	        return (email.equals(admin.getEmail()) && !isTokenExpired(token));
//	    }
//
//	    public static boolean validateUserToken(String token, User user) {
//	        final String email = extractUsername(token);
//	        return (email.equals(user.getEmailId()) && !isTokenExpired(token));
//	    }
//
//	    public static Date extractUsername(String token) {
//	        return extractClaim(token, Claims::getSubject);
//	    }
//
//	    public static Date getExpirationDate(String formattedDate) {
//	        return yourDateParsingLogic(formattedDate);
//	    }
//
//	    private String createToken(Map<String, Object> claims, String subject, Date expirationDate) {
//	        Date now = new Date();
//
//	        return Jwts.builder()
//	                .setClaims(claims)
//	                .setSubject(subject)
//	                .setIssuedAt(now)
//	                .setExpiration(expirationDate)
//	                .signWith(SignatureAlgorithm.HS256, SECRET)
//	                .compact();
//	    }
//
//	    private static Date extractClaim(String token, Function<Claims, ?> claimsResolver) {
//	        final Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
//	        return (Date) claimsResolver.apply(claims);
//	    }
//
//	    private static boolean isTokenExpired(String token) {
//	        final Date expiration = extractClaim(token, Claims::getExpiration);
//	        return expiration.before(new Date());
//	    }
//
//	    private static Date yourDateParsingLogic(String formattedDate) {
//	       
//	        return new Date();
//	    }
//
//		public boolean validateUserToken(String token, User user) {
//			// TODO Auto-generated method stub
//			return false;
//		}
//	}

	
	
	
	
	
	
	
	
	


    
	


