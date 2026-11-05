package com.serviceplus.authorization.service.implementation;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.serviceplus.authorization.ExceptionHandler.SPRuntimeError;
import com.serviceplus.authorization.dto.UserSessionObject;
import com.serviceplus.authorization.service.AuthorizationService;
import com.serviceplus.authorization.service.JWTService;
import com.serviceplus.authorization.service.RBAC;
import com.serviceplus.authorization.service.RedisService;
import com.serviceplus.authorization.service.UserService;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.serviceplus.authorization.utility.Utility.*;
import io.jsonwebtoken.security.SignatureException;
import static com.serviceplus.authorization.utility.ApplicationConstants.*;
import static com.serviceplus.authorization.utility.KeyGenerator.generatePassKey;

@Service
@Primary
public class AuthorizationServiceImpl implements AuthorizationService{

	private static final Logger AuthorizationLogger = LogManager.getLogger("AuthorizationLogger");

	@Autowired
	private JWTService jwtService;

	@Value("${aesAuthKey}")
	private String aesAuthKey;

	@Value("${auth.session.time}")
	private Integer authSessionTime;

	@Autowired
	private RedisService redisService;

	@Autowired
	private UserService userService;

	@Value("${auth.cookie.https}")
	private String cookieHttpsOnly;

	@Value("${auth.cookie.sameSite}")
	private String cookieSameSite;

	@Value("${sync.db.in.minutes}")
	private Integer dbSyncInMinutes;

    @Value("${sync.cache.in.minutes}")
    private Integer cacheSyncInMinutes;

	@Autowired
	private RBAC roleAccessService;

/*
     - Redis stores UserSessionObject with lastCheck and CSRF token
     - Redis key uses sliding TTL (updated here once per minute) to keep session alive for active users
      (Expiration updates every time cache is updated so, session always available in cache)
     - DB updates (login time, lastCheck) are done asynchronously or every 10 minutes for logging
       This avoids per-request DB hits while keeping session state accurate and scalable.


     */

	@Override
	public Map<String,Object> isValidToken(String tk,String uuid,String endpoint,String csrfToken,String captcha,HttpServletResponse srvResponse,HttpServletRequest request) {

		AuthorizationLogger.info("Token validation started for session {} with csrf token {}",uuid,csrfToken);
		if (tk.contains("%2B")) {
			tk = tk.replace("%2B", "+");
		}
		if (tk.contains(" ")) {
			tk = tk.replaceAll(" ", "+");
		}
		tk = AESDecrypt(tk, aesAuthKey);

		UserSessionObject user = new UserSessionObject();
		Map<String,Object> map = new HashMap<>();
        String serverSid = "";
		try {

         Claims claims = jwtService.extractAllClaimsAllowExpired(tk);
         serverSid = (String) claims.get("sid");
         String serverUid = claims.getSubject();
         boolean tokenExpired = jwtService.isTokenExpiredClaim(claims);
         boolean captchaRequired= roleAccessService.checkCaptchaRequired(endpoint,"captcha.required.endpoints");
         boolean checkConfigurableEndpoint= roleAccessService.checkCaptchaRequired(endpoint,"captcha.required.configurable.endpoints");
         if(!SHA256(serverSid).equalsIgnoreCase(uuid)){
             userService.removeUserSession(serverSid, srvResponse);
             throw new SPRuntimeError("Please login again [AUTHERROR - 001]",HttpStatus.FORBIDDEN);
         }

        //REMOVE REDIS LOOKUP MAKE JWT STATELESS ?????
        user = fetchFromRedisCache(serverSid);
            AuthorizationLogger.info("User inf authorization {} ",entityToString(user));
        if(user == null || user.getUserID() == null) {
            AuthorizationLogger.info("Redis data not found for session {}",serverSid);
            userService.removeUserSession(serverSid, srvResponse);
            throw new SPRuntimeError("Please login again [AUTHERROR - 002]",HttpStatus.FORBIDDEN);
        }
        boolean userSessionCaptchaReq=user.getCaptchaRequired()!=null && user.getCaptchaRequired();
        String userSessionCaptcha=user.getCaptchaKey();
        boolean isValid = jwtService.isTokenValidUsingClaims(claims,uuid,user);

		 if(!csrfToken.equals(user.getCsrfToken())) {
			userService.removeUserSession(serverSid, srvResponse);
			throw new SPRuntimeError("Please login again [AUTHERROR - 003]",HttpStatus.FORBIDDEN);
		 }

		 boolean isAuthenticUser = checkUserIdentification(user.getUserIdentifier(),request);
		 if(!isAuthenticUser) {
			 AuthorizationLogger.info("UserIdentification failed for session {}",serverSid);
			 userService.removeUserSession(serverSid, srvResponse);
			 throw new SPRuntimeError("Please login again [AUTHERROR - 004]",HttpStatus.FORBIDDEN);
		 }
		 user.setAtk(serverSid);
		 user.setUserID(user.getUserID());
		 AuthorizationLogger.info("For session {} jwt is valid {}",serverSid,isValid);

		 if(isValid) {

			 AuthorizationLogger.info("For session {} role access check started",serverSid);
			 boolean hasAccess = roleAccessService.checkRole(endpoint, user.getRoles(),user.getActive());
			 isValid = hasAccess;
			 AuthorizationLogger.info("For session {} role access allowed  {}",serverSid,isValid);
			 if(hasAccess) {
				 updateCheckTimeInDBAndCache(serverSid,tk,false,user.getLastCheck(),user.getCsrfToken(),user);
				 map.put("user", userSessionMap(user));
			 }
		 }

         if(tokenExpired && isValid){
             throw new ExpiredJwtException(null,null,"Token Expired");
         }
         //Captcha Validation
         if(captchaRequired || (checkConfigurableEndpoint && userSessionCaptchaReq)) {
        	 if(captcha==null || !SHA256(userSessionCaptcha).equals(captcha)) {
        		 AuthorizationLogger.info("Human Verification failed for session {}",serverSid);
        		 throw new SPRuntimeError("Human Verification failed",HttpStatus.FORBIDDEN);
        	 }else {
        		 user.setCaptchaKey(null);
        		 redisService.addToRedisCache(user, serverSid);
        	 }
         }

         //CHECK TENANT

		 map.put("status", isValid);
		 map.putIfAbsent("user", new HashMap<>());

            AuthorizationLogger.info("Returning authorization user {}",map);
		 return map;
		}
		catch(ExpiredJwtException ex) {
			Date sessionDuration = new Date();
			Date lastCheckTime = user.getLastCheck() ;
			AuthorizationLogger.info("For session {} JWT token expired last hit time {}",serverSid,lastCheckTime);
			long time = TimeUnit.MILLISECONDS.toMinutes(sessionDuration.getTime() - lastCheckTime.getTime());
			if((int) time > authSessionTime) {
				AuthorizationLogger.info("For session {} JWT token expired",serverSid);
				userService.removeUserSession(serverSid, srvResponse);
				throw new SPRuntimeError("Please login again [AUTHERROR - 005]",HttpStatus.UNAUTHORIZED);
			}
			else {

				//Later use a refresh token to issue and validate the user and issue a new refresh token again.
				AuthorizationLogger.info("For session {} JWT token expired but with valid session issuing new JWT",serverSid);
				String token = updateAuthToken(serverSid,user);
				srvResponse.addCookie(createCookie(AUTH_TOKEN_KEY, token, true,
                        isEmpty(cookieHttpsOnly) || Boolean.parseBoolean(cookieHttpsOnly),
									    isEmpty(cookieSameSite) ? "strict" : cookieSameSite
											,"/"));
				map.put("status", true);
				map.putIfAbsent("user", userSessionMap(user));
				return map;
			}

		}
		catch(SignatureException ex) {
			ex.printStackTrace();
			userService.removeUserSession(serverSid, srvResponse);
			throw new SPRuntimeError("Please login again [AUTHERROR - 006]",HttpStatus.UNAUTHORIZED);
		}
		catch(Exception e) {
			e.printStackTrace();
			if(e instanceof SPRuntimeError) {
				throw e;
			}

			throw new SPRuntimeError("Something went wrong",HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	private String updateAuthToken(String uuid,UserSessionObject user) {
		String jwt = jwtService.generateToken(user,uuid);
		addToRedisCache(user,uuid);
		return AESEncrypt(jwt, aesAuthKey);
	}

	private UserSessionObject fetchFromRedisCache(String uuid) {
        return (UserSessionObject) redisService.fetchFromRedisCache(uuid, new UserSessionObject());
	}

	private void addToRedisCache(UserSessionObject user,String uuid) {
		redisService.addToRedisCache(user,uuid);
	}

    //In cache save the db update remaining time ,dbSyncInMinutes is used when user is idle for sometime
	private void updateCheckTimeInDBAndCache(String uuid,String jwt, boolean forceSave, Date lastCheck, String passKey,UserSessionObject user) {

        Integer dbSyncRemaining = user.getDbSyncRemainingTimeInMinutes();

		if(forceSave || shouldUpdateLastCheck(lastCheck,cacheSyncInMinutes) || dbSyncRemaining == 0) {
            AuthorizationLogger.info("Updating cache for session {} lastCheck was {}",uuid,lastCheck);
            user.setCsrfToken(passKey);
            int uTime = dbSyncRemaining == 0 ? dbSyncInMinutes : (user.getDbSyncRemainingTimeInMinutes() - cacheSyncInMinutes);
            if(!forceSave){
                user.setLastCheck(new Date());
                user.setDbSyncRemainingTimeInMinutes(uTime);
            }
            addToRedisCache(user,uuid);
		}

        if(dbSyncRemaining == 0 || shouldUpdateLastCheck(lastCheck,dbSyncInMinutes)){
            AuthorizationLogger.info("Updating DB for session {} lastCheck was {}",uuid,lastCheck);
            user.setDbSyncRemainingTimeInMinutes(dbSyncInMinutes);
            updateLoginTimeInDB(uuid,jwt,passKey);
        }
	}

	private void updateLoginTimeInDB(String uuid,String jwt, String passKey) {
		userService.updateLoginTimeInDB(uuid, jwt,passKey);
	}

    public String generateNewCSRFToken(HttpServletRequest request) {
        String csrfToken = generatePassKey();

        String forLogin = request.getHeader("for-login");

        if(isEmpty(forLogin) || !forLogin.equals("true")) {
            String authKey = "";
            Cookie[] cookies = request.getCookies();
            for(Cookie ck : cookies){
                if(ck.getName().equals(AUTH_TOKEN_KEY)){
                    authKey = ck.getValue();
                }
            }

            if (authKey.contains("%2B")) {
                authKey = authKey.replace("%2B", "+");
            }
            if (authKey.contains(" ")) {
                authKey = authKey.replaceAll(" ", "+");
            }
            authKey = AESDecrypt(authKey, aesAuthKey);

            Claims claims = jwtService.extractAllClaimsAllowExpired(authKey);
            String serverSid = (String) claims.get("sid");
            UserSessionObject user = fetchFromRedisCache(serverSid);
            updateCheckTimeInDBAndCache(serverSid,user.getJwt(),true,user.getLastCheck(),csrfToken,user);
        }

        return csrfToken;
    }

	@Override
	public String generateNewToken(String atk, Long userId) {
		UserSessionObject user = new UserSessionObject();
		user.setUserID(userId);
		return jwtService.generateToken(user,atk);
	}
}
