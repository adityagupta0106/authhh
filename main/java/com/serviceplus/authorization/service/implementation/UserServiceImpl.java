package com.serviceplus.authorization.service.implementation;

import static com.serviceplus.authorization.utility.Utility.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.serviceplus.authorization.ExceptionHandler.SPRuntimeError;
import com.serviceplus.authorization.dto.FetchRoleResponseDto;
import com.serviceplus.authorization.dto.FetchRolesDto;
import com.serviceplus.authorization.dto.UserProfileApiDto;
import com.serviceplus.authorization.dto.UserSessionObject;
import com.serviceplus.authorization.dto.UserSessionObject.Roles;
import com.serviceplus.authorization.entity.UserAuthModel;
import com.serviceplus.authorization.repository.CustomQueryRepository;
import com.serviceplus.authorization.repository.UserAuthRepository;
import com.serviceplus.authorization.service.CallExternalEndpointService;
import com.serviceplus.authorization.service.RedisService;
import com.serviceplus.authorization.service.UserService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService{

	@Autowired
	private CallExternalEndpointService<?> callExternalEndpointService;
	
	@Value("${splus.usermanagement.service}")
	private String USER_MANAGEMENT_SERVICE;
	
	@Value("${aesAuthKey}")
	private String aesAuthKey;
	
	@Autowired
	private UserAuthRepository userAuthRepository;
	
	@Autowired
	private RedisService redisService;

	@Autowired
	private CustomQueryRepository customQueryRepository;
	
	/**
	 * If REDIS is down fetch user session detail from primary database
	 * @return UserSessionObject
	 */
	
	//FOR LATER USE MONGODB
    @Deprecated
	@Override
	public UserSessionObject fetchUserFromMainDB(String atk) {
		UserAuthModel uam = userAuthRepository.fetchFromMainDB(atk);
		UserSessionObject user = new UserSessionObject();
		if(uam == null) {
			throw new SPRuntimeError("Please login again [AUTHERROR - 006]",HttpStatus.FORBIDDEN); 
		}
		
		String encuserName = AESEncrypt(uam.getUserId().toString(), aesAuthKey);
		
		String url = USER_MANAGEMENT_SERVICE.concat("/a/roles");
		FetchRolesDto dto = new FetchRolesDto(encuserName);
		ResponseEntity<? extends Object> apiResponse = callExternalEndpointService.callExternalEndpoint(String.class,HttpMethod.POST,
											getServicePlusInternalRequestAuth(),new HashMap<>(),url,
											entityToString(dto),MediaType.APPLICATION_JSON);
		
		if(!apiResponse.getStatusCode().is2xxSuccessful()) {
			throw new SPRuntimeError("AUTHERROR [UM]",HttpStatus.FORBIDDEN); 
		}		

		FetchRoleResponseDto roleList = (FetchRoleResponseDto) stringToEntity((String) apiResponse.getBody(),FetchRoleResponseDto.class);		
		user.setRoles(roleList.getData().getRoles());
		
		String query = fetchProfileApiBody(encuserName,"u_userName u_emailId u_stateName  u_stateId   u_mobileNo  ul_departmentId  ul_locationId u_designationId ul_departmentLevelId ul_locationName ul_departmentLevelName ul_locationTypeId");
		url = USER_MANAGEMENT_SERVICE.concat("/g/gapi");
		apiResponse = callExternalEndpointService.callExternalEndpoint(String.class, 
														HttpMethod.POST, getServicePlusInternalRequestAuth(), new HashMap<>(), url, 
														query,MediaType.APPLICATION_JSON);
		
		UserProfileApiDto profileDto = (UserProfileApiDto)stringToEntity((String)apiResponse.getBody(),UserProfileApiDto.class);
		UserProfileApiDto.Data.Profile profile = profileDto.getData().getProfile();
		
//		user.setUserID(uam.getUserId());
//		user.setLastCheck(uam.getLastCheckTime());
//		user.setSignNo(uam.getSignNo());
//		user.setStateId(uam.getStateId());
//		user.setUserIdentifier(uam.getUserIdentifier());
//		user.setEmailId(uam.getEmail());
//		user.setTenantId(uam.getTenantId());
//
//		user.setDepartmentId(profile.getUl_departmentId());
//		user.setLocationId(profile.getUl_locationId());
//		user.setStateName(profile.getU_stateName());
//		user.setLocationName(profile.getUl_locationName());
//		user.setMobileNo(profile.getU_mobileNo());
//		user.setDesignationId(profile.getU_designationId());
//		user.setUserName(profile.getU_userName());
//		user.setDepartmentLevelId(profile.getUl_departmentLevelId());
//		user.setDepartmentLevelName(profile.getUl_departmentLevelName());
//        user.setLocationTypeId(profile.getUl_locationTypeId());
//        user.setActive(uam.getActive());
		
		return user;
	}

	@Override
	public boolean removeUserSession(String uuid,HttpServletResponse response) {
		try {
			redisService.revokeLoginKey(uuid);
			Cookie cookie = new Cookie("authKey", ""); 
			cookie.setHttpOnly(true);		
			cookie.setPath("/");
			cookie.setMaxAge(0);
			response.addCookie(cookie);
			return true;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new SPRuntimeError("ERR[S-01]",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	//@Async("authTokenExecutor")
	public void updateLoginTimeInDB(String uuid,String jwt,String passkey) {
		userAuthRepository.updateLastCheckDate(uuid,passkey);
	}

}
