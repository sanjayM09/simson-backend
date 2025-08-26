package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.BankDetails;
import com.example.kkBazar.entity.user.OrderRefund;
import com.example.kkBazar.repository.user.BankDetailsRepository;
import com.example.kkBazar.repository.user.OrderRefundRepository;

@Service
public class OrderRefundService {
	
	@Autowired
	private OrderRefundRepository bankRepo;

//view
	public Iterable<OrderRefund> listAll() {
		return this.bankRepo.findAll();
	}

//save
	public OrderRefund SaveOrderReturnDetails(OrderRefund bank) {
		return bankRepo.save(bank);
	}

	// edit
	public OrderRefund findById(Long bankId) {
		return bankRepo.findById(bankId).get();
	}

	// delete
	public void deleteBankId(Long id) {
		bankRepo.deleteById(id);
	}

}
