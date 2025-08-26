package com.example.kkBazar.repository.companyProfile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.companyProfile.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


	@Query(value=" select i.invoice_id as invoiceId,i.company_id as CompanyId,i.order_item_id as orderItemId,c.address as companyAddress,"
			+ " c.company_name as companyName,c.country as companyCountry,"
			+ " c.email as companyEmail,pl.gst,pl.gst_tax_amount as gstTaxAmount,c.location as companyLocation,c.phone_number as companyPhoneNumber,"
			+ " c.pincode as companyPincode ,c.state as companyState,o.total_amount as totalAmount,o.total_items as totalItems,"
			+ " ol.order_item_list_id as orderItemListId,ol.product_list_id as productListId,ol.quantity,"
			+ " ol.total_price as totalPrice,ol.date as orderDate,"
			+ " o.user_id as userId, u.user_name as userName,u.email_id as userMail,"
			+ " u.mobile_number as userMobileNumber,a.user_address_id as userAddressId,"
			+ " a.address_type as addressType,a.city as userCity,a.country as userCountry,a.postal_code as userPostalCode,"
			+ " a.state as userState,a.street_address as userStreetAddress,p.product_id as productId,p.product_name as productName"
			+ " from invoice as i"
			+ " join company as c on c.company_id=i.company_id"
			+ " join order_item as o on o.order_item_id=i.order_item_id"
			+ " join order_item_list as ol on ol.order_item_id=o.order_item_id"
			+ " join product_list as pl on pl.product_list_id=ol.product_list_id"
			+ " join product as p on p.product_id=pl.product_id"
			+ " join user as u on u.user_id=o.user_id"
			+ " join user_address as a on a.user_id=u.user_id"
			+"  where ol.order_item_list_id = :order_item_list_id", nativeQuery = true)
	Map<String, Object> getInvoiceDetails(@Param("order_item_list_id") Long orderItemListId);	


	@Query(value = " SELECT \r\n"
			+ "    i.invoice_id AS invoiceId,\r\n"
			+ "    c.company_id AS CompanyId,\r\n"
			+ "    o.order_item_id AS orderItemId,\r\n"
			+ "    c.address AS companyAddress,\r\n"
			+ "    c.company_name AS companyName,\r\n"
			+ "    c.country AS companyCountry,\r\n"
			+ "    o.date,\r\n"
			+ "    c.email AS companyEmail,\r\n"
			+ "    pl.gst,\r\n"
			+ "    pl.gst_tax_amount AS gstTaxAmount,\r\n"
			+ "    c.location AS companyLocation,\r\n"
			+ "    c.phone_number AS companyPhoneNumber,\r\n"
			+ "    c.pincode AS companyPincode,\r\n"
			+ "    c.state AS companyState,\r\n"
			+ "    o.total_amount AS totalAmount,\r\n"
			+ "    o.total_items AS totalItems,\r\n"
			+ "    ol.order_item_list_id AS orderItemListId,\r\n"
			+ "    pl.product_list_id AS productListId,\r\n"
			+ "    ol.quantity,\r\n"
			+ "    ol.total_amount,\r\n"
			+ "    ol.total_price AS totalPrice,\r\n"
			+ "    ol.date AS orderDate,\r\n"
			+ "    o.user_id AS userId,\r\n"
			+ "    u.user_name AS userName,\r\n"
			+ "    u.email_id AS userMail,\r\n"
			+ "    u.mobile_number AS userMobileNumber,\r\n"
			+ "    a.user_address_id AS userAddressId,\r\n"
			+ "    a.address_type AS addressType,\r\n"
			+ "    a.city AS userCity,a.district,	"
			+ "    a.country AS userCountry,\r\n"
			+ "    a.postal_code AS userPostalCode,\r\n"
			+ "    a.state AS userState,\r\n"
			+ "    a.street_address AS userStreetAddress,\r\n"
			+ "    p.product_id AS productId,\r\n"
			+ "    p.product_name AS productName,\r\n"
			+ "    COALESCE(s.orderTotalAmount, 0) AS orderTotalAmount\r\n"
			+ " FROM \r\n"
			+ "    invoice AS i\r\n"
			+ " JOIN \r\n"
			+ "    company AS c ON c.company_id = i.company_id\r\n"
			+ " JOIN \r\n"
			+ "    order_item AS o ON o.order_item_id = i.order_item_id\r\n"
			+ " JOIN \r\n"
			+ "    order_item_list AS ol ON ol.order_item_id = o.order_item_id\r\n"
			+ " JOIN \r\n"
			+ "    product_list AS pl ON pl.product_list_id = ol.product_list_id\r\n"
			+ " JOIN \r\n"
			+ "    product AS p ON p.product_id = pl.product_id\r\n"
			+ " JOIN \r\n"
			+ "    user AS u ON u.user_id = o.user_id\r\n"
			+ " JOIN \r\n"
			+ "    user_address AS a ON a.user_address_id = o.user_address_id "
			+ " LEFT JOIN (\r\n"
			+ "    SELECT \r\n"
			+ "        o.order_item_id,\r\n"
			+ "        SUM(CASE WHEN ol.pending = true THEN ol.total_price ELSE 0 END) AS orderTotalAmount\r\n"
			+ "    FROM \r\n"
			+ "        order_item AS o\r\n"
			+ "    JOIN \r\n"
			+ "        order_item_list AS ol ON ol.order_item_id = o.order_item_id\r\n"
			+ "    GROUP BY \r\n"
			+ "        o.order_item_id\r\n"
			+ " ) AS s ON o.order_item_id = s.order_item_id\r\n"
			+ " WHERE \r\n"
			+ "    ol.order_item_id = :order_item_id\r\n"
			+ "    AND ol.order_status != 'cancelled';\r\n"
			+ "", nativeQuery = true)
	List<Map<String, Object>> getInvoiceDetailsByOrderItemId(@Param("order_item_id") Long orderItemId);

	
	Optional<Invoice> findByOrderItemId(long orderItemId);

}
