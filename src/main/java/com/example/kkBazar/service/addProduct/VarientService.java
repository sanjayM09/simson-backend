package com.example.kkBazar.service.addProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.addProduct.Varient;
import com.example.kkBazar.repository.addProduct.VarientRepository;

@Service
public class VarientService {
@Autowired
private VarientRepository varientRepository;

//view
public List<Varient> listAll() {
	return this.varientRepository.findAll();
}

//save
public Varient SaveVarientDetails(Varient varient) {
	return varientRepository.save(varient);
}
//edit 
public Varient findByVarientId(long id) {
	return varientRepository.findById(id).get();
}
//delete
	public void deleteByVarient(Long id) {
		this.varientRepository.deleteById(id);
	}
}
