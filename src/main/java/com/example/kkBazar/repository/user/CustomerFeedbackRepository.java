package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.user.CustomerFeedback;

public interface CustomerFeedbackRepository extends JpaRepository<CustomerFeedback, Long> {

	
	@Query(value="select c. customer_feedback_id, u.user_id, u.user_name,"
			+ " cl.customer_feedback_list_id, cl.date, cl.description, cl.url,cl.product_list_id,cl.star_rate"
			+ " from customer_feedback as c"
			+ " join customer_feedback_list as cl on cl.customer_feedback_id=c.customer_feedback_id"
			+ " join user as u on u.user_id=c.user_id", nativeQuery = true)
	
	List<Map<String, Object>>getFeedbackDetails();
}
