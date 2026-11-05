package com.serviceplus.authorization.controller;


import java.util.Map;

import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.serviceplus.authorization.service.AuthorizationService;
import com.serviceplus.authorization.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.serviceplus.authorization.utility.ApplicationConstants.AUTH_TOKEN_KEY;

@RestController
@RequestMapping("/b")
public class AuthorizationController {
	
	@Autowired
	private AuthorizationService authorizationService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping(value="checkToken")
	public Map<String,Object> checkToken(@RequestParam String uuid,@RequestParam String token,
												@RequestParam String csrfToken,@RequestParam String endpoint,@RequestParam(required=false) String captcha
															,HttpServletResponse response,HttpServletRequest request) {
		return authorizationService.isValidToken(token, uuid,endpoint,csrfToken,captcha,response,request);
	}
	
	@GetMapping(value="removeSession")
	public boolean removeSession(@RequestParam String atk,HttpServletResponse response,HttpServletRequest request) {
		return userService.removeUserSession(atk,response);
	}
	
	@GetMapping(value="createUserToken")
	public String generateNewToken(@RequestParam String atk,@RequestParam Long userId) {
        return authorizationService.generateNewToken(atk,userId);
	}
}
