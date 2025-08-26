package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.UserAddress;

public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {


	@Query(value = "SELECT u.user_id as userId,u.user_name as userName,u.mobile_number as mobileNumber,a.address_type as addressType, a.user_address_id AS userAddressId, a.city, a.country, a.postal_code AS postalCode, a.state, a.street_address AS streetAddress "
			+ " FROM user AS u " + "JOIN user_address AS a ON u.user_id = a.user_id "
			+ " WHERE u.user_id = :user_id and a.address_type=:address_type", nativeQuery = true)
	List<Map<String, Object>> getUserAddressTypeDetails(@Param("user_id") Long userId,
			@Param("address_type") String addressType);

	List<UserAddress> findByUserId(long userId);
	
	@Query(value = "select ud.user_address_id as userAddressId,ud.address_type as addressType,ud.city,ud.district,ud.country,ud.postal_code as postalCode,ud.state,ud.street_address as streetAddress,ud.user_id as userId,ud.status,\r\n"
			+ " u.date,u.alternate_mobile_number as alternateMobileNumber,u.confirm_password as confirmPassword,u.email_id as emailId,u.gender,ud.mobile_number as mobileNumber,u.password,u.user_name as userName\r\n"
			+ " from user_address as ud\r\n"
			+ " join user as u on u.user_id=ud.user_id\r\n"
			+ " where ud.user_id=:user_id", nativeQuery = true)
	List<Map<String, Object>> UserDetailes(Long user_id);

}
