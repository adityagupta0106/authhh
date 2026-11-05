package com.serviceplus.authorization.service.implementation;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.serviceplus.authorization.service.CallExternalEndpointService;

@Service
@Primary
public class CallExternalEndpointServiceImpl<T> implements CallExternalEndpointService<T>{

	@Autowired
	private RestTemplate restTemplate;
	
	@Override
	public ResponseEntity<? extends Object> callExternalEndpoint(Class<?> returnObj,final HttpMethod httpMethod,
															Map<String,String> headers,Map<String,Object> params,String url,
															String bodyContent,final MediaType contentType) {
		try {
			
			if(headers == null) {
				headers = new HashMap<>();
			}	

			HttpHeaders headerParams = new HttpHeaders();
			headers.forEach((hk,hv) -> {
				headerParams.add(hk, hv);
			});
			
			headerParams.setContentType(contentType);
			HttpEntity<Object> requestEntity = new HttpEntity<Object>(bodyContent, headerParams);
			return restTemplate.exchange(url,httpMethod,requestEntity, returnObj,params);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}	

}
