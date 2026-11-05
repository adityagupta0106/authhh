package com.serviceplus.authorization.entity;

import java.util.Date;

import com.serviceplus.authorization.utility.ApplicationConstants;

import jakarta.persistence.*;

@Entity
@Table(name = "user_auth_token_history", schema = ApplicationConstants.SP_SCHEMA_NAME)
public class UserAuthHistoryModel {

    @EmbeddedId
    private Id id;

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
	
	@Column(name = "tenant_id")
	private String tenantId;

    @Embeddable
    public static class Id{

        @Column(name="atk")
        private String uuid;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }
    }

	public UserAuthHistoryModel() {
		super();
	}
	
	public UserAuthHistoryModel(UserAuthModel uat) {
        Id id = new Id();
        id.setUuid(uat.getId().getUuid());
        this.id = id;
		this.userId = uat.getUserId();
		this.creationDate = uat.getCreationDate();
		this.lastCheckTime = uat.getLastCheckTime();
		this.signNo = uat.getSignNo();
		this.stateId = uat.getStateId();
		this.userIdentifier = uat.getUserIdentifier();
		this.tenantId = uat.getTenantId();
	}

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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public Id getId() {
        return id;
    }

    public void setId(Id id) {
        this.id = id;
    }
}
