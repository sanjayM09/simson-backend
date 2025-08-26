package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.OrderReturn;

public interface OrderReturnRepository extends JpaRepository<OrderReturn, Long> {

	@Query(value = "SELECT u.user_id, u.user_name, r.order_return_id, r.accepted,"
			+ " r.reason_for_return, r.rejected, r.return_status, r.return_date as date, r.order_item_list_id,"
			+ " o.product_list_id, o.quantity, o.total_price" + " FROM user AS u"
			+ " JOIN order_return AS r ON r.user_id = u.user_id"
			+ " JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
			+ " ORDER BY r.order_return_id DESC", nativeQuery = true)
	List<Map<String, Object>> getUserReturnDetails();

	@Query(value = " SELECT u.user_id, u.user_name, r.order_return_id, r.reason_for_return, r.accepted, "
			+ " r.rejected, r.return_status, r.return_date as date, r.order_item_list_id, "
			+ " o.product_list_id, o.quantity, o.total_price " + " FROM user AS u "
			+ " JOIN order_return AS r ON r.user_id = u.user_id "
			+ " JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id "
			+ " WHERE u.user_id = :userId " + " ORDER BY r.order_return_id DESC", nativeQuery = true)
	List<Map<String, Object>> getUserReturnDetailsByUserId(Long userId);

	@Query(value = " SELECT u.user_id as userId, u.user_name as userName, r.order_return_id as orderReturnId, r.accepted,"
			+ "	       r.reason_for_return as reasonForReturn, r.rejected, r.return_status as returnStatus,r.return_date as date, r.order_item_list_id as orderItemListId,"
			+ "	         o.product_list_id as productListId, o.quantity, o.total_price as totalPrice,p.product_name as productName"
			+ "	         FROM user AS u" + "	         JOIN order_return AS r ON r.user_id = u.user_id"
			+ "	         JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
			+ "	         join product_list as pl on pl.product_list_id=o.product_list_id"
			+ "             join product as p on p.product_id=pl.product_id", nativeQuery = true)
	List<Map<String, Object>> getOrderReturnDetails();

	@Query(value = " select o.order_return_id, o.accepted,o.return_date as date,o.return_status,o.reason_for_return,u.user_id,"
			+ " u.user_name,u.mobile_number,u.alternate_mobile_number,p.product_id,p.product_name,"
			+ " pl.product_list_id,ol.quantity,ol.total_price,pi.product_images_id"
			+ " from order_return as o"
			+ " join user as u on u.user_id=o.user_id"
			+ " join order_item_list as ol on ol.order_item_list_id=o.order_item_list_id"
			+ " join product_list as pl on pl.product_list_id=ol.product_list_id"
			+ " join product as p on p.product_id=pl.product_id"
			+ " join product_images as pi on pi.product_id=p.product_id"
			+ " where o.accepted=false and o.rejected=false", nativeQuery = true)
	List<Map<String, Object>> getReturnDetails();

	@Query(value = "SELECT o.order_return_id as orderReturnId, o.return_date, o.order_item_list_id as orderItemListId, o.reason_for_return as reasonForReturn, o.return_status as returnStatus, "
			+ "u.user_id as userId, u.user_name as userName " + "FROM order_return AS o "
			+ "JOIN user AS u ON u.user_id = o.user_id "
			+ "WHERE o.order_item_list_id = :order_item_list_id", nativeQuery = true)
	List<Map<String, Object>> getReturnStatusById(@Param("order_item_list_id") Long orderItemListId);

	   @Query(value = "SELECT o.order_return_id as orderReturnId, o.return_date, o.order_item_list_id as orderItemListId, o.reason_for_return as reasonForReturn, o.return_status as returnStatus, "
			   + " u.user_id as userId, u.user_name as userName "
			   + " FROM order_return AS o "
			   + " JOIN user AS u ON u.user_id = o.user_id "
			   + " WHERE u.user_id = :user_id and o.order_item_list_id = :order_item_list_id", nativeQuery = true)
			   List<Map<String, Object>> getReturnStatusById(@Param("order_item_list_id") Long orderItemListId, @Param("user_id") Long userId);
	   
	   
		@Query(value = " SELECT u.user_id as userId, u.user_name as userName, r.order_return_id as orderReturnId, r.accepted,"
				+ "				       r.reason_for_return as reasonForReturn, r.rejected, r.return_status as returnStatus, r.return_date as returnDate, r.order_item_list_id as orderItemListId,"
				+ "			         o.product_list_id as productListId, o.quantity, o.total_price as totalPrice,p.product_name as productName, pv.product_varient_images_id as productVarientImagesId"
				+ "				         FROM user AS u	    "
				+ "            JOIN order_return AS r ON r.user_id = u.user_id"
				+ "			         JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
				+ "			         join product_list as pl on pl.product_list_id=o.product_list_id"
				+ "			             join product as p on p.product_id=pl.product_id"
				+ "                         join product_varient_images as pv on pv.product_list_id = pl.product_list_id"
				+ "                         WHERE u.user_id = :userId and r.accepted = true ", nativeQuery = true)
		List<Map<String, Object>> getUserReturnDetailsByUserIdList(Long userId);

		
		
		@Query(value = " SELECT u.user_id as userId, u.user_name as userName, r.order_return_id as orderReturnId, r.accepted,"
				+ "				       r.reason_for_return as reasonForReturn, r.rejected, r.return_status as returnStatus, r.return_date as returnDate, r.order_item_list_id as orderItemListId,"
				+ "			         o.product_list_id as productListId, o.quantity, o.total_price as totalPrice,p.product_name as productName, pv.product_images_id as productImagesId"
				+ "				         FROM user AS u	    "
				+ "            JOIN order_return AS r ON r.user_id = u.user_id"
				+ "			         JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
				+ "			         join product_list as pl on pl.product_list_id=o.product_list_id"
				+ "			             join product as p on p.product_id=pl.product_id"
				+ "                       join product_images as pv on pv.product_id = p.product_id"
				+ "                         WHERE u.user_id = :userId and o.return_pending = true ", nativeQuery = true)
		List<Map<String, Object>> getQualifications(long userId);
		
		@Query(value = " SELECT u.user_id as userId, u.user_name as userName, r.order_return_id as orderReturnId, r.accepted,"
				+ "								       r.reason_for_return as reasonForReturn, r.rejected, r.return_status as returnStatus, r.return_date as returnDate, r.order_item_list_id as orderItemListId,"
				+ "						         o.product_list_id as productListId, o.quantity, o.total_price as totalPrice,p.product_name as productName, pv.product_images_id as productImagesId,orf.pending as refundPending,"
				+ "orf.return_status as refundStatus,orf.order_refund_id as orderRefundId,orf.accepted as refundAccepted,orf.bank_id as bankId,orf.refund_date as refundDate"
				+ "								         FROM user AS u	    "
				+ "				           JOIN order_return AS r ON r.user_id = u.user_id"
				+ "							         JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
				+ "							         join product_list as pl on pl.product_list_id=o.product_list_id"
				+ "							             join product as p on p.product_id=pl.product_id"
				+ "				                        join product_images as pv on pv.product_id = p.product_id"
				+ "										left join order_refund as orf on  orf.order_item_list_id = r.order_item_list_id"
				+ "                         WHERE u.user_id = :userId and r.accepted = true ", nativeQuery = true)
		List<Map<String, Object>> getAllFamilyInformations(long userId);
		@Query(value = " SELECT u.user_id as userId, u.user_name as userName, r.order_return_id as orderReturnId, r.accepted,"
				+ "				       r.reason_for_return as reasonForReturn, r.rejected, r.return_status as returnStatus, r.return_date as returnDate, r.order_item_list_id as orderItemListId,"
				+ "			         o.product_list_id as productListId, o.quantity, o.total_price as totalPrice,p.product_name as productName, pv.product_images_id as productImagesId"
				+ "				         FROM user AS u	    "
				+ "            JOIN order_return AS r ON r.user_id = u.user_id"
				+ "			         JOIN order_item_list AS o ON o.order_item_list_id = r.order_item_list_id"
				+ "			         join product_list as pl on pl.product_list_id=o.product_list_id"
				+ "			             join product as p on p.product_id=pl.product_id"
				+ "                  join product_images as pv on pv.product_id = p.product_id"
				+ "                         WHERE u.user_id = :userId and r.rejected = true ", nativeQuery = true)
		List<Map<String, Object>> getrejectedQualifications(long userId);
	   
	   
}
