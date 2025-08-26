package com.example.kkBazar.repository.dashboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.dashboard.Dashboard2;

public interface Dashboard2Repository extends JpaRepository<Dashboard2, Long> {

	@Query(value = "SELECT p.*,pl.product_list_id,pl.alert_quantity,pl.quantity,c.category_name,pl.unit,d2.dashboard2id,w.status,"
			+ " d2.description,d2.title,COALESCE(dl.discount_amount, pl.discount_amount) as discount_amount,b.brand_name,"
			+ " COALESCE(dl.discount_percentage, pl.discount_percentage) as discount_percentage,"
			+ " COALESCE(dl.buy_rate, pl.buy_rate) as buy_rate,COALESCE(dl.sell_rate, pl.sell_rate) as sell_rate,"
			+ " COALESCE(dl.mrp, pl.mrp) as mrp,COALESCE(dl.gst, pl.gst) as gst,"
			+ " COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,"
			+ " COALESCE(dl.total_amount, pl.total_amount) as total_amount,pi.product_images_id,"
			+ " pv.varient_name,pv.varient_value,pv.product_varient_id,pvi.product_varient_images_id,"
			+ " review_subquery.review_count,review_subquery.average_star_rate, w.wish_list_id,w.user_id,w.status as wishListStatus"
			+ " FROM dsahboard2 as d2"
			+ " JOIN category as c ON c.category_id = d2.category_id"
			+ " JOIN product as p ON p.category_id = c.category_id"
			+ " join brand as b on b.brand_id = p.brand_id"
			+ " JOIN product_list as pl ON pl.product_id = p.product_id"
			+ " JOIN product_images as pi ON pi.product_id = p.product_id"
			+ " left JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ " LEFT JOIN discount_list as dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= current_date()"
			+ " left JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ " LEFT JOIN (SELECT p.product_list_id,COUNT(r.review_id) as review_count,"
			+ " CAST(FORMAT(AVG(r.star_rate), 1) AS DECIMAL(3, 1)) AS average_star_rate"
			+ " FROM product_list AS p"
			+ " LEFT JOIN review AS r ON r.product_list_id = p.product_list_id"
			+ " GROUP BY p.product_list_id) AS review_subquery ON review_subquery.product_list_id = pl.product_list_id", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard2Details();

	Optional<Dashboard2> findByCategoryId(long categoryId);

}
        
