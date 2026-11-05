package com.serviceplus.authorization.service.implementation;

import java.time.Duration;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisServerCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.authorization.ExceptionHandler.SPRuntimeError;
import com.serviceplus.authorization.dto.UserSessionObject;
import com.serviceplus.authorization.service.RedisService;
import static com.serviceplus.authorization.utility.Utility.stringToEntity;
import static com.serviceplus.authorization.utility.Utility.entityToString;
import static com.serviceplus.authorization.utility.ApplicationConstants.REDIS_KEY_EXIPRATION_DURATION_IN_MINUTES;

@Service
@Primary
public class RedisServiceImpl implements RedisService{

	@Autowired
	private RedisTemplate<String, String> template;

    @Value("${auth.session.time}")
    private Integer authSessionTime;
	
	@Override
	public Object fetchFromRedisCache(String uuid,Object obj) {

		try {
			String json = template.opsForValue().get(uuid);
			
			//Dump data
			//RedisConnection connection = template.getConnectionFactory().getConnection();
			//connection.save();    
			//Dump data */
			
			//RedisServerCommands serverCommands = connection.serverCommands();
			//serverCommands.save();
			
			Object map = stringToEntity(json,obj.getClass());
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void addToRedisCache(UserSessionObject user,String uuid) {
		String finalJson;
		try {
			finalJson = entityToString(user);
			template.opsForValue().set(uuid, finalJson,Duration.ofMinutes(authSessionTime));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String fetchFromNonActivatedUsersList(String value) {
		String returnValue = "";
		try {
			List<String> listValues = template.opsForList().range("NonActivatedUsers", 0, -1);
			if(listValues.contains(value)) {
				returnValue = value; 
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("RDERROR -- 002" , HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return returnValue;
	}

	@Override
	public void removeFromList(String listName,String value) {
		try {
			template.opsForList().remove(listName, 0, value);
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("RDERROR -- 003" , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public void revokeLoginKey(String key) {
		try {
			template.opsForValue().getAndDelete(key);
		}
		catch(Exception e) {
			e.printStackTrace();
			//throw new SPRuntimeError("RDERROR -- 004" , HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@Override
	public void updateCheckTimeAndPasskey(String uuid,String passkey) {
		try {
			UserSessionObject data = (UserSessionObject) fetchFromRedisCache(uuid,new UserSessionObject());
			if(data != null) {
			  data.setLastCheck(new Date());
			  data.setCsrfToken(passkey);
			  String json = entityToString(data);
			  template.opsForValue().set(uuid,json);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
