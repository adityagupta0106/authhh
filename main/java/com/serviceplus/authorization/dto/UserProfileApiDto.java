package com.serviceplus.authorization.dto;

public class UserProfileApiDto {

	private Data data;

	public static class Data {
		private Profile profile;
		private Integer status;
		private String token;

		public static class Profile {
			private String u_mobileNo;
			private String u_emailId;
			private Integer u_stateId;
			private String u_userName;
			private Integer ul_departmentId;
			private Integer ul_locationId;
			private String u_stateName;
			private String ul_locationName;
			private Integer u_designationId;
			private Integer ul_departmentLevelId;
			private String ul_departmentLevelName;
            private Integer ul_locationTypeId;
			public String getU_mobileNo() {
				return u_mobileNo;
			}
			public void setU_mobileNo(String u_mobileNo) {
				this.u_mobileNo = u_mobileNo;
			}
			public String getU_emailId() {
				return u_emailId;
			}
			public void setU_emailId(String u_emailId) {
				this.u_emailId = u_emailId;
			}
			public Integer getU_stateId() {
				return u_stateId;
			}
			public void setU_stateId(Integer u_stateId) {
				this.u_stateId = u_stateId;
			}
			public String getU_userName() {
				return u_userName;
			}
			public void setU_userName(String u_userName) {
				this.u_userName = u_userName;
			}
			public Integer getUl_departmentId() {
				return ul_departmentId;
			}
			public void setUl_departmentId(Integer ul_departmentId) {
				this.ul_departmentId = ul_departmentId;
			}
			public Integer getUl_locationId() {
				return ul_locationId;
			}
			public void setUl_locationId(Integer ul_locationId) {
				this.ul_locationId = ul_locationId;
			}
			public String getU_stateName() {
				return u_stateName;
			}
			public void setU_stateName(String u_stateName) {
				this.u_stateName = u_stateName;
			}
			public String getUl_locationName() {
				return ul_locationName;
			}
			public void setUl_locationName(String ul_locationName) {
				this.ul_locationName = ul_locationName;
			}
			public Integer getU_designationId() {
				return u_designationId;
			}
			public void setU_designationId(Integer u_designationId) {
				this.u_designationId = u_designationId;
			}
			public Integer getUl_departmentLevelId() {
				return ul_departmentLevelId;
			}

            public Integer getUl_locationTypeId() {
                return ul_locationTypeId;
            }

            public void setUl_locationTypeId(Integer ul_locationTypeId) {
                this.ul_locationTypeId = ul_locationTypeId;
            }

            public void setUl_departmentLevelId(Integer ul_departmentLevelId) {
				this.ul_departmentLevelId = ul_departmentLevelId;
			}
			public String getUl_departmentLevelName() {
				return ul_departmentLevelName;
			}
			public void setUl_departmentLevelName(String ul_departmentLevelName) {
				this.ul_departmentLevelName = ul_departmentLevelName;
			}
			@Override
			public String toString() {
				return "Profile [u_mobileNo=" + u_mobileNo + ", u_emailId=" + u_emailId + ", u_stateId=" + u_stateId
						+ ", u_userName=" + u_userName + ", ul_departmentId=" + ul_departmentId + ", ul_locationId="
						+ ul_locationId + ", u_stateName=" + u_stateName + ", ul_locationName=" + ul_locationName + "]";
			}
		}

		public Profile getProfile() {
			return profile;
		}

		public void setProfile(Profile profile) {
			this.profile = profile;
		}

		public Integer getStatus() {
			return status;
		}

		public void setStatus(Integer status) {
			this.status = status;
		}

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

				
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}
}
