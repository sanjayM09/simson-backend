package com.example.kkBazar.repository.dashboard;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.kkBazar.entity.dashboard.Dashboard1;

public interface Dashboard1Repository extends JpaRepository<Dashboard1, Long> {

	@Query(value = "select d1.*,p.product_id as productId,p.product_name as productName, pl.alert_quantity,pl.deleted,"
			+ " pl.quantity,pl.stock_in,pl.unit,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,c.category_id,c.category_name,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " p.description as listDescription,pv.product_varient_images_id as productVarientImagesId from dashboard1 as d1"
			+ " join product as p on p.product_id = d1.product_id"
			+ " join category as c on c.category_id = p.category_id"
			+ " join product_list as pl on pl.product_list_id = d1.product_list_id"
			+ " left join product_varient_images as pv on pv.product_list_id = pl.product_list_id "
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " join product_images as pi on pi.product_images_id = d1.product_images_id", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard1Details();

	Optional<Dashboard1> findByProductId(long productId);

	@Query(value = "select d1.*,p.product_name as productName,p.product_id as productId, pl.alert_quantity,pl.deleted,c.category_id,c.category_name,"
			+ "	pl.quantity,pl.stock_in,pl.unit,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ "	p.description as listDescription,pv.product_varient_images_id as productVarientImagesId from dashboard1 as d1"
			+ "	join product as p on p.product_id = d1.product_id"
			+ " join category as c on c.category_id = p.category_id"
			+ "	join product_list as pl on pl.product_list_id = d1.product_list_id"
			+ " left  join product_varient_images as pv on pv.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ "	join product_images as pi on pi.product_images_id = d1.product_images_id"
			+ " where d1.status=true", nativeQuery = true)
	List<Map<String, Object>> getAllDashboard1ActiveDetails();

}
