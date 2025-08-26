package com.example.kkBazar.repository.productDetails;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.productDetails.AddStock;


public interface AddStockRepository extends JpaRepository<AddStock, Long>{

	
	@Query(value=" select s.stock_id, sl.stock_list_id, sl.quantity,sl.product_list_id,"
			+ " p.product_name,p.product_id,pl.quantity as productQuantity,"
			+ " pv.product_varient_id,pv.varient_name,pv.varient_value"
			+ " from add_stock as s "
			+ " join add_stock_list as sl on sl.stock_id=s.stock_id"
			+ " join product_list as pl on pl.product_list_id=sl.product_list_id"
			+ " join product as p on p.product_id=pl.product_id"
			+ " left join product_varient as pv on pv.product_list_id=pl.product_list_id", nativeQuery = true)
	List<Map<String, Object>>getProdustStockDetails();
	
	
	
	
	@Query(value=" SELECT sl.quantity, sl.stock_list_id, a.stock_id, a.date,"
			+ "       pl.product_list_id, pl.alert_quantity, pl.buy_rate, pl.discount_amount, pl.discount_percentage,"
			+ "       pl.gst, pl.gst_tax_amount, pl.mrp, pl.sell_rate, pl.stock_in, p.description, pl.total_amount, pl.quantity as product_quantity,"
			+ "       pl.unit, p.product_id, p.product_name"
			+ " FROM add_stock AS a"
			+ " JOIN add_stock_list AS sl ON sl.stock_id = a.stock_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = sl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
			+ " WHERE DATE(a.date) = CURRENT_DATE", nativeQuery = true)
	List<Map<String, Object>>findStockListDetailByCurrentDate();
	
	
	
	
	@Query(value=" SELECT sl.quantity, sl.stock_list_id, a.stock_id, a.date,"
			+ "       pl.product_list_id, pl.alert_quantity, pl.buy_rate, pl.discount_amount, pl.discount_percentage,"
			+ "       pl.gst, pl.gst_tax_amount, pl.mrp, pl.sell_rate, pl.stock_in, p.description, pl.total_amount, pl.quantity as product_quantity,"
			+ "       pl.unit, p.product_id, p.product_name, pvi.product_varient_images_id,"
			+ "       pvi.product_varient_image, pvi.product_varient_image_url, pv.product_varient_id,pi.product_images_id as productImagesId,"
			+ "       pv.varient_name, pv.varient_value"
			+ " FROM add_stock AS a"
			+ " JOIN add_stock_list AS sl ON sl.stock_id = a.stock_id"
			+ " JOIN product_list AS pl ON pl.product_list_id = sl.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id"
		    + " join product_images as pi on pi.product_id=p.product_id"
			+ " left JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ "  left JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ " WHERE DATE(a.date) = CURRENT_DATE", nativeQuery = true)
	List<Map<String, Object>>findStockListDetailsByCurrentDate();
	
	
	
	@Query(value = "SELECT sl.quantity, sl.stock_list_id as stockListId, a.stock_id as stockId, a.date,"
	        + " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate, pl.discount_amount as discountAmount, pl.discount_percentage as discountPercentage,"
	        + " pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, p.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
	        + " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,pi.product_images_id as productImagesId,"
	        + "  pv.product_varient_id as productVarientId,"
	        + " pv.varient_name as varientName, pv.varient_value as varientValue"
	        + " FROM add_stock AS a"
	        + " JOIN add_stock_list AS sl ON sl.stock_id = a.stock_id"
	        + " JOIN product_list AS pl ON pl.product_list_id = sl.product_list_id"
	        + " JOIN product AS p ON p.product_id = pl.product_id"
	        + " join product_images as pi on pi.product_id=p.product_id"
	        + " left JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
	        + " left JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
	        + " WHERE a.date BETWEEN :startDate AND :endDate", nativeQuery = true)
	List<Map<String, Object>> findStockListBetweenDate(
	        @Param("startDate") LocalDate startDate,
	        @Param("endDate") LocalDate endDate);

	@Query(value = "SELECT sl.quantity, sl.stock_list_id as stockListId, a.stock_id as stockId, a.date,"
	        + " pl.product_list_id as productListId, pl.alert_quantity as alertQuantity, pl.buy_rate as buyRate , pl.discount_amount as discountAmount, pl.discount_percentage as discountPercentage ,"
	        + " pl.gst, pl.gst_tax_amount as gstTaxAmount, pl.mrp, pl.sell_rate as sellRate, pl.stock_in as stockIn, p.description, pl.total_amount as totalAmount, pl.quantity as productQuantity,"
	        + " pl.unit, p.product_id as productId, p.product_name as productName, pvi.product_varient_images_id as productVarientImagesId,pi.product_images_id as productImagesId,"
	        + "  pv.product_varient_id as productVarientId ,"
	        + " pv.varient_name as varientName, pv.varient_value as varientValue"
	        + " FROM add_stock AS a"
	        + " JOIN add_stock_list AS sl ON sl.stock_id = a.stock_id"
	        + " JOIN product_list AS pl ON pl.product_list_id = sl.product_list_id"
	        + " JOIN product AS p ON p.product_id = pl.product_id"
	        + " join product_images as pi on pi.product_id=p.product_id"
	        + " left JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
	        + " left JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
	        + "    WHERE monthname(a.date)=:month"
	        + "             AND year(a.date)=:year", nativeQuery = true)
	List<Map<String, Object>> findStockListByMonthYear(
	        @Param("month") String month,
	        @Param("year") String year);



@Query(value=" select p.product_id,p.product_name,pl.product_list_id,pl.alert_quantity, pl.quantity, b.brand_name,b.brand_id,"
		+ "	 pi.product_images_id,pi.product_images_upload,pi.product_images_upload_url"
		+ "	 from  product_list as pl"
		+ "  join product as p on p.product_id=pl.product_id"
		+ "	 join product_images as pi on pi. product_id = p.product_id"
		+ "	 join brand as b on b.brand_id=p.brand_id"
		+ "	 WHERE pl.alert_quantity >= pl.quantity;", nativeQuery = true)

List<Map<String, Object>>getOutOfStockDetails();
	
}
