package com.example.kkBazar.service.productDetails;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.productDetails.Discount;
import com.example.kkBazar.repository.productDetails.DiscountRepository;

@Service

public class DiscountService {

	@Autowired
	private DiscountRepository discountRepository;
	
	public List<Discount> listAll() {
		return this.discountRepository.findAll();
	}

	// save
	public Discount SaveDiscount(Discount discount) {
		return discountRepository.save(discount);
	}

	public Discount findDiscountById(Long id) {
		return discountRepository.findById(id).get();
	}

	// delete
	public void deleteDiscountById(Long id) {
		discountRepository.deleteById(id);
	}
	
}
