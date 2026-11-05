package com.serviceplus.authorization.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.authorization.service.AuthorizationService;
import com.serviceplus.authorization.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.serviceplus.authorization.utility.ApplicationConstants.*;

@RestController
@RequestMapping("/a")
public class PublicApis {
	
	private static final Logger AuthorizationLogger = LogManager.getLogger("AuthorizationLogger");
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@PostMapping(value="logout")
	public boolean removeSession(HttpServletResponse response,HttpServletRequest request) {
		String atk = request.getHeader(AUTH_SESSION_HEADER);
		AuthorizationLogger.info("Remove session request received for session {} ",atk);
		return userService.removeUserSession(atk,response);
	}
	
	@GetMapping(value="security/csrf-token")
	public String antiForge(HttpServletResponse response,HttpServletRequest request) {
		return authorizationService.generateNewCSRFToken(request);
	}
}
