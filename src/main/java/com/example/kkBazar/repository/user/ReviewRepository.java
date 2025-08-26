package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.user.Review;

public interface ReviewRepository extends JpaRepository<Review, Long>{
	@Query(value = "SELECT r.review_id, r.date, r.description, r.user_id, r.product_varient_images_id, r.star_rate, " +
	        " u.user_name, up.user_profile_id " +
	        " FROM review AS r " +
	        " JOIN product_varient_images AS p ON p.product_varient_images_id = r.product_varient_images_id " +
	        " JOIN user AS u ON u.user_id = r.user_id " +
	        " JOIN user_profile AS up ON up.user_id = u.user_id " +
	        " WHERE p.product_varient_images_id = :product_varient_images_id", nativeQuery = true)
	List<Map<String, Object>> getReviewDetails(@Param("product_varient_images_id") Long productVarientImagesId);
	
	
	@Query(value="select r.* from review as r"
			+ " join user as u on u.user_id = r.user_id"
			+ " where u.user_id = :user_id and r.review_id = :review_id",nativeQuery = true)
	Map<String, Object> getAllReviewDetailsWithId(Long user_id, Long review_id);
	
	
	 boolean existsByUserIdAndProductListId(long userId, long productListId);

}
