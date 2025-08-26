package com.example.kkBazar.controller.productDetails;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.productDetails.AddStock;
import com.example.kkBazar.entity.productDetails.AddStockList;
import com.example.kkBazar.repository.addProduct.ProductListRepository;
import com.example.kkBazar.repository.productDetails.AddStockRepository;
import com.example.kkBazar.service.productDetails.AddStockService;

@RestController
@CrossOrigin
public class AddStockController {

	@Autowired
	private AddStockService addStockService;
	@Autowired
	private ProductListRepository productListRepository;
	@Autowired
	private AddStockRepository addStockRepository;

	@GetMapping("/productStockDetail")
	public ResponseEntity<?> getStockDetails(@RequestParam(required = true) String stock) {
		try {
			if ("stockDetails".equals(stock)) {
				Iterable<AddStock> stockDetails = addStockService.listAll();
				return new ResponseEntity<>(stockDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided stock is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving product stock details: " + e.getMessage());
		}
	}

	@PostMapping("/Stock/saves")
	public ResponseEntity<?> saveStockDetails(@RequestBody AddStock addStock) {
		try {
			addStock.setCreatedAt(new Date(System.currentTimeMillis()));
			addStockService.SaveAddStock(addStock);
			long id = addStock.getStockId();
			return ResponseEntity.status(HttpStatus.OK).body("Product Stock details saved successfully." + id);
		} catch (Exception e) {
			String errorMessage = "An error occurred while saving Product Stock details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PostMapping("/Stock/save")
	private ResponseEntity<String> saveBook(@RequestBody AddStock addStock) {
		try {
			addStock.setCreatedAt(new Date(System.currentTimeMillis()));
			addStock.setDate(LocalDate.now());
			List<Long> addStockIdList = addStock.getAddStockList().stream().map(AddStockList::getProductListId)
					.collect(Collectors.toList());

			List<ProductList> productList = productListRepository.findAllById(addStockIdList);

			for (ProductList product : productList) {
				for (AddStockList addStockItem : addStock.getAddStockList()) {
					if (product.getProductListId().equals(addStockItem.getProductListId())) {
						product.setQuantity(Objects.nonNull(product.getQuantity())
								? product.getQuantity() + addStockItem.getQuantity()
								: addStockItem.getQuantity());

						product.setStockIn(Objects.nonNull(product.getStockIn())
								? product.getStockIn() + addStockItem.getQuantity()
								: addStockItem.getQuantity());
					}
				}
			}

			productListRepository.saveAll(productList);

			addStockService.SaveAddStock(addStock);

			return ResponseEntity.status(HttpStatus.OK).body("Product Stock details saved successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving product stock details.");
		}
	}

	@DeleteMapping("/Stock/delete/{addStockId}")
	public ResponseEntity<String> deleteStock(@PathVariable Long addStockId) {
		try {
			AddStock addStock = addStockService.findAddStockById(addStockId);

			if (addStock != null) {
				List<Long> addStockIdList = addStock.getAddStockList().stream().map(AddStockList::getProductListId)
						.collect(Collectors.toList());

				List<ProductList> productList = productListRepository.findAllById(addStockIdList);

				for (ProductList product : productList) {
					for (AddStockList addStockItem : addStock.getAddStockList()) {
						if (product.getProductListId().equals(addStockItem.getProductListId())) {
							product.setQuantity(Objects.nonNull(product.getQuantity())
									? product.getQuantity() - addStockItem.getQuantity()
									: 0);
						}
					}
				}
				productListRepository.saveAll(productList);
				addStockService.deleteAddStockById(addStockId);
				return ResponseEntity.status(HttpStatus.OK).body("Product Stock details deleted successfully.");
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("AddStock with ID " + addStockId + " not found.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error deleting product stock details.");
		}
	}

//	@GetMapping("/productStockDetail")
//	public ResponseEntity<?> getStockDetail(@RequestParam(required = true) String stock) {
//		try {
//			if ("stockDetails".equals(stock)) {
//				Iterable<Map<String, Object>> stockDetails = addStockRepository.getProdustStockDetails();
//				return new ResponseEntity<>(stockDetails, HttpStatus.OK);
//			} else {
//				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided stock is not supported.");
//			}
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body("Error retrieving product stock details: " + e.getMessage());
//		}
//	}
	@GetMapping("/productStockDetails")
	public ResponseEntity<Object> getAllProducts(@RequestParam(required = true) String stock) {
		if ("stockDetails".equals(stock)) {
			List<Map<String, Object>> mainStockList = new ArrayList<>();
			List<Map<String, Object>> stockRole = addStockRepository.getProdustStockDetails();
			Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> stockGroupMap = stockRole
			        .stream()
			        .collect(Collectors.groupingBy(
			                action -> Objects.toString(action.get("stock_id"), "UNKNOWN"),
			                Collectors.groupingBy(
			                        action -> Objects.toString(action.get("stock_list_id"), "UNKNOWN"),
			                        Collectors.groupingBy(
			                                action -> Objects.toString(action.get("product_list_id"), "UNKNOWN"),
			                                Collectors.groupingBy(
			                                        action -> Objects.toString(action.get("product_varient_id"), "UNKNOWN")
			                                )
			                        )
			                )
			        ));

			for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> stockLoop : stockGroupMap
					.entrySet()) {
				Map<String, Object> stockMap = new HashMap<>();
				stockMap.put("stockId", stockLoop.getKey());

				List<Map<String, Object>> stockList = new ArrayList<>();
				for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> stockListLoop : stockLoop
						.getValue().entrySet()) {
					Map<String, Object> stockListMap = new HashMap<>();
					stockListMap.put("stockListId", stockListLoop.getKey());

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Map.Entry<String, Map<String, List<Map<String, Object>>>> productListLoop : stockListLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						productListMap.put("productListId", productListLoop.getKey());

						List<Map<String, Object>> productVariantList = new ArrayList<>();
						for (Map.Entry<String, List<Map<String, Object>>> productVariantLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> productVariantMap = new HashMap<>();
							productVariantMap.put("productVarientId", productVariantLoop.getKey());
							stockMap.put("productId", productVariantLoop.getValue().get(0).get("product_id"));
							stockListMap.put("productId", productVariantLoop.getValue().get(0).get("product_id"));
							stockListMap.put("productName", productVariantLoop.getValue().get(0).get("product_name"));
							stockListMap.put("quantity", productVariantLoop.getValue().get(0).get("quantity"));
							stockListMap.put("productQuantity",
									productVariantLoop.getValue().get(0).get("productQuantity"));
							stockListMap.put("varientValue", productVariantLoop.getValue().get(0).get("varient_value"));
							stockListMap.put("varientName", productVariantLoop.getValue().get(0).get("varient_name"));
							stockListMap.put("productListId",
									productVariantLoop.getValue().get(0).get("product_list_id"));
							stockListMap.put("productVarientId",
									productVariantLoop.getValue().get(0).get("product_varient_id"));
							productVariantList.add(productVariantMap);
						}
						productList.add(productListMap);
					}

					stockList.add(stockListMap);
				}
				stockMap.put("stockList", stockList);
				mainStockList.add(stockMap);
			}

			return ResponseEntity.ok(mainStockList);
		} else {
			String errorMessage = "Invalid value for 'stock'. Expected 'stockDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	// Find StockList Details By CurrentDate
	@GetMapping("/findStockListDetailsByCurrentDate")
	public ResponseEntity<?> findDiscountListByCurrentDate(@RequestParam String stock) {
		try {
			if ("stockListDetails".equals(stock)) {
				List<Map<String, Object>> stockListDetails = addStockRepository.findStockListDetailsByCurrentDate();

				List<Map<String, Object>> modifiedStockDetails = stockListDetails.stream().map(stockListDetail -> {
					Map<String, Object> result = new HashMap<>();
					result.put("quantity", stockListDetail.get("quantity"));
					result.put("stockListId", stockListDetail.get("stock_list_id"));
					result.put("stockId", stockListDetail.get("stock_id"));
					result.put("date", stockListDetail.get("date"));
					result.put("productListId", stockListDetail.get("product_list_id"));
					result.put("productQuantity", stockListDetail.get("product_quantity"));
					result.put("alertQuantity", stockListDetail.get("alert_quantity"));
					result.put("buyRate", stockListDetail.get("buy_rate"));

					result.put("gst", stockListDetail.get("gst"));
					result.put("gstTaxAmount", stockListDetail.get("gst_tax_amount"));
					result.put("mrp", stockListDetail.get("mrp"));
					result.put("sellRate", stockListDetail.get("sell_rate"));
					result.put("totalAmount", stockListDetail.get("total_amount"));
					result.put("productId", stockListDetail.get("product_id"));
					result.put("productName", stockListDetail.get("product_name"));
					result.put("productListId", stockListDetail.get("product_list_id"));
					result.put("productVarientId", stockListDetail.get("product_varient_id"));
					result.put("varientName", stockListDetail.get("varient_name"));
					result.put("varientValue", stockListDetail.get("varient_value"));

					result.put("productVarientImagesId", stockListDetail.get("product_varient_images_id"));
					result.put("productImagesId", stockListDetail.get("productImagesId"));
					result.put("productImageUrl", "product/" + generateRandomNumber() + "/"
							+ stockListDetail.get("productImagesId"));
					return result;
				}).collect(Collectors.toList());

				return new ResponseEntity<>(modifiedStockDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided stockList is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving stockList details: " + e.getMessage());
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}
	
	@GetMapping("/StockListDetailByCurrentDate")
	public ResponseEntity<?> getStockDetail2(@RequestParam(required = true) String stock) {
		try {
			if ("stockListDetail".equals(stock)) {
				Iterable<Map<String, Object>> stockDetails = addStockRepository.findStockListDetailByCurrentDate();
				return new ResponseEntity<>(stockDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The provided stock is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving product stock details: " + e.getMessage());
		}
	}

//	@PostMapping("/findStockListByDateRange")
//	public ResponseEntity<List<Map<String, Object>>> findStockListByDateRange1(
//			@RequestBody Map<String, Object> requestBody) {
//		if (!requestBody.containsKey("choose")) {
//			return ResponseEntity.badRequest().build();
//		}
//		String choose = requestBody.get("choose").toString();
//		switch (choose) {
//		case "date":
//			if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
//				LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(),
//						DateTimeFormatter.ISO_DATE);
//				LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
//				List<Map<String, Object>> stockList = addStockRepository.findStockListBetweenDate(startDate, endDate);
//				List<Map<String, Object>> modifiedStockDetails = new ArrayList<>();
//				for (Map<String, Object> stockDetail : stockList) {
//					int randomNumber = generateRandomNumber();
//					String imageUrl = "varient/" + randomNumber + "/" + stockDetail.get("productVarientImagesId");
//					Map<String, Object> modifiedDetail = new HashMap<>(stockDetail);
//					modifiedDetail.put("productVarientImageUrl", imageUrl);
//					modifiedStockDetails.add(modifiedDetail);
//				}
//				return ResponseEntity.ok(modifiedStockDetails);
//			}
//			break;
//		case "month":
//			if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
//				String month = requestBody.get("monthName").toString();
//				String year = requestBody.get("year").toString();
//				List<Map<String, Object>> stockList = addStockRepository.findStockListByMonthYear(month, year);
//				List<Map<String, Object>> modifiedStockDetails = new ArrayList<>();
//				for (Map<String, Object> stockDetail : stockList) {
//					int randomNumber = generateRandomNumber();
//					String imageUrl = "varient/" + randomNumber + "/" + stockDetail.get("productVarientImagesId");
//					Map<String, Object> modifiedDetail = new HashMap<>(stockDetail);
//					modifiedDetail.put("productVarientImageUrl", imageUrl);
//					modifiedStockDetails.add(modifiedDetail);
//				}
//				return ResponseEntity.ok(modifiedStockDetails);
//			}
//			break;
//		default:
//			return ResponseEntity.badRequest().build();
//		}
//		return ResponseEntity.badRequest().build();
//	}
//	
	
	@PostMapping("/findStockListByDateRange")
	public ResponseEntity<List<Map<String, Object>>> findStockListByDateRange1(@RequestBody Map<String, Object> requestBody) {
	    if (!requestBody.containsKey("choose")) {
	        return ResponseEntity.badRequest().build();
	    }
	    
	    String choose = requestBody.get("choose").toString();
	    
	    switch (choose) {
	        case "date":
	            if (requestBody.containsKey("startDate") && requestBody.containsKey("endDate")) {
	                LocalDate startDate = LocalDate.parse(requestBody.get("startDate").toString(), DateTimeFormatter.ISO_DATE);
	                LocalDate endDate = LocalDate.parse(requestBody.get("endDate").toString(), DateTimeFormatter.ISO_DATE);
	                List<Map<String, Object>> stockList = addStockRepository.findStockListBetweenDate(startDate, endDate);
	                return buildStockResponse(stockList);
	            }
	            break;
	        case "month":
	            if (requestBody.containsKey("year") && requestBody.containsKey("monthName")) {
	                String month = requestBody.get("monthName").toString();
	                String year = requestBody.get("year").toString();
	                List<Map<String, Object>> stockList = addStockRepository.findStockListByMonthYear(month, year);
	                return buildStockResponse(stockList);
	            }
	            break;
	        default:
	            return ResponseEntity.badRequest().build();
	    }
	    
	    return ResponseEntity.badRequest().build();
	}

	private ResponseEntity<List<Map<String, Object>>> buildStockResponse(List<Map<String, Object>> stockList) {
	    Set<String> uniqueStockIds = new HashSet<>();
	    List<Map<String, Object>> uniqueStockList = new ArrayList<>();
	    
	    for (Map<String, Object> item : stockList) {
	        Object orderItemIdObj = item.get("orderItemId");
	        String stockListId = orderItemIdObj != null ? orderItemIdObj.toString() : "UNKNOWN";
	        
	        if (!uniqueStockIds.contains(stockListId)) {
	            uniqueStockIds.add(stockListId);
	            Map<String, Object> result = new HashMap<>();
	            
	            Object productImagesIdObj = item.get("productImagesId");
	            String productImagesId = productImagesIdObj != null ? productImagesIdObj.toString() : "UNKNOWN";
	            result.put("productImageUrl", "product/" + generateRandomNumber() + "/" + productImagesId);
	            
	            result.putAll(item);
	            uniqueStockList.add(result);
	        }
	    }
	    
	    return ResponseEntity.ok(uniqueStockList);
	}



	private String getFileExtensionForImage(List<Map<String, Object>> stockDetails) {
		Map<String, Object> imageDetails = stockDetails.get(0);
		if (imageDetails == null || imageDetails.get("url") == null || imageDetails.get("url").toString().isEmpty()) {
			return "jpg";
		}
		String url = imageDetails.get("url").toString();
		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}
	@GetMapping("/outOfStock/details")
	public ResponseEntity<?> getOutOfStockDetails() {
		try {
			List<Map<String, Object>> stockDetails = addStockRepository.getOutOfStockDetails();
			List<Map<String, Object>> stockMapList = new ArrayList<>();

			for (Map<String, Object> productDetails : stockDetails) {
				Map<String, Object> productMap = new HashMap<>();

				productMap.put("productImagesUploadUrl",
						"product/" + generateRandomNumber() + "/" + productDetails.get("product_images_id"));

				
				productMap.put("productId", productDetails.get("product_id"));
				productMap.put("productName", productDetails.get("product_name"));
				productMap.put("productListId", productDetails.get("product_list_id"));
				productMap.put("quantity", productDetails.get("quantity"));
				productMap.put("productImagesId", productDetails.get("product_images_id"));
				productMap.put("brandId", productDetails.get("brand_id"));
				productMap.put("brandname", productDetails.get("brand_name"));
				productMap.put("alertQuantity", productDetails.get("alert_quantity"));
				stockMapList.add(productMap);
			}

			return ResponseEntity.ok(stockMapList);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
