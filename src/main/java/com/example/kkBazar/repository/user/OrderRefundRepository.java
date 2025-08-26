package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.user.OrderRefund;

public interface OrderRefundRepository extends JpaRepository<OrderRefund, Long> {
	
	
	
	
	@Query(value = " select ord.order_refund_id as orderRefundId ,ord.accepted,ord.order_item_list_id as orderItemListId,ord.refund_date as refundDate,ord.order_return_id as orderReturnId,ord.return_status as refundStatus,ord.rejected,ord.user_id as userId,ord.bank_id as bankId,\r\n"
			+ " ol.alert_quantity as alertQuantity,ol.buy_rate as buyRate,ol.date,ol.delivered_date as deliveredDate,ol.discount_amount as discountAmount,ol.discount_percentage as discountPercentage,ol.gst_tax_amount as gstTaxAmount,ol.gst,ol.mrp,ol.order_status as orderStatus,ol.product_list_id as productListId,\r\n"
			+ " ol.quantity,ol.reason,ol.sell_rate as sellRate,orn.reason_for_return as reasonForReturn,orn.return_status as returnStatus,orn.return_date as returnDate,u.email_id as emailId,u.gender,u.mobile_number as mobileNumber,u.user_name as userName,b.account_number as accountNumber,b.bank_address as bankAddress,\r\n"
			+ " b.bank_name as bankName,b.holder_name as holderName,b.ifsc_code as ifscCode,b.pan_number as panNumber,p.product_id as productId,p.product_name as productName,pi.product_images_id as productImagesId\r\n"
			+ " from order_refund as ord\r\n"
			+ " join order_item_list as ol on ol.order_item_list_id = ord.order_item_list_id\r\n"
			+ " join order_return as orn on orn.order_return_id = ord.order_return_id\r\n"
			+ " join product_list as pl on pl.product_list_id=ol.product_list_id\r\n"
			+ " join product as p on p.product_id=pl.product_id\r\n"
			+ " join product_images as pi on pi.product_id=p.product_id\r\n"
			+ " join user as u on u.user_id = ord.user_id\r\n"
			+ " join bank as b on b.bank_id = ord.bank_id\r\n"
			+ " ", nativeQuery = true)
	List<Map<String, Object>> getUserReturnDetailsByUserIdList();
	

}
