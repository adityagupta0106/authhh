package com.serviceplus.authorization.utility;


import java.net.InetAddress;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.serviceplus.authorization.dto.UserSessionObject;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

public class Utility {
	
	public static SimpleDateFormat uniqueNumber = new SimpleDateFormat("ddMMyyyyhhmmss");
	
	public static String AESEncrypt(String content, String authKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(new byte[16]));
			byte[] encrypted = cipher.doFinal(content.getBytes("UTF-8"));
			String finalString = org.apache.commons.codec.binary.Base64.encodeBase64String(encrypted);
			
			return finalString;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String AESDecrypt(String content, String authKey) {
		try {
			SecretKeySpec skeySpec = new SecretKeySpec(authKey.getBytes("UTF-8"), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec,new IvParameterSpec(new byte[16]));
			byte[] original = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(content));

			return new String(original);

		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Cookie createCookie(String name,String Value,boolean httpflag,boolean secureflag,String sameSite,String path) {
		Cookie cookie = new Cookie(name, Value); 
		cookie.setSecure(secureflag);
		cookie.setAttribute("SameSite", sameSite); 
		cookie.setHttpOnly(httpflag);		
		cookie.setPath(path);
		return cookie;
	}
	
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}
	
	public static String generateRandomString(String randomString, Integer length) {

		StringBuffer randStr = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = getRandomNumber(randomString);
			char ch = randomString.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
	
	private static int getRandomNumber(String randomString) {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(randomString.length());
		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}
	
	public static String getUniqueNumber() {
		Date obCur = new Date();
		return uniqueNumber.format(obCur);
	}
	
	public static String getClientIpAddr(HttpServletRequest request) {
		String ip = "";
		if (request != null) {
			ip = request.getHeader("X-Forwarded-For");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			if (isEmpty(ip) || ip.equalsIgnoreCase("127.0.0.1")) {
				try {
					InetAddress ownIP = InetAddress.getLocalHost();
					ip = ownIP.getHostAddress();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return ip;
	}
	
	public static String SHA256(String plaintext)   {
	    MessageDigest md = null;
	    try {
	        md = MessageDigest.getInstance("SHA-256"); 
	        md.update(plaintext.getBytes("UTF-8")); 
	    } catch (Exception e) {
	        md=null;
	    }

	    StringBuffer ls_sb=new StringBuffer();
	    
	    if(md != null){
	    	 byte raw[] = md.digest(); 
	    	    for(int i=0;i<raw.length;i++)
	    	        ls_sb.append(char2hex(raw[i]));
	    }
	   
	    return ls_sb.toString(); 
	    }
	
		public static String char2hex(byte x)
	
		{
		    char arr[]={
		                  '0','1','2','3',
		                  '4','5','6','7',
		                  '8','9','A','B',
		                  'C','D','E','F'
		                };
	
		    char c[] = {arr[(x & 0xF0)>>4],arr[x & 0x0F]};
		    return (new String(c));
		}
		
		/**
		 * Fetch logged in user details
		 * @param request
		 * @return UserSessionObject
		 */
		public static UserSessionObject getUserSessionDetails(HttpServletRequest request) {
			String user = request.getHeader("USER-DETAILS");
			if(isEmpty(user))
				return null;
				
			return (UserSessionObject) stringToEntity(user,UserSessionObject.class);
		}
		
		public static String entityToString(Object data) {
			return new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create().toJson(data);
		}
		
		public static Map<String,String> getServicePlusInternalRequestAuth(){
			Map<String,String> headers = new HashMap<>();
			headers.put("_scall", "true");
			headers.put("Atk", ApplicationConstants.STATIC_AUTH_TOKEN);
			return headers;
		}
		
		public static Object stringToEntity(String data,Class<?> classs) {
			return new Gson().fromJson(data, classs);
		}
		
		public static boolean checkUserIdentification(String savedIdentifier,HttpServletRequest request) {
			String userIdentifier = createUserIdentifier(request);
			return true;
			//return userIdentifier.equals(savedIdentifier);
		}

		//Mobile network dynamic IP need to remove IP
		public static String createUserIdentifier(HttpServletRequest request) {
			String clientIP = getClientIpAddr(request);
			String userAgent = request.getHeader("User-Agent");		
			return SHA256(userAgent + clientIP);
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String,Object> userSessionMap(UserSessionObject user) {
			String entityToString = entityToString(user);
			return (Map<String, Object>) stringToEntity(entityToString,Map.class);
		}
		
		public static String fetchProfileApiBody(String userId,String params) {
			String body = "query { profile(id: \\\"".concat(userId.toString()).concat("\\\") { ").concat(params).concat(" }}");
			return "{\"query\":\"".concat(body).concat("\"}");
		}

        public static boolean shouldUpdateLastCheck(Date lastCheck,int syncDuration) {
            return Duration.between(lastCheck.toInstant(), Instant.now()).toMinutes() >= syncDuration;
        }

        public static long timeDifferenceInMinutes(Date start,Date end) {
            return Duration.between(start.toInstant(), end.toInstant()).toMinutes();
        }
}


