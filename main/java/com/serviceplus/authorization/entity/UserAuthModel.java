package com.serviceplus.authorization.entity;

import java.util.Date;

import com.serviceplus.authorization.utility.ApplicationConstants;

import jakarta.persistence.*;

@Entity
@Table(name = "user_auth_token", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class UserAuthModel {

    @EmbeddedId
    private UserAuthHistoryModel.Id id;

	@Column(name="user_id")
	private Long userId;
	
	@Column(name="c_date")
	private Date creationDate;
	
	@Column(name="last_check_time")
	private Date lastCheckTime;
	
	@Column(name = "sign_no")
	private String signNo;	
	
	@Column(name = "state_id")
	private Integer stateId;
	
	@Column(name = "user_identifier")
	private String userIdentifier;
	
	@Column(name = "email_id")
	private String email;
	
	@Column(name = "tenant_id")
	private String tenantId;

    @Column(name = "active")
    private Boolean active;

    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastCheckTime() {
		return lastCheckTime;
	}

	public void setLastCheckTime(Date lastCheckTime) {
		this.lastCheckTime = lastCheckTime;
	}

	public String getSignNo() {
		return signNo;
	}

	public void setSignNo(String signNo) {
		this.signNo = signNo;
	}

	public Integer getStateId() {
		return stateId;
	}

	public void setStateId(Integer stateId) {
		this.stateId = stateId;
	}

	public String getUserIdentifier() {
		return userIdentifier;
	}

	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public UserAuthHistoryModel.Id getId() {
        return id;
    }

    public void setId(UserAuthHistoryModel.Id id) {
        this.id = id;
    }
}
