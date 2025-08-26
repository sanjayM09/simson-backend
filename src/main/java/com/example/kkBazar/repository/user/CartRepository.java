package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.kkBazar.entity.user.AddToCart;

public interface CartRepository extends JpaRepository<AddToCart, Long> {

	@Query(value = " select a.add_to_cart_id,u.user_id,u.user_name,p.total_amount, pr.product_id,pr.product_name, p.product_list_id,"
			+ "	p.buy_rate, p.discount_amount,p.discount_percentage,p.gst,p.gst_tax_amount,p.mrp,"
			+ " p.sell_rate,p.alert_quantity,pri.product_images_id,pri.product_images_upload_url,a.total_price as cartPrice,"
			+ "	prv.product_varient_id,prv.varient_name,prv.varient_value,vi.product_varient_images_id,vi.product_varient_image_url,"
			+ "	c.category_id,c.category_name,c.url,b.brand_id,b.brand_name,pd.product_description_id,pd.description_name,"
			+ "	pdl.product_description_list_id,pdl.name,pdl.value" + "	from add_to_cart as a"
			+ " join user as u on u.user_id=a.user_id"
			+ "	join product_list as p on p.product_list_id=a.product_list_id"
			+ "	join product as pr on pr.product_id=p.product_list_id"
			+ "	join category as c on c.category_id=pr.category_id" + "	join brand as b on b.brand_id=pr.brand_id"
			+ "	join product_images as pri on pri.product_id=p.product_id"
			+ "	left join product_varient as prv on prv.product_list_id=p.product_list_id"
			+ "	left join product_varient_images as vi on vi.product_list_id=p.product_list_id"
			+ "	join product_description as pd on pd.product_id=pr.product_id"
			+ "	join product_description_list as pdl on pdl.product_description_id=pd.product_description_id", nativeQuery = true)
	List<Map<String, Object>> getAllAddToCartDetails();


	Optional<AddToCart> findByUserIdAndProductListId(long userId, long productListId);

	@Query(value = " select a.*,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,p.description,p.product_name,pl.alert_quantity,a.quantity as cartQuantity,pl.quantity as productQuantity,"
			+ "			 coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "			 coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,u.user_name,a.total_price as cartPrice,"
			+ "			coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,pl.unit,pl.product_id "
			+ "			from add_to_cart as a"
			+ "			 join product_list as pl on pl.product_list_id = a.product_list_id"
			+ "			 join product as p on p.product_id = pl.product_id"
			+ "			 join user as u on u.user_id = a.user_id"
			+ "			 JOIN product_images AS pi ON pi.product_images_id = a.product_images_id"
			+ "			 left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ "			 where a.user_id =:user_id "
			+ "			 ORDER BY a.add_to_cart_id DESC", nativeQuery = true)
	List<Map<String, Object>> getAllCartDetailsByUserId(@Param("user_id") Long userId);


	

	

}
