package com.serviceplus.authorization.service;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthorizationService {

	Map<String,Object> isValidToken(String tk,String uuid,String endpoint,String csrfToken,String captcha, HttpServletResponse srvResponse,HttpServletRequest request) ;
	String generateNewCSRFToken(HttpServletRequest request);
	String generateNewToken(String atk, Long userId);
}
