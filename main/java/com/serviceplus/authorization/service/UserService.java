package com.serviceplus.authorization.service;

import com.serviceplus.authorization.dto.UserSessionObject;

import jakarta.servlet.http.HttpServletResponse;

public interface UserService {

	UserSessionObject fetchUserFromMainDB(String atk);
	
	boolean removeUserSession(String uuid,HttpServletResponse response);
	 
	void updateLoginTimeInDB(String uuid,String jwt, String passKey) ;
}
