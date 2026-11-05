package com.serviceplus.authorization.service;

import java.util.List;

import com.serviceplus.authorization.dto.UserSessionObject;

public interface RBAC {

	boolean checkRole(String endpoint, List<UserSessionObject.Roles> roles, Boolean active);
	
	boolean checkCaptchaRequired(String endpoint,String propertyName);
}
