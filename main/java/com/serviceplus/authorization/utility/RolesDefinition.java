package com.serviceplus.authorization.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;

@Configuration
@PropertySource("classpath:roledefinition.properties")
public class RolesDefinition {

	@Autowired
    private Environment roleEnvironment;

	public Environment getRoleEnvironment() {
		return roleEnvironment;
	}

}
