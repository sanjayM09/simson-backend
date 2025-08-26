package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.Review;
import com.example.kkBazar.repository.user.ReviewRepository;

@Service
public class ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
	//view
			public List<Review> listAll() {
				return this.reviewRepository.findAll();
			}

		//save
			public Review SaveReview(Review review) {
				return reviewRepository.save(review);
			}

			// edit
			public Review findById(Long reviewId) {
				return reviewRepository.findById(reviewId).get();
			}
	
}
