package com.example.kkBazar.repository.productDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.productDetails.Discount;


public interface DiscountRepository extends JpaRepository<Discount, Long> {

	
	@Query(value=" select p.*,pl.product_list_id,pl.buy_rate,pl.sell_rate,"
			+ "			 pl.alert_quantity,pl.gst,pl.gst_tax_amount,"
			+ "			pl.mrp,pl.quantity,pl.total_amount,c.category_name,pl.unit,"
			+ "			 pi.product_images_id,pv.varient_name,pv.varient_value,pl.description as listDescription,"
			+ "			 pv.product_varient_id,pvi.product_varient_images_id,d.discount_id,d.discount_title,"
			+ "			 dl.discount_list_id,dl.discount_amount as newDiscountAmount,dl.status,dl.discount_percentage as newDiscountPercentage,"
			+ "			 dl.start_date,dl.end_date"
			+ "			 from category as c "
			+ "			join product as p on p.category_id = c.category_id"
			+ "		 join product_list as pl on pl.product_id = p.product_id"
			+ "			 join product_images as pi on pi.product_id = p.product_id"
			+ "			left join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ "			left join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ "			 join discount_list as dl on dl.product_list_id=pl.product_list_id"
			+ "			 join discount as d on d.discount_id=dl.discount_id where dl.status=true", nativeQuery=true)
	List<Map<String, Object>> getAllDiscountDetails();
	

	@Query(value="  select p.*,pl.product_list_id,pl.buy_rate,pl.sell_rate,pl.discount_amount,"
			+ "			 pl.discount_percentage,pl.alert_quantity,pl.gst,pl.gst_tax_amount,"
			+ "			 pl.mrp,pl.quantity,pl.total_amount,c.category_name,pl.unit,"
			+ "			 pi.product_images_id,pv.varient_name,pv.varient_value,pl.description as listDescription,"
			+ "			 pv.product_varient_id"
			+ "			 from category as c "
			+ "			 join product as p on p.category_id = c.category_id"
			+ "			 join product_list as pl on pl.product_id = p.product_id"
			+ "			 join product_images as pi on pi.product_id = p.product_id"
			+ "			left join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ "			 join discount_list as dl on dl.product_list_id=pl.product_list_id"
			+ "			 join discount as d on d.discount_id=dl.discount_id ", nativeQuery=true)
	List<Map<String, Object>> getAllDiscountDetail();
	
	@Query(value=" select d.discount_id, d.discount_title,d.discount_percentage, d.start_date, d.end_date,"
			+ " dl.discount_list_id,dl.discount_amount,dl.status,"
			+ " dl.buy_rate,dl.gst,dl.gst_tax_amount,dl.mrp,dl.sell_rate,dl.total_amount,"
			+ " pl.product_list_id,p.product_id,p.product_name"
			+ " from discount as d"
			+ " join discount_list as dl on dl.discount_id=d.discount_id"
			+ " join product_list as pl on pl.product_list_id=dl.product_list_id"
			+ " join product as p on p.product_id=pl.product_id", nativeQuery = true)
	List<Map<String, Object>> getAllDiscountDetails1();
	
	
	@Query(value=" SELECT d.discount_id, d.discount_title, d.discount_percentage, d.start_date, d.end_date,"
			+ " dl.discount_list_id, dl.discount_amount, dl.status,"
			+ " dl.buy_rate, dl.gst, dl.gst_tax_amount, dl.mrp, dl.sell_rate, dl.total_amount,"
			+ " pl.product_list_id, p.product_id, p.product_name,pvi.product_varient_images_id,"
			+ " pvi.product_varient_image,pvi.product_varient_image_url,pv.product_varient_id,"
			+ " pv.varient_name,pv.varient_value"
			+ " FROM discount AS d"
			+ " JOIN discount_list AS dl ON dl.discount_id = d.discount_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = dl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " left join product_varient as pv on pv.product_list_id=pl.product_list_id"
			+ " left join product_varient_images as pvi on pvi.product_list_id=pl.product_list_id"
			+ " WHERE CURDATE() BETWEEN d.start_date AND d.end_date", nativeQuery = true)
	List<Map<String, Object>> findDiscountListByCurrentDate();
	
	@Query(value=" SELECT d.discount_id as discountId, d.discount_title as discountTitle, d.discount_percentage as discountPercentage, d.start_date as startDate, d.end_date as endDate,"
			+ "		 dl.discount_list_id as discountListId, dl.discount_amount as discountAmount, dl.status,"
			+ "		dl.buy_rate as buyRate, dl.gst, dl.gst_tax_amount as gstTaxAmount, dl.mrp, dl.sell_rate as sellRate, dl.total_amount as totalAmount,"
			+ "		 pl.product_list_id as productListId, p.product_id as productId, p.product_name as productName,pvi.product_varient_images_id as productVarientImagesId,"
			+ "		pv.product_varient_id as productVarientId,"
			+ "		 pv.varient_name as varientName,pv.varient_value as varientValue"
			+ "		 FROM discount AS d"
			+ " JOIN discount_list AS dl ON dl.discount_id = d.discount_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = dl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " left join product_varient as pv on pv.product_list_id=pl.product_list_id"
			+ " left join product_varient_images as pvi on pvi.product_list_id=pl.product_list_id"
			+ "   WHERE monthname(d.start_date)=:month"
		    + "   AND year(d.start_date)=:year", nativeQuery = true)
		List<Map<String, Object>> findDiscountListByMonthYear(
		        @Param("month") String month,
		        @Param("year") String year);

	@Query(value=" SELECT d.discount_id as discountId, d.discount_title as discountTitle, d.discount_percentage as discountPercentage, d.start_date as startDate, d.end_date as endDate,"
			+ " dl.discount_list_id as discountListId, dl.discount_amount as discountAmount, dl.status,"
			+ " dl.buy_rate as buyRate, dl.gst, dl.gst_tax_amount as gstTaxAmount, dl.mrp, dl.sell_rate as sellRate, dl.total_amount as totalAmount,"
			+ " pl.product_list_id as productListId, p.product_id as productId, p.product_name as productName,pvi.product_varient_images_id as productVarientImagesId,"
			+ " pv.product_varient_id as productVarientId,"
			+ " pv.varient_name as varientName,pv.varient_value as varientValue"
			+ " FROM discount AS d"
			+ " JOIN discount_list AS dl ON dl.discount_id = d.discount_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = dl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " left join product_varient as pv on pv.product_list_id=pl.product_list_id"
			+ " left join product_varient_images as pvi on pvi.product_list_id=pl.product_list_id"
			 + "  WHERE d.start_date BETWEEN :startDate AND :endDate", nativeQuery = true)
	List<Map<String, Object>> findDiscountListBetweenDate(
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate);
	
	
	@Query(value=" SELECT d.discount_id,d.discount_title,d.start_date,d.end_date,dl.discount_list_id,"
			+ " pl.product_list_id,dl.discount_percentage,pi.product_images_id,"
			+ " p.product_id,p.product_name"
			+ " FROM discount AS d JOIN discount_list AS dl ON dl.discount_id = d.discount_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = dl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " join product_images as pi on pi. product_id = p.product_id"
			+ " left JOIN product_varient AS pv ON pv.product_list_id = pl.product_list_id"
			+ " WHERE YEAR(CURDATE()) = YEAR(d.end_date) AND MONTH(CURDATE()) = MONTH(d.end_date)", nativeQuery = true)
			List<Map<String, Object>>getExpirationDiscountDetails();
}
