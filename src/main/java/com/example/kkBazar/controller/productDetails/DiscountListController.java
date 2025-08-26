package com.example.kkBazar.controller.productDetails;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.productDetails.DiscountList;
import com.example.kkBazar.repository.productDetails.DiscountListRepository;

@RestController
@CrossOrigin
public class DiscountListController {
	@Autowired
	private DiscountListRepository discountListRepository;
	
//	@PostMapping("/discount/save")
//	public ResponseEntity<?> saveDiscountDetails(@RequestBody DiscountList discountList) {
//		try {
//
//			discountListRepository.save(discountList);
//			long id = discountList.getDiscountListId();
//			return ResponseEntity.status(HttpStatus.OK).body("Discount details saved successfully." + id);
//		} catch (Exception e) {
//			String errorMessage = "An error occurred while saving Discount details.";
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//		}
//	}
}
