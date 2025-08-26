package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.WishList;

public interface WishListRepository extends JpaRepository<WishList, Long> {

	@Query(value = " select w.wish_list_id, pr.product_id,pr.product_name, u.user_id,u.user_name,p.product_list_id,"
			+ "			 p.buy_rate, p.discount_amount,p.discount_percentage,p.gst,p.gst_tax_amount,p.mrp,p.quantity,"
			+ "			 p.sell_rate,p.total_amount,p.alert_quantity,pri.product_images_id,pri.product_images_upload, pri.product_images_upload_url,"
			+ "			 prv.product_varient_id,prv.varient_name,prv.varient_value,vi.product_varient_images_id,vi.product_varient_image,vi.product_varient_image_url,"
			+ "			 c.category_id,c.category_name,c.category_image,c.url,b.brand_id,b.brand_name,pd.product_description_id,pd.description_name,"
			+ "             pdl.product_description_list_id,pdl.name,pdl.value" + "			 from wish_list as w"
			+ "          join user as u on u.user_id=w.user_id"
			+ "			 join product_list as p on p.product_list_id=w.product_list_id"
			+ "			 join product as pr on pr.product_id=p.product_list_id"
			+ "			 join category as c on c.category_id=pr.category_id"
			+ "			 join brand as b on b.brand_id=pr.brand_id"
			+ "			 join product_images as pri on pri.product_id=p.product_id"
			+ "			join product_varient as prv on prv.product_list_id=p.product_list_id"
			+ "			 join product_varient_images as vi on vi.product_list_id=p.product_list_id"
			+ "             join product_description as pd on pd.product_id=pr.product_id"
			+ "            join product_description_list as pdl on pdl.product_description_id=pd.product_description_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListProductDetails();

	Optional<WishList> findByUserIdAndProductListIdAndProductImagesId(long userId, long productListId, long productImagesId);

	@Query(value = " select w.wish_list_id,p.product_list_id,u.user_id,u.user_name, pr.product_id,pr.product_name,"
			+ " b.brand_id,b.brand_name,c.category_id,c.category_name,c.category_image,c.url,"
			+ " pi.product_images_id,pi.product_images_upload,pi.product_images_upload_url,"
			+ " pv.product_varient_id,pv.varient_name,pv.varient_value,pvi.product_varient_images_id,"
			+ " pvi.product_varient_image,pvi.product_varient_image_url,"
			+ " p.alert_quantity,p.buy_rate,p.discount_amount,p.discount_percentage,p.gst,p.gst_tax_amount,"
			+ " p.mrp,p.quantity,p.sell_rate,p.stock_in,p.total_amount,p.unit,p.description" + " from wish_list as w"
			+ " join user as u on u.user_id=w.user_id"
			+ " join product_list as p on p.product_list_id=w.product_list_id"
			+ " join product as pr on pr.product_id=p.product_id" + " join brand as b on b.brand_id=pr.brand_id"
			+ " join category as c on c.category_id=pr.category_id"
			+ " join product_images as pi on pi.product_id=pr.product_id"
			+ " join product_varient as pv on pv.product_list_id=p.product_list_id"
			+ " join product_varient_images as pvi on pvi.product_list_id=p.product_list_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListProductDetails1();

	@Query(value = " SELECT w.wish_list_id, p.product_list_id, u.user_id, u.user_name, pr.product_id, pr.product_name,"
			+ "  b.brand_id, b.brand_name, c.category_id, c.category_name, c.category_image, c.url,"
			+ "  pi.product_images_id, pi.product_images_upload, pi.product_images_upload_url,"
			+ "  pv.product_varient_id, pv.varient_name, pv.varient_value, pvi.product_varient_images_id,"
			+ "  pvi.product_varient_image, pvi.product_varient_image_url,"
			+ "  p.alert_quantity, p.buy_rate, p.discount_amount, p.discount_percentage, p.gst, p.gst_tax_amount,"
			+ "  p.mrp, p.quantity, p.sell_rate, p.stock_in, p.total_amount, p.unit, p.description"
			+ "  FROM wish_list AS w" + "  JOIN user AS u ON u.user_id = w.user_id"
			+ "  JOIN product_list AS p ON p.product_list_id = w.product_list_id"
			+ "  JOIN product AS pr ON pr.product_id = p.product_id" + "  JOIN brand AS b ON b.brand_id = pr.brand_id"
			+ "  JOIN category AS c ON c.category_id = pr.category_id"
			+ "  JOIN product_images AS pi ON pi.product_id = pr.product_id"
			+ "  JOIN product_varient AS pv ON pv.product_list_id = p.product_list_id"
			+ "  JOIN product_varient_images AS pvi ON pvi.product_list_id = p.product_list_id"
			+ "  WHERE w.wish_list_id = :wish_list_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListDetailsById(@Param("wish_list_id") Long wishListId);

	@Query(value = "select w.wish_list_id as wishListId,w.product_list_id as productListId,w.product_varient_images_id as productVarientImagesId,w.user_id as userId,pl.alert_quantity as alertQuantity,pl.buy_rate as buyRate,pl.description,pl.discount_amount as discountAmount,"
			+ " pl.discount_percentage as discountPercentage,pl.gst,pl.gst_tax_amount as gstTaxAmount,pl.mrp,pl.quantity,pl.sell_rate as sellRate,pl.total_amount as totalAmount,"
			+ " pl.unit,p.product_name as productName,b.brand_id as brandId,b.brand_name as brandName,c.category_id as categoryId,c.category_name as categoryName from wish_list as w"
			+ " join product_list as pl on pl.product_list_id = w.product_list_id"
			+ " join product_varient_images as pv on pv.product_varient_images_id = w.product_varient_images_id"
			+ " join product as p on p.product_id = pl.product_id"
			+ " join brand as b on b.brand_id = p.brand_id"
			+ " join category as c on c.category_id = p.category_id"
			+ " where w.user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListDetailsByUserId(@Param("user_id") Long userId);

	@Query(value =  " SELECT w.wish_list_id, p.product_list_id, u.user_id, u.user_name, pr.product_id, pr.product_name,w.product_images_id,"
			+ "	p.alert_quantity, p.buy_rate, p.discount_amount, p.discount_percentage, p.gst, p.gst_tax_amount,"
			+ "	p.mrp, p.quantity, p.sell_rate, p.stock_in, p.total_amount, p.unit, pr.description"
			+ "	FROM wish_list AS w"
			+ "	JOIN user AS u ON u.user_id = w.user_id"
			+ "	JOIN product_list AS p ON p.product_list_id = w.product_list_id"
			+ "	JOIN product AS pr ON pr.product_id = p.product_id"
			+ "	 join product_images as pie on pie.product_images_id=w.product_images_id"	
			+ " WHERE u.user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListDetailByUserId(@Param("user_id") Long userId);
	
	@Query(value = " SELECT w.wish_list_id, p.product_list_id, u.user_id, u.user_name, pr.product_id, pr.product_name,w.product_images_id,"
			+ "	p.alert_quantity, p.buy_rate, p.discount_amount, p.discount_percentage, p.gst, p.gst_tax_amount,"
			+ "	p.mrp, p.quantity, p.sell_rate, p.stock_in, p.total_amount, p.unit, pr.description"
			+ "	FROM wish_list AS w"
			+ "	JOIN user AS u ON u.user_id = w.user_id"
			+ "	JOIN product_list AS p ON p.product_list_id = w.product_list_id"
			+ "	JOIN product AS pr ON pr.product_id = p.product_id"
			+ "	 join product_images as pie on pie.product_images_id=w.product_images_id"	
			+ " WHERE u.user_id = :user_id", nativeQuery = true)
	List<Map<String, Object>> getAllWishListDetailByUserIdList(@Param("user_id") Long userId);



	
	
}
