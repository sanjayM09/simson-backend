package com.example.kkBazar.repository.addProduct;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@Query(value = "select p.product_id,p.category_id,p.brand_id,c.category_name,b.brand_name from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " join brand as b on b.brand_id = p.brand_id", nativeQuery = true)
	List<Map<String, Object>> getAllProductWithCategoryAndBrandDetails();

	@Query(value = " SELECT p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,pl.alert_quantity,pl.quantity,c.category_name,pl.unit,"
			+ "	 pi.deleted,pvi.deleted AS varient_img_delete,COALESCE(dl.discount_amount, pl.discount_amount) AS discount_amount,pl.return_type,pl.return_status,pl.return_count,"
			+ "	 COALESCE(dl.discount_percentage, pl.discount_percentage) AS discount_percentage,"
			+ "	 COALESCE(dl.buy_rate, pl.buy_rate) AS buy_rate,COALESCE(dl.sell_rate, pl.sell_rate) AS sell_rate,"
			+ "	 COALESCE(dl.mrp, pl.mrp) AS mrp,COALESCE(dl.gst, pl.gst) AS gst,COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) AS gst_tax_amount,"
			+ "	 COALESCE(dl.total_amount, pl.total_amount) AS total_amount,pi.product_images_id,pv.varient_name,pv.varient_value,"
			+ "	 pv.product_varient_id,pvi.product_varient_images_id,review_subquery.review_count,review_subquery. average_star_rate,si.size_id,si.size_name,"
			+ "	 w.wish_list_id,w.status as wishListStatus,w.user_id FROM product AS p"
			+ "  JOIN category AS c ON c.category_id = p.category_id"
			+ "	 LEFT JOIN brand AS b ON b.brand_id = p.brand_id"
			+ "	 JOIN product_list AS pl ON pl.product_id = p.product_id"
			+ "	 JOIN product_images AS pi ON pi.product_id = p.product_id"
			+ "	LEFT JOIN product_varient AS pv ON pv.product_list_id = pl.product_list_id"
			+ "	LEFT JOIN product_varient_images AS pvi ON pvi.product_list_id = pl.product_list_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ "  left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ "	 LEFT JOIN discount_list AS dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE()"
			+ "	 LEFT JOIN (SELECT pl.product_list_id,COUNT(r.review_id) AS review_count,"
			+ "	 CAST(FORMAT(AVG(r.star_rate), 1) AS DECIMAL(3, 1)) AS average_star_rate FROM product_list AS pl"
			+ "	 LEFT JOIN review AS r ON r.product_list_id = pl.product_list_id"
			+ "  GROUP BY pl.product_list_id) AS review_subquery ON review_subquery.product_list_id = pl.product_list_id"
//			+ "	 WHERE pi.deleted = FALSE AND pvi.deleted = FALSE AND pl.deleted = FALSE AND pv.deleted = FALSE"
			, nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetails();

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,w.wish_list_id,w.status as wishListStatus,w.user_id,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.return_type,pl.return_status,pl.return_count,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,si.size_id,si.size_name,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.unit,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where pl.product_list_id = :product_list_id "
//			+ "  pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false"
			, nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductListDetailsWithId(Long product_list_id);

	@Query(value = "SELECT "
			+ "    p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,pl.quantity,pl.unit,c.category_name,pi.deleted,w.wish_list_id,w.status as wishListStatus,w.user_id,si.size_id,si.size_name,"
			+ "    pvi.deleted AS varient_img_delete,COALESCE(dl.discount_amount, pl.discount_amount) AS discount_amount,COALESCE(dl.discount_percentage, pl.discount_percentage) AS discount_percentage,"
			+ "    COALESCE(dl.buy_rate, pl.buy_rate) AS buy_rate,COALESCE(dl.sell_rate, pl.sell_rate) AS sell_rate,COALESCE(dl.mrp, pl.mrp) AS mrp,COALESCE(dl.gst, pl.gst) AS gst,pl.return_type,pl.return_status,pl.return_count,"
			+ "    COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) AS gst_tax_amount,COALESCE(dl.total_amount, pl.total_amount) AS total_amount,review_subquery.review_count,review_subquery.average_star_rate,"
			+ "    pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id"
			+ " FROM product AS p" 
			+ " JOIN category AS c ON c.category_id = p.category_id"
			+ " LEFT JOIN brand AS b ON b.brand_id = p.brand_id"
			+ " JOIN product_list AS pl ON pl.product_id = p.product_id"
			+ " JOIN product_images AS pi ON pi.product_id = p.product_id"
			+ " LEFT JOIN product_varient AS pv ON pv.product_list_id = pl.product_list_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT JOIN product_varient_images AS pvi ON pvi.product_list_id = pl.product_list_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id" + " LEFT JOIN "
			+ "    (SELECT pl.product_list_id,COUNT(r.review_id) AS review_count,CAST(FORMAT(AVG(r.star_rate), 1) AS DECIMAL(3, 1)) AS average_star_rate"
			+ "     FROM product_list AS pl " 
			+ "     LEFT JOIN review AS r ON r.product_list_id = pl.product_list_id"
			+ "     WHERE pl.deleted = false GROUP BY pl.product_list_id) AS review_subquery "
			+ "    ON review_subquery.product_list_id = pl.product_list_id"
			+ " LEFT JOIN discount_list AS dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE()"
			+ " WHERE p.product_id =:product_id "
//			+ "AND pi.deleted = false AND pvi.deleted = false AND pl.deleted = false AND pv.deleted = false"
			+ "  ORDER BY p.product_id DESC ;", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithId(Long product_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.unit,pl.pieces,pl.weight,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,si.size_id,si.size_name,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where coalesce(dl.total_amount, pl.total_amount) <= :total_amount "
			+ " and pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false and c.category_id = :category_id", nativeQuery = true)
	List<Map<String, Object>> getAllProductDetailsWithLessThanSellRate(double total_amount, Long category_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.unit,pl.pieces,pl.weight,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,si.size_id,si.size_name,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where coalesce(dl.total_amount, pl.total_amount) >= :total_amount "
			+ " and pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false and c.category_id = :category_id", nativeQuery = true)
	List<Map<String, Object>> getAllProductDetailsWithGreaterThanSellRate(double total_amount, Long category_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,w.wish_list_id,w.status as wishListStatus,w.user_id,si.size_id,si.size_name,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.return_type,pl.return_status,pl.return_count,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " left join product_list as pl on pl.product_id = p.product_id"
			+ " left join product_images as pi on pi.product_id = p.product_id"
			+ " LEFT  join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ "  left join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where p.category_id = :category_id "
			+ " and pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithCategoryById(Long category_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,w.wish_list_id,w.status as wishListStatus,w.user_id,si.size_id,si.size_name,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.return_type,pl.return_status,pl.return_count,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " wherew.user_id = :user_id "
			+ " and pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithCategoryById222(Long user_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,w.wish_list_id,w.status as wishListStatus,w.user_id,si.size_id,si.size_name,"
			+ " pl.quantity,c.category_name,pi.deleted,pvi.deleted as varient_img_delete,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,"
			+ "	coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "	coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.return_type,pl.return_status,pl.return_count,"
			+ "	coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity,pv.product_varient_id,pvi.product_varient_images_id from product as p"
			+ " left join category as c on c.category_id = p.category_id"
			+ " left join brand as b on b.brand_id = p.brand_id"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " join product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where p.category_id = :category_id and p.product_id != :product_id"
//			+ " and pi.deleted= false and pvi.deleted = false and pl.deleted = false and pv.deleted = false"
			, nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithCategoryByIdAndProductId(Long category_id,
			Long product_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,si.size_id,si.size_name,"
			+ " coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ " coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,w.wish_list_id,w.status as wishListStatus,w.user_id,"
			+ " coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " c.category_name,pi.deleted,pvi.deleted as varient_img_delete,pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity, pv.product_varient_id,pvi.product_varient_images_id"
			+ " from product as p" + " join  category as c on c.category_id = p.category_id"
			+ " left join  brand as b on b.brand_id = p.brand_id"
			+ " join  product_list as pl on pl.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " join  product_images as pi on pi.product_id = p.product_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where c.category_id = :category_id and pi.deleted = false  and pvi.deleted = false and pl.deleted = false and pv.deleted=false", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithCategoryId(Long category_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,si.size_id,si.size_name,"
			+ " coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ " coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.quantity,"
			+ " coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " c.category_name,pi.deleted,pvi.deleted as varient_img_delete,pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity, pv.product_varient_id,pvi.product_varient_images_id"
			+ " from product as p" + " join  category as c on c.category_id = p.category_id"
			+ " left join  brand as b on b.brand_id = p.brand_id"
			+ " join  product_list as pl on pl.product_id = p.product_id"
			+ " join  product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where b.brand_id = :brand_id and pi.deleted = false  and pvi.deleted = false and pl.deleted = false and pv.deleted=false ", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithBrandId(Long brand_id);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,si.size_id,si.size_name,"
			+ " coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ " coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.quantity,"
			+ " coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " c.category_name,pi.deleted,pvi.deleted as varient_img_delete,pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity, pv.product_varient_id,pvi.product_varient_images_id"
			+ " from product as p" + " join  category as c on c.category_id = p.category_id"
			+ " left join  brand as b on b.brand_id = p.brand_id"
			+ " join  product_list as pl on pl.product_id = p.product_id"
			+ " join  product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where b.brand_id = :brand_id and pi.deleted = false  and pvi.deleted = false and pl.deleted = false and pv.deleted=false "
			+ " and coalesce(dl.total_amount, pl.total_amount) <= :total_amount", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithBrandIdAndBelowRate(Long brand_id,
			double total_amount);

	@Query(value = "select p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,coalesce(dl.discount_amount, pl.discount_amount) as discount_amount,pl.unit,si.size_id,si.size_name,"
			+ " coalesce(dl.discount_percentage, pl.discount_percentage) as discount_percentage,coalesce(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ " coalesce(dl.sell_rate, pl.sell_rate) as sell_rate,coalesce(dl.mrp, pl.mrp) as mrp,pl.quantity,"
			+ " coalesce(dl.gst, pl.gst) as gst,coalesce(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,coalesce(dl.total_amount, pl.total_amount) as total_amount,"
			+ " c.category_name,pi.deleted,pvi.deleted as varient_img_delete,pi.product_images_id,pv.varient_name,pv.varient_value,pl.alert_quantity, pv.product_varient_id,pvi.product_varient_images_id"
			+ " from product as p" + " join  category as c on c.category_id = p.category_id"
			+ " left join  brand as b on b.brand_id = p.brand_id"
			+ " join  product_list as pl on pl.product_id = p.product_id"
			+ " join  product_images as pi on pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT join product_varient as pv on pv.product_list_id = pl.product_list_id"
			+ " LEFT join product_varient_images as pvi on pvi.product_list_id = pl.product_list_id"
			+ " left join discount_list as dl on dl.product_list_id = pl.product_list_id and dl.end_date >= current_date()"
			+ " where b.brand_id = :brand_id and pi.deleted = false  and pvi.deleted = false and pl.deleted = false and pv.deleted=false "
			+ " and coalesce(dl.total_amount, pl.total_amount) >= :total_amount", nativeQuery = true)
	List<Map<String, Object>> getAllCategoryWithProductDetailsWithBrandIdAndAboveRate(Long brand_id,
			double total_amount);

	Optional<Product> findById(Long productId);

	@Query(value = "select p.category_id as categoryId,p.brand_id as brandId,b.brand_name as brandName from product as p"
			+ " join category as c on c.category_id = p.category_id" + " join brand as b on b.brand_id = p.brand_id"
			+ " where c.category_id = :category_id", nativeQuery = true)
	List<Map<String, Object>> getAllBrandDetails(Long category_id);

	@Query(value = "SELECT p.*, pl.product_list_id, b.brand_name,pl.pieces,pl.weight,si.size_id,si.size_name,"
			+ "       pl.quantity, c.category_name, pi.deleted, pvi.deleted as varient_img_delete,"
			+ "       COALESCE(dl.discount_amount, pl.discount_amount) as discount_amount, pl.unit,"
			+ "       COALESCE(dl.discount_percentage, pl.discount_percentage) as discount_percentage,"
			+ "       COALESCE(dl.buy_rate, pl.buy_rate) as buy_rate,"
			+ "       COALESCE(dl.sell_rate, pl.sell_rate) as sell_rate, COALESCE(dl.mrp, pl.mrp) as mrp,"
			+ "       COALESCE(dl.gst, pl.gst) as gst, COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,"
			+ "       COALESCE(dl.total_amount, pl.total_amount) as total_amount,"
			+ "       pi.product_images_id, pv.varient_name, pv.varient_value,"
			+ "       pl.alert_quantity, pv.product_varient_id, pvi.product_varient_images_id" + " FROM product as p"
			+ " JOIN category as c ON c.category_id = p.category_id"
			+ " LEFT JOIN brand as b ON b.brand_id = p.brand_id"
			+ " JOIN product_list as pl ON pl.product_id = p.product_id"
			+ " JOIN product_images as pi ON pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
			+ " LEFT JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
			+ " LEFT JOIN discount_list as dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE()"
			+ " WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName, '%'), ' ', '')))"
			+ " AND pi.deleted = FALSE AND pvi.deleted = FALSE AND pl.deleted = FALSE AND pv.deleted = FALSE", nativeQuery = true)
	List<Map<String, Object>> getAllDetailsByProductName(@Param("productName") String productName);

//@Query(value=" SELECT p.*, pl.product_list_id,"
//		+ "              pl.quantity,  pi.deleted, pvi.deleted as varient_img_delete,"
//		+ "              COALESCE(dl.discount_amount, pl.discount_amount) as discount_amount, pl.unit,"
//		+ "              COALESCE(dl.discount_percentage, pl.discount_percentage) as discount_percentage,"
//		+ "              COALESCE(dl.buy_rate, pl.buy_rate) as buy_rate,"
//		+ "              COALESCE(dl.sell_rate, pl.sell_rate) as sell_rate, COALESCE(dl.mrp, pl.mrp) as mrp,"
//		+ "             COALESCE(dl.gst, pl.gst) as gst, COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount,"
//		+ "              COALESCE(dl.total_amount, pl.total_amount) as total_amount,"
//		+ "              pi.product_images_id,pi.product_images_upload, pi.product_images_upload_url, pv.varient_name, pv.varient_value,"
//		+ "              pl.alert_quantity, pv.product_varient_id,pvi.product_varient_image, pvi.product_varient_image_url, pvi.product_varient_images_id"
//		+ "               FROM product as p"
//		+ "              JOIN product_list as pl ON pl.product_id = p.product_id"
//		+ "              JOIN product_images as pi ON pi.product_id = p.product_id"
//		+ "              JOIN product_varient as pv ON pv.product_list_id = pl.product_list_id"
//		+ "              JOIN product_varient_images as pvi ON pvi.product_list_id = pl.product_list_id"
//		+ "              LEFT JOIN discount_list as dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE()"
//		+ "  WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName, '%'), ' ', '')))"
//	        + " AND pi.deleted = FALSE AND pvi.deleted = FALSE AND pl.deleted = FALSE AND pv.deleted = FALSE", nativeQuery = true)
//	List<Map<String, Object>> getAllDetailsByProductName1(@Param("productName") String productName);

	@Query(value = "SELECT p.*, pl.product_list_id, pl.quantity, pi.deleted, "
			+ "COALESCE(dl.discount_amount, pl.discount_amount) as discount_amount, pl.unit,si.size_id,si.size_name, "
			+ "COALESCE(dl.discount_percentage, pl.discount_percentage) as discount_percentage, "
			+ "COALESCE(dl.buy_rate, pl.buy_rate) as buy_rate, "
			+ "COALESCE(dl.sell_rate, pl.sell_rate) as sell_rate, COALESCE(dl.mrp, pl.mrp) as mrp, "
			+ "COALESCE(dl.gst, pl.gst) as gst, COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) as gst_tax_amount, "
			+ "COALESCE(dl.total_amount, pl.total_amount) as total_amount, "
			+ "pi.product_images_id,pi.product_images_upload, pi.product_images_upload_url, "
			+ "pl.alert_quantity " + "FROM product as p JOIN product_list as pl ON pl.product_id = p.product_id "
			+ "JOIN product_images as pi ON pi.product_id = p.product_id "
			+ " join size as si on si.size_id=pl.size_id"
			+ "LEFT JOIN discount_list as dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE() "
			+ "WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName, '%'), ' ', ''))) "
			+ "AND pi.deleted = FALSE AND pl.deleted = FALSE", nativeQuery = true)
	List<Map<String, Object>> getAllDetailsByProductName1(@Param("productName") String productName);

	@Query(value = " SELECT o.product_list_id,p.product_id,p.product_name,b.brand_name,pl.pieces,pl.weight,si.size_id,si.size_name,"
			+ " COUNT(o.product_list_id) AS orderCount,c.category_id,c.category_name,pi.product_images_id"
			+ " FROM order_item_list AS o" + " JOIN product_list AS pl ON pl.product_list_id = o.product_list_id"
			+ " JOIN product AS p ON p.product_id = pl.product_id" + " JOIN brand AS b ON b.brand_id = p.brand_id"
			+ " JOIN product_images as pi on pi.product_id=p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " join category as c on c.category_id=p.category_id"
			+ " GROUP BY o.product_list_id, p.product_id, p.product_name, b.brand_name, pi.product_images_id"
			+ " ORDER BY orderCount DESC LIMIT 5", nativeQuery = true)
	List<Map<String, Object>> getHighestMovingPoroduct();

	@Query(value = "select c.* from category as c" + " where c.category_id = :category_id", nativeQuery = true)
	List<Map<String, Object>> getAllCtaegoryDetailsWithId(Long category_id);

	@Query(value = "SELECT p.*,pl.product_list_id,b.brand_name,pl.pieces,pl.weight,pl.alert_quantity,pl.quantity,c.category_name,pl.unit,si.size_id,si.size_name,\r\n"
			+ " pi.deleted,pvi.deleted AS varient_img_delete,COALESCE(dl.discount_amount, pl.discount_amount) AS discount_amount,pl.return_type,pl.return_status,pl.return_count,\r\n"
			+ " COALESCE(dl.discount_percentage, pl.discount_percentage) AS discount_percentage,\r\n"
			+ " COALESCE(dl.buy_rate, pl.buy_rate) AS buy_rate,COALESCE(dl.sell_rate, pl.sell_rate) AS sell_rate,\r\n"
			+ " COALESCE(dl.mrp, pl.mrp) AS mrp,COALESCE(dl.gst, pl.gst) AS gst,COALESCE(dl.gst_tax_amount, pl.gst_tax_amount) AS gst_tax_amount,\r\n"
			+ " COALESCE(dl.total_amount, pl.total_amount) AS total_amount,pi.product_images_id,pv.varient_name,pv.varient_value,\r\n"
			+ " pv.product_varient_id,pvi.product_varient_images_id,review_subquery.review_count,review_subquery. average_star_rate,\r\n"
			+ " w.wish_list_id,w.status as wishListStatus,w.user_id FROM product AS p\r\n"
			+ " JOIN category AS c ON c.category_id = p.category_id\r\n"
			+ " LEFT JOIN brand AS b ON b.brand_id = p.brand_id\r\n"
			+ " JOIN product_list AS pl ON pl.product_id = p.product_id\r\n"
			+ " JOIN product_images AS pi ON pi.product_id = p.product_id"
			+ " join size as si on si.size_id=pl.size_id"
			+ " LEFT JOIN product_varient AS pv ON pv.product_list_id = pl.product_list_id\r\n"
			+ " LEFT JOIN product_varient_images AS pvi ON pvi.product_list_id = pl.product_list_id\r\n"
			+ " left join wish_list as w on w.product_list_id = pl.product_list_id\r\n"
			+ " LEFT JOIN discount_list AS dl ON dl.product_list_id = pl.product_list_id AND dl.end_date >= CURRENT_DATE()\r\n"
			+ " LEFT JOIN (SELECT pl.product_list_id,COUNT(r.review_id) AS review_count,\r\n"
			+ " CAST(FORMAT(AVG(r.star_rate), 1) AS DECIMAL(3, 1)) AS average_star_rate FROM product_list AS pl\r\n"
			+ " LEFT JOIN review AS r ON r.product_list_id = pl.product_list_id\r\n"
			+ " GROUP BY pl.product_list_id) AS review_subquery ON review_subquery.product_list_id = pl.product_list_id\r\n"
			+ " WHERE (LOWER(REPLACE(p.product_name, ' ', '')) LIKE LOWER(REPLACE(CONCAT('%', :productName , '%'), ' ', ''))) "
//			+ " and pi.deleted = FALSE AND pvi.deleted = FALSE AND pl.deleted = FALSE AND pv.deleted = FALSE"
			, nativeQuery = true)
	List<Map<String, Object>> getAllDetailsByProductNameDetails(@Param("productName") String productName);

	@Query(value = "select p.product_name as productName,p.product_id as productId from product as p", nativeQuery = true)
	List<Map<String, Object>> getAllAddToCartDetails();

}
