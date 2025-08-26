package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.user.CustomerFeedback;
import com.example.kkBazar.repository.user.CustomerFeedbackRepository;

@Service
public class CustomerFeedbackService {

	@Autowired
	private CustomerFeedbackRepository customerFeedbackRepository;
	
	
	public List<CustomerFeedback> listAll() {
		return this.customerFeedbackRepository.findAll();
	}

	// save
	public CustomerFeedback SaveCustomerFeedback(CustomerFeedback customerFeedback) {
		return customerFeedbackRepository.save(customerFeedback);
	}

	public CustomerFeedback findCustomerFeedbackById(Long id) {
		return customerFeedbackRepository.findById(id).get();
	}

	// delete
	public void deleteCustomerFeedbackById(Long id) {
		customerFeedbackRepository.deleteById(id);
	}
	
	
}
