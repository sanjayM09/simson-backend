package com.example.kkBazar.repository.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,pv.product_varient_images_id as productVarientImagesId,p.description,r.review_id as reviewId,r.star_rate as starRate,r.message,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,ua.user_address_id,o.payment_type,pl.return_count,pl.return_status,pl.return_type,ol.delivered_date as deliveredDate,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity,ua.street_address,ua.state,ua.postal_code,ua.city,ua.country,ua.address_type,ua.mobile_number,ua.status as addressStatus,ors.order_return_id,ors.return_cancelled "
			+ " from order_item as o" + " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join user_address as ua on ua.user_address_id = o.user_address_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_varient_images as pv on pv.product_list_id = pl.product_list_id"
			+ " left join review as r on r.product_list_id=ol.product_list_id	"
			+ " left join order_return as ors on ors.order_item_list_id =ol.order_item_list_id"
			+ " WHERE o.user_id = :user_id" + " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getOrderItemDetails(@Param("user_id") Long userId);

//	@Query(value = " SELECT u.user_id, u.user_name,u.mobile_number, o.order_item_id,o.date,ol.order_item_list_id,ol.cancelled,"
//			+ " ol.delivered,ol.order_status,ol.product_list_id, ol.quantity, p.product_name,"
//			+ " ol.total_price FROM user AS u JOIN order_item AS o ON o.user_id = u.user_id"
//			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id "
//			+ " join product_list as pl on pl.product_list_id=ol.product_list_id"
//			+ " join product as p on p.product_id=pl.product_id"
//			+ " ORDER BY  ol.order_item_list_id desc ", nativeQuery = true)
//	List<Map<String, Object>> getUserPurchaseDetails();

	@Query(value = "select ol.order_item_list_id as orderItemListId,ol.cancelled,ol.delivered,ol.date,ua.city,ua.country,ua.postal_code as postalCode,"
			+ "	ua.state,ua.street_address as stateAddress,ua.address_type as addressType,u.user_name as userName,u.mobile_number as mobileNumber,"
			+ " ol.order_status as orderStatus,ol.product_list_id as productListId,p.product_name as productName,ol.quantity,ol.total_price as totalPrice,pl.return_count,pl.return_status,pl.return_type,"
			+ "	ol.total_amount as totalAmount,ol.order_item_id as orderItemId,o.user_id as userId,pl.mrp,pv.product_varient_images_id as productVarientImagesId,p.description,"
			+ "	r.review_id as reviewId,r.star_rate as starRate" + " from order_item_list as ol"
			+ "	join order_item as o on o.order_item_id = ol.order_item_id" + "	join user as u on u.user_id = o.user_id"
			+ "	join user_address as ua on ua.user_id = u.user_id"
			+ "	join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ "	join product as p on p.product_id = pl.product_id"
			+ "	join product_varient_images as pv on pv.product_list_id=pl.product_list_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ "	where o.user_id =:user_id and ol.order_item_list_id=:order_item_list_id", nativeQuery = true)
	List<Map<String, Object>> getAllOrderItemListDetails(Long user_id, Long order_item_list_id);

	@Query(value = "SELECT u.user_id, u.user_name, o.order_item_id, o.date, ol.order_item_list_id, ol.cancelled, "
			+ "ol.delivered, ol.order_status, ol.product_list_id, ol.quantity, ol.total_price " + "FROM user AS u "
			+ "JOIN order_item AS o ON o.user_id = u.user_id "
			+ "JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id " + "WHERE u.user_id = :userId "
			+ "ORDER BY ol.order_item_list_id desc ", nativeQuery = true)
	List<Map<String, Object>> getUserPurchaseDetailsByUserId(Long userId);

	@Query(value = " SELECT 'totalOrders' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ " FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id UNION ALL"
			+ " SELECT 'totalOrdersCurrentDay' AS metric,COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE DATE(oi.date) = CURRENT_DATE UNION ALL"
			+ " SELECT 'totalOrdersCurrentMonth' AS metric,COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ " JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE)"
			+ " AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) UNION ALL SELECT 'totalOrdersCurrentYear' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ " FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " UNION ALL SELECT 'totalCustomers' AS metric, COUNT(u.user_id) AS value FROM user AS u UNION ALL SELECT"
			+ " 'totalDeliveredOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value FROM order_item oi JOIN order_item_list oii ON oi.order_item_id = oii.order_item_id"
			+ " WHERE oii.delivered = true UNION ALL SELECT 'totalCancelledOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value FROM"
			+ " order_item oi JOIN order_item_list oii ON oi.order_item_id = oii.order_item_id WHERE oii.cancelled = true UNION ALL"
			+ " SELECT 'currentDayIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value FROM order_item_list oi"
			+ " WHERE DATE(oi.date) = CURRENT_DATE AND oi.delivered = true UNION ALL SELECT 'currentMonthIncome' AS metric,COALESCE(SUM(oi.total_price), 0) AS value"
			+ " FROM order_item_list oi WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ " AND oi.delivered = true UNION ALL SELECT 'currentYearIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value FROM order_item_list oi"
			+ " WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true UNION ALL SELECT 'totalStock' AS metric,"
			+ " COALESCE(SUM(pl.quantity), 0) AS value FROM product_list pl", nativeQuery = true)
	List<Map<String, Object>> getDashboardPageDetails();

	@Query(value = " SELECT 'totalOrders' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ "	JOIN order_item oi ON oii.order_item_id = oi.order_item_id UNION ALL"
			+ "	SELECT 'totalOrdersCurrentDay' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ "	JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE DATE(oi.date) = CURRENT_DATE"
			+ "	UNION ALL SELECT 'totalOrdersCurrentMonth' AS metric, COUNT(DISTINCT oii.order_item_id) AS value"
			+ "	FROM order_item_list oii JOIN order_item oi ON oii.order_item_id = oi.order_item_id"
			+ "	WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE) AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ "	UNION ALL SELECT 'totalOrdersCurrentYear' AS metric, COUNT(DISTINCT oii.order_item_id) AS value FROM order_item_list oii"
			+ "	JOIN order_item oi ON oii.order_item_id = oi.order_item_id WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE)"
			+ "	UNION ALL SELECT 'totalCustomers' AS metric, COUNT(u.user_id) AS value FROM user AS u"
			+ " UNION ALL SELECT 'totalDeliveredOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value"
			+ "	FROM order_item oi JOIN order_item_list oii ON oi.order_item_id = oii.order_item_id"
			+ " WHERE oii.delivered = true UNION ALL SELECT 'totalCancelledOrders' AS metric, COUNT(DISTINCT oi.order_item_id) AS value"
			+ "	FROM order_item oi JOIN order_item_list oii ON oi.order_item_id = oii.order_item_id WHERE oii.cancelled = true", nativeQuery = true)
	List<Map<String, Object>> getDashboardDetails();

	@Query(value = " SELECT 'currentDayIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ "	FROM order_item_list oi WHERE DATE(oi.date) = CURRENT_DATE AND oi.delivered = true"
			+ "	UNION ALL SELECT 'currentMonthIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ "	FROM order_item_list oi WHERE EXTRACT(MONTH FROM oi.date) = EXTRACT(MONTH FROM CURRENT_DATE)"
			+ "	AND EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true"
			+ "	UNION ALL SELECT 'currentYearIncome' AS metric, COALESCE(SUM(oi.total_price), 0) AS value"
			+ "	FROM order_item_list oi WHERE EXTRACT(YEAR FROM oi.date) = EXTRACT(YEAR FROM CURRENT_DATE) AND oi.delivered = true"
			+ "	UNION ALL SELECT 'totalStock' AS metric, COALESCE(SUM(pl.quantity), 0) AS value FROM product_list pl", nativeQuery = true)
	List<Map<String, Object>> getIncomeDetails();

	@Query(value = " SELECT o.order_item_id, o.date, u.user_id,u.user_name,ol.order_item_list_id,ol.cancelled,ol.order_id,"
			+ " ol.delivered,ol.order_status,ol.quantity,ol.total_price,"
			+ " pl.product_list_id, pl.alert_quantity, pl.buy_rate, pl.discount_amount, pl.discount_percentage,"
			+ " pl.gst, pl.gst_tax_amount, pl.mrp, pl.sell_rate, pl.stock_in, p.description, pl.total_amount, pl.return_count,pl.return_status,pl.return_type,pl.quantity as product_quantity,"
			+ " pl.unit, p.product_id, p.product_name, pvi.product_varient_images_id,"
			+ " pvi.product_varient_image, pvi.product_varient_image_url, pv.product_varient_id,"
			+ " pv.varient_name, pv.varient_value" + " FROM user as u" + " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ " WHERE DATE(o.date) = CURRENT_DATE", nativeQuery = true)
	List<Map<String, Object>> findOrderListByCurrentDate();

	@Query(value = " SELECT o.order_item_id as orderItemId, o.date, u.user_id as userId,u.user_name as userName,ol.order_item_list_id as orderItemListId,ol.cancelled,pl.return_count,pl.return_status,pl.return_type,ol.order_id as orderId,"
			+ " ol.delivered,ol.order_status as orderStatus,ol.quantity,ol.total_price as totalPrice,"
			+ " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate, pl.discount_amount as discountmount, pl.discount_percentage as discountPercentage,"
			+ " pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, p.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
			+ " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,"
			+ " pv.product_varient_id as productVarientId,"
			+ " pv.varient_name as varientName , pv.varient_value as varientValue" + " FROM user as u"
			+ " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ "   WHERE monthname(o.date)=:month" + "   AND year(o.date)=:year", nativeQuery = true)
	List<Map<String, Object>> findOrderListByMonthYear(@Param("month") String month, @Param("year") String year);

	@Query(value = " SELECT o.order_item_id as orderItemId, o.date, u.user_id as userId,u.user_name as userName,ol.order_item_list_id as orderItemListId,ol.cancelled,pl.return_count,pl.return_status,pl.return_type,ol.order_id as orderId,"
			+ " ol.delivered,ol.order_status as orderStatus,ol.quantity,ol.total_price as totalPrice,"
			+ " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate, pl.discount_amount as discountmount, pl.discount_percentage as discountPercentage,"
			+ "  pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, p.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
			+ " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,"
			+ " pv.product_varient_id as productVarientId,"
			+ " pv.varient_name as varientName , pv.varient_value as varientValue" + " FROM user as u"
			+ " join order_item AS o on o.user_id=u.user_id"
			+ " JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ "  WHERE o.date BETWEEN :startDate AND :endDate", nativeQuery = true)
	List<Map<String, Object>> findOrderListBetweenDate(@Param("startDate") LocalDate startDate,
			@Param("endDate") LocalDate endDate);

	@Query(value = " SELECT o.order_item_id, o.total_amount as totalPrice, o.total_items, o.user_id, u.user_name,pl.return_count,pl.return_status,pl.return_type, "
			+ " oi.order_item_list_id, pl.product_list_id, pl.buy_rate, pl.sell_rate, pl.discount_amount, "
			+ " pl.discount_percentage, pl.alert_quantity, pl.gst, pl.gst_tax_amount, pl.mrp, pl.quantity, "
			+ " pl.total_amount, p.product_id, p.product_name, pl.unit,pv.varient_name, "
			+ " pv.varient_value, p.description as listDescription, pvi.product_varient_image, "
			+ " pvi.product_varient_image_url, pv.product_varient_id, pvi.product_varient_images_id, "
			+ " oi.quantity as orderItemQuantity, oi.date, oi.order_status " + " FROM order_item as o "
			+ " JOIN order_item_list as oi ON oi.order_item_id = o.order_item_id "
			+ " JOIN user as u ON u.user_id = o.user_id "
			+ " JOIN product_list as pl ON pl.product_list_id = oi.product_list_id "
			+ " JOIN product as p ON p.product_id = pl.product_id "
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id "
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id "
			+ " WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName, '%'), ' ', ''))) "
			+ " AND o.user_id = :userId", nativeQuery = true)
	List<Map<String, Object>> getOrderDetailsByProductName(@Param("userId") Long userId,
			@Param("productName") String productName);

	@Query(value = " SELECT YEAR(CURDATE()) AS currentyear,YEAR(CURDATE()) - 1 AS previousyear,"
			+ " SUBSTRING(MONTHNAME(o.date), 1, 3) AS month,SUM(CASE WHEN YEAR(CURDATE()) = YEAR(o.date) THEN 1 ELSE 0 END) AS currentcount,"
			+ " SUM(CASE WHEN YEAR(CURDATE()) - 1 = YEAR(o.date) THEN 1 ELSE 0 END) AS previouscount"
			+ " FROM order_item_list AS o" + " WHERE YEAR(o.date) IN (YEAR(CURDATE()), YEAR(CURDATE()) - 1)"
			+ " GROUP BY SUBSTRING(MONTHNAME(o.date), 1, 3)" + " ORDER BY MIN(o.date)", nativeQuery = true)
	List<Map<String, Object>> getOrderCounts();

	@Query(value = " SELECT o.order_item_id, o.user_id, u.user_name, oi.order_item_list_id, pl.product_list_id, pl.buy_rate,pl.return_count,pl.return_status,pl.return_type,ol.order_id, "
			+ " pl.sell_rate, pl.discount_amount, pl.discount_percentage, pl.alert_quantity, pl.gst, pl.gst_tax_amount, "
			+ " pl.mrp, pl.quantity, pl.total_amount,p.product_id,p.product_name, pl.unit, "
			+ " pi.product_images_id, pv.varient_name, pv.varient_value, p.description as listDescription, pv.product_varient_id, pvi.product_varient_images_id, "
			+ " oi.quantity as orderItemQuantity,oi.date, oi.order_status " + " FROM order_item as o "
			+ " JOIN order_item_list as oi ON oi.order_item_id=o.order_item_id "
			+ " JOIN user as u ON u.user_id=o.user_id "
			+ " JOIN product_list as pl ON pl.product_list_id=oi.product_list_id "
			+ " JOIN product as p ON p.product_id=pl.product_id "
			+ " JOIN product_images as pi ON pi.product_id=p.product_id "
			+ " JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id "
			+ " JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id", nativeQuery = true)
	List<Map<String, Object>> getOrderItemDetails();

	@Query(value = " SELECT year,totalOrders,deliveredCount,"
			+ " CAST(ROUND((deliveredCount / totalOrders) * 100, 0) AS UNSIGNED) AS deliveredPercentage,cancelledCount,"
			+ " CAST(ROUND((cancelledCount / totalOrders) * 100, 0) AS UNSIGNED) AS cancelledPercentage,"
			+ " CAST(ROUND((totalOrders / MAX(totalOrders) OVER ()) * 100, 0) AS UNSIGNED) AS totalOrderCountPercentage"
			+ " FROM(SELECT YEAR(o.date) AS year,COUNT(o.order_item_id) AS totalOrders,"
			+ " COUNT(CASE WHEN ol.order_status = 'delivered' THEN o.order_item_id END) AS deliveredCount,"
			+ " COUNT(CASE WHEN ol.order_status = 'cancelled' THEN o.order_item_id END) AS cancelledCount"
			+ " FROM order_item AS o JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ " GROUP BY year) AS subquery" + " ORDER BY year DESC", nativeQuery = true)
	List<Map<String, Object>> getOrderDetails();

	@Query(value = "SELECT u.user_id, u.user_name,u.mobile_number,p.product_id, o.order_item_id,o.date,ol.order_item_list_id,ol.cancelled,pl.return_count,pl.return_status,pl.return_type,ol.order_id, ol.delivered,ol.order_status,ol.url,ol.invoice_pdf,"
			+ " ol.confirmed,ol.product_list_id, ol.quantity, p.product_name,o.invoice_status,o.invoice_flag,ol.total_price,ol.total_amount,o.payment_type,o.payment_status,pi.product_images_id,COALESCE(s.orderTotalAmount, 0) AS orderTotalAmount"
			+ "	FROM user AS u" + "	JOIN order_item AS o ON o.user_id = u.user_id"
			+ "	JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ "	JOIN product_list AS pl ON pl.product_list_id = ol.product_list_id"
			+ "	JOIN product AS p ON p.product_id = pl.product_id"
			+ " join product_images as pi on pi.product_id =p.product_id"
			+ "	LEFT JOIN ( SELECT o.order_item_id, SUM(CASE WHEN ol.pending = true THEN ol.total_price ELSE 0 END) AS orderTotalAmount\r\n"
			+ "	FROM order_item AS o" + "	JOIN order_item_list AS ol ON ol.order_item_id = o.order_item_id"
			+ "	GROUP BY  o.order_item_id) AS s ON o.order_item_id = s.order_item_id"
			+ "	where ol.order_status != 'cancelled'" + "	ORDER BY ol.order_item_list_id DESC;", nativeQuery = true)
	List<Map<String, Object>> getUserPurchaseDetails();

	@Query(value = "select ol.product_list_id,p.product_name from order_item_list as ol"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " where pl.product_list_id = :id", nativeQuery = true)
	List<Map<String, Object>> getAllProductListDetails(Long id);

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,pl.return_count,pl.return_status,pl.return_type,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,p.description,r.review_id as reviewId,r.star_rate as starRate,pi.product_images_id,ud.user_address_id,ud.address_type,ud.city,ud.country,ud.mobile_number,ud.postal_code,ud.state,ud.street_address,ud.district,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,ol.delivered_date as deliveredDate,ors.return_cancelled as returnCancelled,pd.pdf_url as pdfBlob,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity,pd.url as pdf_url"
			+ " from order_item as o" + " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " left join pdf as pd on pd.order_item_id = o.order_item_id"
			+ " left join order_return as ors on ors.order_item_list_id =ol.order_item_list_id"
			+ " join user_address as ud on ud.user_address_id=o.user_address_id"
			+ " WHERE o.user_id = :userId and ol.delivered = true and ol.order_status='delivered'"
			+ " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getAllFamilyInformations(long userId);

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,pl.return_count,pl.return_status,pl.return_type,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,p.description,r.review_id as reviewId,r.star_rate as starRate,pi.product_images_id,ud.user_address_id,ud.address_type,ud.city,ud.country,ud.mobile_number,ud.postal_code,ud.state,ud.street_address,ud.district,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,ol.delivered_date as deliveredDate,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity,pd.url as pdf_url"
			+ " from order_item as o" 
			+ " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " left join pdf as pd on pd.order_item_id = o.order_item_id"
			+ " join user_address as ud on ud.user_address_id=o.user_address_id"
			+ " WHERE o.user_id = :userId and ol.confirmed = true and ol.order_status='confirmed'"
			+ " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getAllConfirmedInformations(long userId);

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,pl.return_count,pl.return_status,pl.return_type,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,p.description,r.review_id as reviewId,r.star_rate as starRate,pi.product_images_id,ud.user_address_id,ud.address_type,ud.city,ud.country,ud.mobile_number,ud.postal_code,ud.state,ud.street_address,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity" + " from order_item as o"
			+ " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join user_address as ud on ud.user_address_id=o.user_address_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " WHERE o.user_id = :userId and ol.pending =true and ol.order_status='pending'"
			+ " ORDER BY ol.order_item_id DESC" + "", nativeQuery = true)
	List<Map<String, Object>> getQualifications(long userId);

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,pl.return_count,pl.return_status,pl.return_type,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,p.description,r.review_id as reviewId,r.star_rate as starRate,pi.product_images_id,ud.user_address_id,ud.address_type,ud.city,ud.country,ud.mobile_number,ud.postal_code,ud.state,ud.street_address,ud.district,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity"
			+ " from order_item as o"
			+ " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " join user_address as ud on ud.user_address_id=o.user_address_id"			
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " WHERE o.user_id = :userId and ol.pending =true and ol.order_status='pending'"
			+ " or ol.confirmed =true and ol.order_status='confirmed'"
			+ " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getQualifications1(long userId);

	@Query(value = "select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,pl.return_count,pl.return_status,pl.return_type,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,ol.order_id,\r\n"
			+ "						ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,\r\n"
			+ "					 p.product_name as productName,pv.product_varient_images_id as productVarientImagesId,p.description,r.review_id as reviewId,r.star_rate as starRate,\r\n"
			+ "						 ol.gst,ol.mrp,pl.alert_quantity as alertQuantity, ol.discount_percentage as discountPercentage, ol.sell_rate as sellRate,pl.unit,\r\n"
			+ "						  ol.gst_tax_amount as gstTaxAmount, ol.buy_rate as buyRate,ol.quantity,u.user_name as userName,u.mobile_number as mobileNumber,ua.user_address_id as userAddressId,ua.address_type as addressType,ua.city,c.company_id,\r\n"
			+ "                          c.address as companyAddress,c.company_name,c.country as companyCountry,c.email as companyEmail,c.location,c.phone_number as companyPhoneNumber,c.pincode as companyPincode,c.state as companyState,\r\n"
			+ "			            ua.country,ua.postal_code as postalCode,ua.state,ua.street_address,pf.url as pdf_url\r\n"
			+ "					 from order_item as o\r\n"
			+ "						 join order_item_list as ol on ol.order_item_id = o.order_item_id\r\n"
			+ "						 join product_list as pl on pl.product_list_id = ol.product_list_id\r\n"
			+ "						 join product as p on p.product_id = pl.product_id\r\n"
			+ "						 join product_varient_images as pv on pv.product_list_id = pl.product_list_id\r\n"
			+ "			             join user as u on u.user_id=o.user_id\r\n"
			+ "			            join user_address as ua on ua.user_id= u.user_id\r\n"
			+ "						 left join review as r on r.product_list_id=pl.product_list_id\r\n"
			+ "                         join company as c on c.company_id=1\r\n"
			+ "			 left join pdf as pf on pf.order_item_id = o.order_item_id"
			+ "  WHERE o.user_id =:userId and o.order_item_id =:orderItemId and  ol.order_item_list_id=:orderItemListId and ua.status = true"
			+ " ORDER BY ol.order_item_id DESC" + "", nativeQuery = true)
	List<Map<String, Object>> getQualificationsOrderiteamList(long userId, long orderItemId, long orderItemListId);

	@Query(value = " select o.date,o.order_item_id as orderItemId,pl.return_count,pl.return_status,pl.return_type,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,pv.product_images_id as productImagesId,p.description,r.review_id as reviewId,r.star_rate as starRate,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity" + " from order_item as o"
			+ " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_images as pv on pv.product_id = p.product_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " WHERE o.user_id = :userId and ol.cancelled = true and ol.order_status = 'cancelled' "
			+ " ORDER BY ol.order_item_id DESC" + "", nativeQuery = true)
	List<Map<String, Object>> getQualifications2(long userId);

	@Query(value = " SELECT oi.* FROM order_item AS oi "
			+ " JOIN order_item_list AS oil ON oil.order_item_id = oi.order_item_id "
			+ " WHERE oi.invoice_flag = TRUE AND oi.invoice_status ='InvoiceCompleted' and oi.order_item_id = :id", nativeQuery = true)
	List<Map<String, Object>> getQualifications28888(@Param("id") long orderItemListId);

	@Query(value = " select o.date,o.order_item_id as orderItemId,o.total_items as totalItems,o.total_price as orderTotalPrice,o.user_id as userId, ol.order_item_list_id as orderItemListId,pl.return_count,pl.return_status,pl.return_type,ol.order_id,"
			+ " ol.order_status as orderStatus,pl.product_id as productId, pl.product_list_id as productListId,ol.total_amount as totalAmount,ol.total_price as totalPrice,"
			+ " p.product_name as productName,pv.product_varient_images_id as productVarientImagesId,p.description,r.review_id as reviewId,r.star_rate as starRate,"
			+ " pl.gst,pl.mrp,pl.alert_quantity as alertQuantity,pl.discount_percentage as discountPercentage,pl.sell_rate as sellRate,pl.unit,ol.delivered_date as deliveredDate,ors.return_cancelled as returnCancelled,"
			+ " pl.gst_tax_amount as gstTaxAmount,pl.buy_rate as buyRate,ol.quantity,pd.url as pdf_url"
			+ " from order_item as o" + " join order_item_list as ol on ol.order_item_id = o.order_item_id"
			+ " join product_list as pl on pl.product_list_id = ol.product_list_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join product_varient_images as pv on pv.product_list_id = pl.product_list_id"
			+ " left join review as r on r.product_list_id=pl.product_list_id"
			+ " left join pdf as pd on pd.order_item_id = o.order_item_id"
			+ " left join order_return as ors on ors.order_item_list_id =ol.order_item_list_id"
			+ " WHERE ol.order_item_list_id = :order_item_list_id and ol.delivered = true and ol.order_status='delivered'"
			+ " ORDER BY ol.order_item_id DESC", nativeQuery = true)
	List<Map<String, Object>> getAllFamilyInformations0000(long order_item_list_id);
}
