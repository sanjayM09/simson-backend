package com.example.kkBazar.service.addProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.addProduct.SubCategory;
import com.example.kkBazar.repository.addProduct.SubCategoryRepository;

@Service
public class SubCategoryService {
@Autowired
private SubCategoryRepository subCategoryRepository;

//view
	public List<SubCategory> listSubCategory() {
		return this.subCategoryRepository.findAll();
	}

	// save
	public SubCategory SaveSubCategoryDetails(SubCategory subCategory) {
		return subCategoryRepository.save(subCategory);
	}

	public SubCategory findSubCategoryById(Long subCategoryId) {
		return subCategoryRepository.findById(subCategoryId).get();
	}

	// delete
	public void deleteSubCategoryById(Long id) {
		subCategoryRepository.deleteById(id);
	}


	
}
