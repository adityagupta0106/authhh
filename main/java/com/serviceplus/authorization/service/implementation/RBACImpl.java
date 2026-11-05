package com.serviceplus.authorization.service.implementation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.serviceplus.authorization.dto.UserSessionObject.Roles;
import com.serviceplus.authorization.service.RBAC;
import com.serviceplus.authorization.utility.RolesDefinition;

@Service
public class RBACImpl implements RBAC{
	
	private static final Logger AuthorizationLogger = LogManager.getLogger("AuthorizationLogger");
	
	@Autowired
	private RolesDefinition rolesDefinition;

	@Override
	public boolean checkRole(String endpoint, List<Roles> roles, Boolean active) {
		
		boolean hasAccess = false;
        Environment configuredRoles = rolesDefinition.getRoleEnvironment();
		
		AuthorizationLogger.info("Checking role access for endpoint {} for role {} active {}",endpoint,roles.toString(),active);

        if(active) {

            for (Roles role : roles) {
                Integer roleId = role.getRoleId();
                String endpoints = configuredRoles.getProperty(roleId.toString());
                hasAccess = endpoints.contains(endpoint);

                if (!hasAccess) {
                    endpoints = configuredRoles.getProperty("global.allowed.endpoints");
                    hasAccess = endpoints.contains(endpoint);
                }

                if (hasAccess) {
                    break;
                }
            }
        }
        else{
            String endpoints = configuredRoles.getProperty("inactive.user.allowed.endpoints");
            hasAccess = endpoints.contains(endpoint);
        }

		AuthorizationLogger.info("role access for endpoint {} -> {}",endpoint,hasAccess);
		
		return hasAccess;
	}

	@Override
	public boolean checkCaptchaRequired(String endpoint,String propertyName) {
		boolean captchaRequired=false;
		AuthorizationLogger.info("Checking propertyName: {} for endpoint {} ",propertyName,endpoint);
		Environment configuredRoles = rolesDefinition.getRoleEnvironment();
		String endpoints = configuredRoles.getProperty(propertyName);
		captchaRequired = endpoints.contains(endpoint);
		AuthorizationLogger.info("For propertyName: {} and endpoint {} , result {}",propertyName,endpoint,captchaRequired);
		return captchaRequired;
	}

	
}
