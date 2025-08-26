package com.example.kkBazar.service.addProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.addProduct.Unit;
import com.example.kkBazar.repository.addProduct.UnitRepository;

@Service
public class UnitService {

	@Autowired
	private UnitRepository unitRepository;
	
	public List<Unit> listUnit() {
		return this.unitRepository.findAll();
	}

	// save
	public Unit SaveUnitDetails(Unit unit) {
		return unitRepository.save(unit);
	}

	public Unit findUnitById(Long brandId) {
		return unitRepository.findById(brandId).get();
	}

	// delete
	public void deleteUnitById(Long id) {
		unitRepository.deleteById(id);
	}
}
