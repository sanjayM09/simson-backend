package com.example.kkBazar.controller.addProduct;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.addProduct.SubCategory;
import com.example.kkBazar.service.addProduct.SubCategoryService;

@RestController
@CrossOrigin
public class SubCategoryController {
	@Autowired
	private SubCategoryService subCategoryService;

	@GetMapping("/subCategory/view")
	public ResponseEntity<Object> getSubCategoryDetails(@RequestParam(required = true) String subCategory) {
		if ("subCategoryDetails".equals(subCategory)) {
			return ResponseEntity.ok(subCategoryService.listSubCategory());
		} else {
			String errorMessage = "Invalid value for 'subCategory'. Expected 'subCategoryDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/subCategory/save")
	public ResponseEntity<String> saveSubCategoryDetails(@RequestBody SubCategory subCategory) {
		try {
			subCategory.setCreatedAt(new Date(System.currentTimeMillis()));
			subCategoryService.SaveSubCategoryDetails(subCategory);
			long id = subCategory.getSubCategoryId();
			return ResponseEntity.ok("SubCategory Details saved successfully. SubCategory ID: " + id);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving subCategory: " + e.getMessage());
		}
	}

	@PutMapping("/subCategory/edit/{id}")
	public ResponseEntity<SubCategory> updateSubCategory(@PathVariable("id") Long subCategoryId,
			@RequestBody SubCategory subCategory) {
		try {
			SubCategory existingSubCategory = subCategoryService.findSubCategoryById(subCategoryId);

			if (existingSubCategory == null) {
				return ResponseEntity.notFound().build();
			}
			existingSubCategory.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingSubCategory.setSubCategoryName(subCategory.getSubCategoryName());

			subCategoryService.SaveSubCategoryDetails(existingSubCategory);
			return ResponseEntity.ok(existingSubCategory);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/subCategory/delete/{id}")
	public ResponseEntity<String> deleteSubCategoryId(@PathVariable("id") Long subCategoryId) {
		subCategoryService.deleteSubCategoryById(subCategoryId);
		return ResponseEntity.ok("SubCategory deleted successfully With Id :" + subCategoryId);

	}

}
