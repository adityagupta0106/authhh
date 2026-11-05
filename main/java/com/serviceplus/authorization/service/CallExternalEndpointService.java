package com.serviceplus.authorization.service;

import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface CallExternalEndpointService<T> {

	ResponseEntity<? extends Object> callExternalEndpoint(Class<?>  returnObj,HttpMethod httpMethod,Map<String,String> headers,Map<String,Object> params,String url,String bodyContent,MediaType contentType);
}
