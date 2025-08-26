package com.example.kkBazar.service.productDetails;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.productDetails.AddStock;
import com.example.kkBazar.repository.productDetails.AddStockRepository;

@Service
public class AddStockService {

	
	@Autowired
	private AddStockRepository addStockRepository;
	

	public List<AddStock> listAll() {
		return this.addStockRepository.findAll();
	}

	// save
	public AddStock SaveAddStock(AddStock addStock) {
		return addStockRepository.save(addStock);
	}

	public AddStock findAddStockById(Long id) {
		return addStockRepository.findById(id).get();
	}

	// delete
	public void deleteAddStockById(Long id) {
		addStockRepository.deleteById(id);
	}
	
}
