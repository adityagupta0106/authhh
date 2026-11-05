package com.serviceplus.authorization.service;

import com.serviceplus.authorization.dto.UserSessionObject;

public interface RedisService {

	Object fetchFromRedisCache(String uuid,Object obj);
	
	void addToRedisCache(UserSessionObject user,String uuid);
	
	String fetchFromNonActivatedUsersList(String value);

	void removeFromList(String listName,String value);

	void revokeLoginKey(String key);
	
	void updateCheckTimeAndPasskey(String uuid,String passkey);
}
