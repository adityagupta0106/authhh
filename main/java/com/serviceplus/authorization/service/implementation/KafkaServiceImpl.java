package com.serviceplus.authorization.service.implementation;

import com.serviceplus.authorization.entity.UserAuthHistoryModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.serviceplus.authorization.dto.UserSessionObject;
import com.serviceplus.authorization.entity.UserAuthModel;
import com.serviceplus.authorization.repository.UserAuthRepository;
import com.serviceplus.authorization.service.KafkaService;
import static com.serviceplus.authorization.utility.Utility.*;


@Service
public class KafkaServiceImpl implements KafkaService{

	private static final Logger KafkaLogger = LogManager.getLogger("KafkaLogger");
	
	@Autowired
	private UserAuthRepository userAuthRepository;
	
	@Override
	public void updateAuthMeta(String key, String message) {
		try {
			if(isEmpty(message)) 
				return;
			
			UserSessionObject authMeta = (UserSessionObject) stringToEntity(message,UserSessionObject.class);
			UserAuthModel uam = convertToAuthModel(key,authMeta);
			userAuthRepository.saveAndFlush(uam);
			KafkaLogger.info("Auth meta updated succefully");
		}
		catch(Exception e) {
			KafkaLogger.error("Error while updating auth meta {}",e.getMessage());
			e.printStackTrace();
		}
	}
	
	
	private UserAuthModel convertToAuthModel(String uuid,UserSessionObject userDetails) {
		UserAuthModel uam = new UserAuthModel();
        uam.setId(new UserAuthHistoryModel.Id());
		uam.setUserId(userDetails.getUserID());
		uam.setCreationDate(userDetails.getLastCheck());
		uam.getId().setUuid(uuid);
		uam.setLastCheckTime(userDetails.getLastCheck());
		uam.setSignNo(userDetails.getSignNo());
		uam.setStateId(userDetails.getClcId());
		uam.setUserIdentifier(userDetails.getUserIdentifier());
		uam.setEmail(userDetails.getEmailId());
        uam.setActive(userDetails.getActive());
        uam.setTenantId(userDetails.getTenantId());
		return uam;
	}
	 

}
