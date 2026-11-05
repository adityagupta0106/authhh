package com.serviceplus.authorization.dto;

public class FetchRolesDto {

	private String userId;

	public String getUserName() {
		return userId;
	}

	public void setUserName(String userId) {
		this.userId = userId;
	}

	public FetchRolesDto(String userId) {
		super();
		this.userId = userId;
	}

	public FetchRolesDto() {
	}
}
