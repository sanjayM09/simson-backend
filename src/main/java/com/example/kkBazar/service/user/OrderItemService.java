package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.OrderItem;
import com.example.kkBazar.repository.user.OrderItemRepository;

@Service
public class OrderItemService {

	@Autowired
	private OrderItemRepository orderItemRepository;
	
	//view
		public List<OrderItem> listAll() {
			return this.orderItemRepository.findAll();
		}

	//save
		public OrderItem SaveOrderItemDetails(OrderItem orderItem) {
			return orderItemRepository.save(orderItem);
		}

		// edit
		public OrderItem findById(Long orderItemId) {
			return orderItemRepository.findById(orderItemId).get();
		}

		// delete
		public void deleteOrderItemId(Long id) {
			orderItemRepository.deleteById(id);
		}
		
		//edit
		public OrderItem getByUserId(Long userId) {
			return orderItemRepository.findById(userId).get();
		}
		
		
}
