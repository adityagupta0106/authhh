package com.serviceplus.authorization.dto;

public class SessionManagerDto {

	private String atk;
	private String userId;
	private Boolean removeSession;
	
	public String getAtk() {
		return atk;
	}
	public void setAtk(String atk) {
		this.atk = atk;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public Boolean getRemoveSession() {
		return removeSession;
	}
	public void setRemoveSession(Boolean removeSession) {
		this.removeSession = removeSession;
	}
	
}
