package com.example.kkBazar.service.addProduct;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import com.example.kkBazar.entity.addProduct.Category;
import com.example.kkBazar.repository.addProduct.CategoryRepository;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;
	
	// view
	public List<Category> listCategory() {
		return this.categoryRepository.findAll();
	}


	// save
	public Category SaveCateggoryDetails(Category category) {
		return categoryRepository.save(category);
	}

	public Category findCategoryById(Long categoryId) {
		return categoryRepository.findById(categoryId).get();
	}

	// delete
	public void deleteCategoryById(Long id) {
		categoryRepository.deleteById(id);
	}


}
