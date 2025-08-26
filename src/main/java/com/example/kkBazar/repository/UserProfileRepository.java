package com.example.kkBazar.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.user.UserProfile;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

	@Query(value = " select u.user_id,u.user_name, up.user_profile_id,up.profile_url,up.user_profile from user as u "
			+ " join user_profile as up on up.user_id=u.user_id", nativeQuery = true)
	List<Map<String, Object>> getUserProfileDetails();

	@Query(value = "select u.user_id,u.user_name, up.user_profile_id,up.profile_url,up.user_profile from user as u "
			+ " join user_profile as up on up.user_id=u.user_id" + " where u.user_id= :user_id", nativeQuery = true)
	List<Map<String, Object>> getProfileDetailById(Long user_id);
	
}
