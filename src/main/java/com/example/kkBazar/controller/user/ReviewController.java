package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.Review;
import com.example.kkBazar.repository.user.ReviewRepository;
import com.example.kkBazar.service.user.ReviewService;

@RestController
@CrossOrigin
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

	@Autowired
	private ReviewRepository reviewRepository;

	@PostMapping("/review/save")
	public ResponseEntity<Object> saveReviewDetails(@RequestBody Review review) {
		try {
			review.setCreatedAt(new Date(System.currentTimeMillis()));
			
			long userId = review.getUserId();
			long productListId = review.getProductListId();
			
			 boolean reviewExists = reviewRepository.existsByUserIdAndProductListId(userId, productListId);
		        if (reviewExists) {
		            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
		                    .body("Review already exists for this user and product.");
		        }


			if (isNullOrEmpty(review.getStarRate())) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Enter your Rate.");
			}

			int starRate = review.getStarRate();
			if (starRate < 1 || starRate > 5) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("StarRate must be between 1 to 5.");
			}
			review.setDate(LocalDate.now());
			starRate = Math.max(1, Math.min(5, starRate));
			review.setStarRate(starRate);
			reviewService.SaveReview(review);


			return ResponseEntity.ok("Thank you for your review");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving review: " + e.getMessage());
		}
	}

	private boolean isNullOrEmpty(int starRate) {
		return false;
	}

	@PutMapping("/review/edit/{reviewId}")
	public ResponseEntity<Object> updateReview(@PathVariable("reviewId") Long reviewId, @RequestBody Review review) {
		try {

			int starRate = review.getStarRate();

			if (starRate < 1 || starRate > 5) {
				return ResponseEntity.badRequest().body("starRate must be between 1 to 5.");
			}

			starRate = Math.max(1, Math.min(5, starRate));
			review.setStarRate(starRate);
			Review existingReview = reviewService.findById(reviewId);

			if (existingReview == null) {
				return ResponseEntity.notFound().build();
			}
			existingReview.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingReview.setStarRate(review.getStarRate());

			reviewService.SaveReview(review);

			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Thank you for your review!");
			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Error saving review: " + e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}

	@GetMapping("/review/data/{userId}/{reviewId}")
	public ResponseEntity<?> getAllDatas(@PathVariable("userId") Long userId, @PathVariable("reviewId") Long reviewId) {
		Map<String, Object> data = reviewRepository.getAllReviewDetailsWithId(userId, reviewId);
		Map<String, Object> result = new HashMap<>();

		if (data == null || data.isEmpty()) {
			return ResponseEntity.ok(Collections.emptyMap());
		}
		result.put("reviewId", data.get("review_id"));
		result.put("starRate", data.get("star_rate"));
		result.put("message", data.get("message"));
		result.put("reviewStatus", data.get("review_status"));
		result.put("orderItemListId", data.get("order_item_list_id"));
		result.put("userId", data.get("user_id"));
		result.put("date", data.get("date"));
		result.put("productListId", data.get("product_list_id"));

		return ResponseEntity.ok(result);

	}

}