package com.example.kkBazar.repository.admin;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.admin.User;

public interface UserRepository extends JpaRepository<User, Long> {

	User findByEmailId(String email);

	@Query(value = "select a.* ,r.role_name" + " from admin_login as a" + " join role  as r on r.role_id = a.role_id"
			+ " where a.id=:id", nativeQuery = true)
	List<Map<String, Object>> getAllAdminDetailsById(Long id);

	@Query(value = "select u.user_id as userId,u.email_id as emailId,u.password,u.user_name as userName from user as u"
			+ " where u.user_id =:user_id", nativeQuery = true)
	List<Map<String, Object>> getDetailsById(Long user_id);

	@Query(value = "SELECT u.user_id, u.alternate_mobile_number, u.date_of_birth, u.email_id, u.gender, u.mobile_number, "
			+ " u.user_name, a.user_address_id, a.city, a.country, a.postal_code, a.state, a.street_address "
			+ " left   FROM user AS u " + " JOIN user_address AS a ON u.user_id = a.user_id "
			+ " WHERE u.user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getUserDetails(Long user_id);

	
	@Query(value = "SELECT u.user_id, u.alternate_mobile_number, u.date_of_birth, u.email_id, u.gender, u.mobile_number, u.date, u.user_name,"
	        + " a.user_address_id, a.address_type, a.city, a.country, a.postal_code, a.state, a.street_address"
	        + " FROM user AS u"
	        + " LEFT JOIN user_address AS a ON a.user_id = u.user_id", nativeQuery = true)
	List<Map<String, Object>> getAllUserDetails();


	
	@Query(value = "SELECT u.user_id as userId, a.address_type as addressType,a.user_address_id AS userAddressId, a.city, a.country, a.postal_code AS postalCode, a.state, a.street_address AS streetAddress "
	        + "FROM user AS u "
	        + " left JOIN user_address AS a ON u.user_id = a.user_id "
	        + "WHERE u.user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getUserAddressDetails(@Param("user_id") Long userId);

	
	@Query(value = "SELECT user_id as userId, alternate_mobile_number as alternateMobileNumber, email_id as emailId, date_of_birth as dateOfBirth, gender, mobile_number as mobileNumber, user_name as userName"
	        + " FROM user "
	        + " WHERE user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getUserById(@Param("user_id") Long userId);

@Query(value=" SELECT YEAR(u.date) AS year, COUNT(u.user_id) AS customerCount"
		+ " FROM user AS u"
		+ " WHERE YEAR(u.date) BETWEEN YEAR(CURRENT_DATE) - 4 AND YEAR(CURRENT_DATE)"
		+ " GROUP BY YEAR(u.date) ORDER BY year DESC", nativeQuery = true)
		List<Map<String, Object>>getYearWishCustomerCount();


@Query(value=" "
		+ "select u.user_id as userId,u.date,u.alternate_mobile_number as alternateMobileNumber,u.confirm_password as confirmPassword,u.email_id as emailId,u.gender,u.mobile_number as mobileNumber,u.password,u.user_name as userName,u.date_of_birth as dateOfBirth from  user u"
		+ " where u.user_id =:userId", nativeQuery = true)
Map<String, Object> UserDetailes(Long userId);
	
}
