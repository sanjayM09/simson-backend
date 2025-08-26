package com.example.kkBazar.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.CustomerFeedbackList;
import com.example.kkBazar.repository.user.CustomerFeedbackListRepository;

@Service
public class CustomerFeedbackListService {

	
	@Autowired
	private CustomerFeedbackListRepository customerFeedbackListRepository;
	
	public CustomerFeedbackList findCustomerFeedbackListById(Long id) {
		return customerFeedbackListRepository.findById(id).get();
	}

}
