package com.serviceplus.authorization.repository;

import com.serviceplus.authorization.entity.UserAuthHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.serviceplus.authorization.entity.UserAuthModel;

import jakarta.transaction.Transactional;

@Repository
public interface UserAuthRepository extends JpaRepository<UserAuthModel, UserAuthHistoryModel.Id>{

	@Query(value = "SELECT uam FROM UserAuthModel uam WHERE uam.id.uuid = ?1", nativeQuery = false)
    UserAuthModel fetchFromMainDB(String atk);

	@Query(value = "UPDATE UserAuthModel uam set uam.lastCheckTime = CURRENT_TIMESTAMP  WHERE uam.id.uuid = ?1", nativeQuery = false)
	@Transactional
	@Modifying
    void updateLastCheckDate(String uuid, String passkey);
}
