package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.OrderReturn;
import com.example.kkBazar.repository.user.OrderReturnRepository;

@Service
public class OrderReturnService {

	@Autowired
	private OrderReturnRepository orderReturnRepository;
	
	//view
		public List<OrderReturn> listAll() {
			return this.orderReturnRepository.findAll();
		}

	//save
		public OrderReturn SaveOrderReturnDetails(OrderReturn orderReturn) {
			return orderReturnRepository.save(orderReturn);
		}

		// edit
		public OrderReturn findById(Long orderReturnId) {
			return orderReturnRepository.findById(orderReturnId).get();
		}

		// delete
		public void deleteOrderReturnId(Long id) {
			orderReturnRepository.deleteById(id);
		}
	
}
