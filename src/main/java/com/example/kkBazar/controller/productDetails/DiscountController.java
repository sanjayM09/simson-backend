package com.example.kkBazar.controller.productDetails;

import java.sql.Date;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

import com.example.kkBazar.entity.productDetails.Discount;
import com.example.kkBazar.entity.productDetails.DiscountList;
import com.example.kkBazar.repository.productDetails.DiscountRepository;
import com.example.kkBazar.service.addProduct.ProductListService;
import com.example.kkBazar.service.productDetails.DiscountService;

@RestController
@CrossOrigin
public class DiscountController {

	@Autowired
	private DiscountService discountService;

	@Autowired
	private ProductListService productListService;

	@Autowired
	private DiscountRepository discountRepository;

	@GetMapping("/discountDetails")
	public ResponseEntity<?> getDiscountDetails(@RequestParam(required = true) String discount) {
		try {
			if ("discountDetails".equals(discount)) {
				Iterable<Discount> discountDetails = discountService.listAll();
				return new ResponseEntity<>(discountDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided discount is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving discount details: " + e.getMessage());
		}
	}

	@GetMapping("/discountDetail/view")
	public ResponseEntity<Object> getAllDiscountDetails(@RequestParam(required = true) String discount) {
		if ("discountDetails".equals(discount)) {
			List<Map<String, Object>> mainDiscountList = new ArrayList<>();
			List<Map<String, Object>> discountRole = discountRepository.getAllDiscountDetails1();

			Map<String, Map<String, Map<String, List<Map<String, Object>>>>> discountGroupMap = discountRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("discount_id").toString(),
							Collectors.groupingBy(action -> action.get("discount_list_id").toString(),
									Collectors.groupingBy(action -> action.get("product_list_id").toString()))));

			for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> discountLoop : discountGroupMap
					.entrySet()) {
				Map<String, Object> discountMap = new HashMap<>();
				discountMap.put("discountId", discountLoop.getKey());

				List<Map<String, Object>> discountList = new ArrayList<>();
				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> discountListLoop : discountLoop
						.getValue().entrySet()) {
					Map<String, Object> discountListMap = new HashMap<>();
					discountListMap.put("discountListId", discountListLoop.getKey());

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Map.Entry<String, List<Map<String, Object>>> productListLoop : discountListLoop.getValue()
							.entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						productListMap.put("productListId", productListLoop.getKey());

						discountMap.put("discountTitle", productListLoop.getValue().get(0).get("discount_title"));
						discountMap.put("discountPercentage",
								productListLoop.getValue().get(0).get("discount_percentage"));
						discountMap.put("startDate", productListLoop.getValue().get(0).get("start_date"));
						discountMap.put("endDate", productListLoop.getValue().get(0).get("end_date"));
						discountListMap.put("productListId", productListLoop.getKey());
						discountMap.put("productId", productListLoop.getValue().get(0).get("product_id"));
						discountMap.put("productName", productListLoop.getValue().get(0).get("product_name"));
						discountListMap.put("discountAmount", productListLoop.getValue().get(0).get("discount_amount"));
						discountListMap.put("discountPercentage",
								productListLoop.getValue().get(0).get("discount_percentage"));
						discountListMap.put("gst", productListLoop.getValue().get(0).get("gst"));
						discountListMap.put("mrp", productListLoop.getValue().get(0).get("mrp"));
						discountListMap.put("gstTaxAmount", productListLoop.getValue().get(0).get("gst_tax_amount"));
						discountListMap.put("sellRate", productListLoop.getValue().get(0).get("sell_rate"));
						discountListMap.put("totalAmount", productListLoop.getValue().get(0).get("total_amount"));
						discountListMap.put("buyRate", productListLoop.getValue().get(0).get("buy_rate"));
						productList.add(productListMap);
					}

					discountList.add(discountListMap);
				}

				discountMap.put("discountList", discountList);
				mainDiscountList.add(discountMap);
			}

			return ResponseEntity.ok(mainDiscountList);
		} else {
			String errorMessage = "Invalid value for 'discount'. Expected 'discountDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@PostMapping("/discount/save")
	public ResponseEntity<?> saveDiscountDetails(@RequestBody Discount discount) {
		try {
			discount.setCreatedAt(new Date(System.currentTimeMillis()));
			List<DiscountList> discountList = discount.getDiscountList();
			double discountPercentage = discount.getDiscountPercentage();
			Date startDate = discount.getStartDate();
			Date endDate = discount.getEndDate();
			for (DiscountList discountLoop : discountList) {
				discountLoop.setStatus(true);
				discountLoop.setStartDate(startDate);
				discountLoop.setEndDate(endDate);
				discountLoop.setDiscountPercentage(discountPercentage);

				double mrp = discountLoop.getMrp();
				double discountPercentageAmount = (discountPercentage / 100) * mrp;
				DecimalFormat df = new DecimalFormat("#.##");
				discountPercentageAmount = Double.parseDouble(df.format(discountPercentageAmount));
				discountLoop.setDiscountAmount(discountPercentageAmount);

				double calculatedAmount = mrp - discountPercentageAmount;
				discountLoop.setTotalAmount(calculatedAmount);

			}
			discountService.SaveDiscount(discount);

			long id = discount.getDiscountId();
			return ResponseEntity.status(HttpStatus.OK).body("Discount details saved successfully." + id);
		} catch (Exception e) {
			e.printStackTrace();
			String errorMessage = "An error occurred while saving Discount details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PutMapping("/discount/edit/{discountId}")
	public ResponseEntity<Discount> updateDiscountId(@PathVariable("discountId") Long DiscountId,
			@RequestBody Discount discount) {
		try {
			Discount existingDiscount = discountService.findDiscountById(DiscountId);
			if (existingDiscount == null) {
				return ResponseEntity.notFound().build();
			}
			existingDiscount.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingDiscount.setDiscountTitle(discount.getDiscountTitle());
			existingDiscount.setDiscountList(discount.getDiscountList());
			discountService.SaveDiscount(existingDiscount);
			return ResponseEntity.ok(existingDiscount);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@DeleteMapping("/discount/delete/{discountId}")
	public ResponseEntity<String> deleteDiscountDetail(@PathVariable("discountId") Long discountId) {
		discountService.deleteDiscountById(discountId);
		return ResponseEntity.ok("Discount details deleted successfully");
	}

	@GetMapping("/discountDetails/view")
	public ResponseEntity<?> getDiscountDetails1(@RequestParam(required = true) String discount) {
		try {
			if ("discountDetails".equals(discount) || "discountDetail".equals(discount)) {
				List<Map<String, Object>> discountList;

				if ("discountDetail".equals(discount)) {
					discountList = discountRepository.getAllDiscountDetails();
				} else {
					discountList = discountRepository.getAllDiscountDetail();
				}

				return new ResponseEntity<>(discountList, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided discount is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving discount details: " + e.getMessage());
		}
	}

// Discount Details By CurrentDate
	@GetMapping("/findDiscountListByCurrentDate")
	public ResponseEntity<?> findDiscountListByCurrentDate(@RequestParam String discount) {
		try {
			if ("discountDetails".equals(discount)) {
				List<Map<String, Object>> discountDetails = discountRepository.findDiscountListByCurrentDate();

				List<Map<String, Object>> modifiedDiscountDetails = discountDetails.stream().map(DiscountListDetail -> {
					Map<String, Object> result = new HashMap<>();
					result.put("discountId", DiscountListDetail.get("discount_id"));
					result.put("discountTitle", DiscountListDetail.get("discount_title"));
					result.put("discountPercentage", DiscountListDetail.get("discount_percentage"));
					result.put("startDate", DiscountListDetail.get("start_date"));
					result.put("endDate", DiscountListDetail.get("end_date"));
					result.put("discountListId", DiscountListDetail.get("discount_list_id"));
					result.put("discountAmount", DiscountListDetail.get("discount_amount"));
					result.put("status", DiscountListDetail.get("status"));
					result.put("buyRate", DiscountListDetail.get("buy_rate"));
					result.put("gst", DiscountListDetail.get("gst"));
					result.put("gstTaxAmount", DiscountListDetail.get("gst_tax_amount"));
					result.put("mrp", DiscountListDetail.get("mrp"));
					result.put("sellRate", DiscountListDetail.get("sell_rate"));
					result.put("totalAmount", DiscountListDetail.get("total_amount"));
					result.put("productId", DiscountListDetail.get("product_id"));
					result.put("productName", DiscountListDetail.get("product_name"));
					result.put("productListId", DiscountListDetail.get("product_list_id"));
					result.put("productVarientId", DiscountListDetail.get("product_varient_id"));
					result.put("varientName", DiscountListDetail.get("varient_name"));
					result.put("varientValue", DiscountListDetail.get("varient_value"));

					result.put("productVarientImagesId", DiscountListDetail.get("product_varient_images_id"));
					result.put("productVarientImageUrl", "varient/" + generateRandomNumber() + "/"
							+ DiscountListDetail.get("product_varient_images_id"));
					return result;
				}).collect(Collectors.toList());

				return new ResponseEntity<>(modifiedDiscountDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided discount is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving discount details: " + e.getMessage());
		}
	}

	@PostMapping("/findDiscountListByDateRange")
	public ResponseEntity<List<Map<String, Object>>> findDiscountListByDateRange1(@RequestBody Map<String, Object> requestBody) {
	    if (!requestBody.containsKey("choose")) {
	        return ResponseEntity.badRequest().build();
	    }
	    
	    String choose = requestBody.get("choose").toString();
	    
	    switch (choose) {
	        case "date":
	            if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
	                LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(), DateTimeFormatter.ISO_DATE);
	                LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
	                List<Map<String, Object>> discountList = discountRepository.findDiscountListBetweenDate(startDate, endDate);
	                return buildDiscountResponse(discountList);
	            }
	            break;
	        case "month":
	            if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
	                String month = requestBody.get("monthName").toString();
	                String year = requestBody.get("year").toString();
	                List<Map<String, Object>> discountList = discountRepository.findDiscountListByMonthYear(month, year);
	                return buildDiscountResponse(discountList);
	            }
	            break;
	        default:
	            return ResponseEntity.badRequest().build();
	    }
	    
	    return ResponseEntity.badRequest().build();
	}

	private ResponseEntity<List<Map<String, Object>>> buildDiscountResponse(List<Map<String, Object>> discountList) {
	    Set<String> uniqueDiscounts = new HashSet<>();
	    List<Map<String, Object>> cartList = new ArrayList<>();
	    
	    for (Map<String, Object> item : discountList) {
	        String discountId = item.get("discountId").toString();
	        if (!uniqueDiscounts.contains(discountId)) {
	            uniqueDiscounts.add(discountId);
	            Map<String, Object> result = new HashMap<>();
	            result.put("productVarientImageUrl", "varient/" + generateRandomNumber() + "/" + item.get("productVarientImagesId"));
	            result.putAll(item);
	            cartList.add(result);
	        }
	    }
	    	    return ResponseEntity.ok(cartList);
	}


	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/currentMonth/Discountdetails")
	public ResponseEntity<?> getCurrentMonthExpiry(@RequestParam(required = true) String currentMonth) {
		try {
			if ("discountDetails".equalsIgnoreCase(currentMonth)) {
				List<Map<String, Object>> discountDetails = discountRepository.getExpirationDiscountDetails();
				List<Map<String, Object>> discountMapList = new ArrayList<>();

				for (Map<String, Object> productDetails : discountDetails) {
					Map<String, Object> productMap = new HashMap<>();

					productMap.put("productImagesUploadUrl", "product/" + generateRandomNumber() + "/"
							+ productDetails.get("product_images_id"));
					productMap.put("discountPercentage", productDetails.get("discount_percentage"));
					productMap.put("productId", productDetails.get("product_id"));
					productMap.put("productName", productDetails.get("product_name"));
					productMap.put("productListId", productDetails.get("product_list_id"));
					productMap.put("discountId", productDetails.get("discount_id"));
					productMap.put("discountTitle", productDetails.get("discount_title"));
					productMap.put("startDate", productDetails.get("start_date"));
					productMap.put("endDate", productDetails.get("end_date"));
					productMap.put("discountListId", productDetails.get("discount_list_id"));
					productMap.put("productImagesId", productDetails.get("product_images_id"));

					discountMapList.add(productMap);
				}

				return ResponseEntity.ok(discountMapList);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
