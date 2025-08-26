package com.example.kkBazar.controller.user;

import java.sql.Date;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.entity.user.OrderReturn;
import com.example.kkBazar.repository.user.OrderIemListRepository;
import com.example.kkBazar.repository.user.OrderReturnRepository;
import com.example.kkBazar.service.user.OrderReturnService;

@RestController
@CrossOrigin
public class OrderReturnController {

	@Autowired
	private OrderReturnService orderReturnService;

	@Autowired
	private OrderReturnRepository orderReturnRepository;

	@Autowired
	private OrderIemListRepository orderItemListRepository;

	@GetMapping("/orderReturn/views")
	public ResponseEntity<?> getOrderReturnDetails(@RequestParam(required = true) String orderReturn) {
		try {
			if ("orderReturnDetails".equals(orderReturn)) {
				Iterable<OrderReturn> orderReturnDetails = orderReturnService.listAll();
				return new ResponseEntity<>(orderReturnDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderReturnDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderReturn details: " + e.getMessage());
		}
	}


	 @PostMapping("/orderReturn/save")
	    public ResponseEntity<Object> saveOrderReturnDetails(@RequestBody OrderReturn orderReturn) {
	        try {
	        	
	        	
	        	orderReturn.setCreatedAt(new Date(System.currentTimeMillis()));
	            List<Map<String, Object>> demo = orderItemListRepository
	                    .getAllRetuenDetails(orderReturn.getOrderItemListId());

	            if (demo.isEmpty()) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Order item details not found.");
	            }

	            Date deliveredDate = (Date) demo.get(0).get("delivered_date");
	            Integer returnCount = (Integer) demo.get(0).get("return_count");

	            Calendar calendar = Calendar.getInstance();
	            calendar.setTime(deliveredDate);
	            calendar.add(Calendar.DAY_OF_YEAR, returnCount + 1);

	            Date endDate = new java.sql.Date(calendar.getTime().getTime());
	            Date currentDate = new Date(System.currentTimeMillis());

	            if (currentDate.before(deliveredDate)) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Submission is too early. Allowed after: " + deliveredDate);
	            } else if (currentDate.after(endDate)) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                        .body("Submission window has expired. Allowed until: " + endDate);
	            }

	            orderReturn.setReturnDate(LocalDate.now());
	            orderReturn.setReturnStatus("returnRequestPending");
	            orderReturnService.SaveOrderReturnDetails(orderReturn);

	            Long orderItemListId = orderReturn.getOrderItemListId();
	            OrderItemList orderItemList = orderItemListRepository.findById(orderItemListId).orElse(null);
	            if (orderItemList != null) {
	                orderItemList.setOrderStatus("returnRequestPending");
	                orderItemList.setReturnPending(true);
	                orderItemList.setDelivered(false);
	                orderItemListRepository.save(orderItemList);
	            }

	            orderReturnRepository.save(orderReturn);

	            return ResponseEntity.ok("Your Order return request submitted successfully.");
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body("Failed to submit order return request.");
	        }
	    }
	
	
	 
	@PutMapping("/orderReturn/status/{orderReturnId}")
	public ResponseEntity<?> updateReturnStatus(@PathVariable("orderReturnId") Long orderReturnId,
			@RequestBody OrderReturn requestBody) {

		try {
			OrderReturn existingReturn = orderReturnService.findById(orderReturnId);

			if (existingReturn == null) {
				return ResponseEntity.notFound().build();
			}
			existingReturn.setReasonForReturn(requestBody.getReasonForReturn());
			String previousReturnStatus = existingReturn.getReturnStatus();
			String newReturnStatus = requestBody.getReturnStatus();

			existingReturn.setReturnStatus(newReturnStatus);
			existingReturn.setUpdatedAt(new Date(System.currentTimeMillis()));
			if ("accepted".equals(newReturnStatus)) {
				existingReturn.setAccepted(true);
				existingReturn.setRejected(false);
			} else if ("rejected".equals(newReturnStatus)) {
				existingReturn.setAccepted(false);
				existingReturn.setRejected(true);

			} else {
				existingReturn.setAccepted(false);
				existingReturn.setRejected(false);
			}

			orderReturnService.SaveOrderReturnDetails(existingReturn);

			if (!previousReturnStatus.equals(newReturnStatus)) {
				OrderItemList orderItemList = orderItemListRepository.findById(existingReturn.getOrderItemListId())
						.orElse(null);
				if (orderItemList != null) {

					if ("accepted".equals(newReturnStatus)) {
						orderItemList.setReturnAccepted(true);
						orderItemList.setReturnRejected(false);
						orderItemList.setReturnPending(false);
						orderItemList.setOrderStatus("returnAccepted");
					} else if ("rejected".equals(newReturnStatus)) {
						orderItemList.setReturnAccepted(false);
						orderItemList.setReturnRejected(true);
						orderItemList.setReturnPending(false);
						orderItemList.setOrderStatus("returnRejected");
					} else {
						orderItemList.setReturnAccepted(false);
						orderItemList.setReturnRejected(false);
						orderItemList.setReturnPending(false);
					}
					orderItemListRepository.save(orderItemList);
				}
			}

			return ResponseEntity.ok(existingReturn);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	 
	@PutMapping("/orderReturn/status/cancelled/{orderReturnId}")
	public ResponseEntity<?> updateReturnStatusrrrr(@PathVariable("orderReturnId") Long orderReturnId,
			@RequestBody OrderReturn requestBody) {

		try {
			
			OrderReturn existingReturn = orderReturnService.findById(orderReturnId);

			if (existingReturn == null) {
				return ResponseEntity.notFound().build();
			}
			existingReturn.setReasonForReturn(requestBody.getReasonForReturn());
			String previousReturnStatus = existingReturn.getReturnStatus();
			String newReturnStatus = requestBody.getReturnStatus();

			existingReturn.setReturnStatus(newReturnStatus);

			if ("returnCancelled".equals(newReturnStatus)) {
				existingReturn.setAccepted(false);
				existingReturn.setRejected(false);
				existingReturn.setReturnCancelled(true);
			} else {
				existingReturn.setAccepted(false);
				existingReturn.setRejected(false);
				existingReturn.setReturnCancelled(false);
			}

			orderReturnService.SaveOrderReturnDetails(existingReturn);
			
			if (!previousReturnStatus.equals(newReturnStatus)) {
				OrderItemList orderItemList = orderItemListRepository.findById(existingReturn.getOrderItemListId())
						.orElse(null);
				if (orderItemList != null) {

					if ("returnCancelled".equals(newReturnStatus)) {
						orderItemList.setReturnAccepted(false);
						orderItemList.setReturnRejected(false);
						orderItemList.setReturnPending(false);
						orderItemList.setDelivered(true);					
						orderItemList.setOrderStatus("delivered");
					} else {
						orderItemList.setReturnAccepted(false);
						orderItemList.setReturnRejected(false);
						orderItemList.setReturnPending(false);
						orderItemList.setReturnCancelled(false);
					}
					orderItemListRepository.save(orderItemList);
				}
			}
			return ResponseEntity.ok(existingReturn);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	@GetMapping("/userOrderReturnDetails")
	public ResponseEntity<Object> getUserReturnDetails1(@RequestParam(required = true) String orderReturn) {
		if ("orderReturnDetails".equals(orderReturn)) {
			try {
				List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetails();

				Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> action.get("date").toString(),
										Collectors.groupingBy(action -> action.get("order_return_id").toString()))));

				for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> dateList = new ArrayList<>();
					for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
							.entrySet()) {
						Map<String, Object> dateMap = new HashMap<>();
						dateMap.put("date", dateLoop.getKey());

						List<Map<String, Object>> orderReturnList = new ArrayList<>();
						for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
								.entrySet()) {
							Map<String, Object> orderReturnMap = new HashMap<>();
							orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());

							orderReturnMap.put("orderReturnId",
									orderReturnLoop.getValue().get(0).get("order_return_id"));
							orderReturnMap.put("orderItemListId",
									orderReturnLoop.getValue().get(0).get("order_item_list_id"));
							orderReturnMap.put("accepted", orderReturnLoop.getValue().get(0).get("accepted"));
							orderReturnMap.put("rejected", orderReturnLoop.getValue().get(0).get("rejected"));
							orderReturnMap.put("reasonForReturn",
									orderReturnLoop.getValue().get(0).get("reason_for_return"));
							orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
							orderReturnMap.put("productListId",
									orderReturnLoop.getValue().get(0).get("product_list_id"));
							orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
							orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

							orderReturnList.add(orderReturnMap);

						}

						dateMap.put("ListOfOrderReturnDetails", orderReturnList);
						dateList.add(dateMap);
					}

					userMap.put("userName", userRole.get(0).get("user_name"));
					userMap.put("userDetails", dateList);
					mainOrderReturnList.add(userMap);
				}

				return ResponseEntity.ok(mainOrderReturnList);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user order return details.");
			}
		} else {
			String errorMessage = "Invalid value for 'orderReturn'. Expected 'orderReturnDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/userOrderReturnDetail/{id}")
	public ResponseEntity<Object> getUserReturnDetails(@PathVariable("id") Long userId) {
		try {
			List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
			List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetailsByUserId(userId);

			Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
							Collectors.groupingBy(action -> action.get("date").toString(),
									Collectors.groupingBy(action -> action.get("order_return_id").toString()))));

			for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
					.entrySet()) {
				Map<String, Object> userMap = new HashMap<>();
				userMap.put("userId", userLoop.getKey());

				List<Map<String, Object>> dateList = new ArrayList<>();
				for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
						.entrySet()) {
					Map<String, Object> dateMap = new HashMap<>();
					dateMap.put("date", dateLoop.getKey());

					List<Map<String, Object>> orderReturnList = new ArrayList<>();
					for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
							.entrySet()) {
						Map<String, Object> orderReturnMap = new HashMap<>();
						orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());

						orderReturnMap.put("orderItemListId",
								orderReturnLoop.getValue().get(0).get("order_item_list_id"));
						orderReturnMap.put("accepted", orderReturnLoop.getValue().get(0).get("accepted"));
						orderReturnMap.put("rejected", orderReturnLoop.getValue().get(0).get("rejected"));
						orderReturnMap.put("reasonForReturn",
								orderReturnLoop.getValue().get(0).get("reason_for_return"));
						orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
						orderReturnMap.put("productListId", orderReturnLoop.getValue().get(0).get("product_list_id"));
						orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
						orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

						orderReturnList.add(orderReturnMap);

					}
					dateMap.put("ListOfOrderReturnDetails", orderReturnList);
					dateList.add(dateMap);
				}

				userMap.put("userName", userRole.get(0).get("user_name"));
				userMap.put("userDetails", dateList);
				mainOrderReturnList.add(userMap);
			}

			return ResponseEntity.ok(mainOrderReturnList);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error fetching user order return details.");
		}
	}

	@GetMapping("/orderReturn/view")
	public ResponseEntity<?> getOrderReturnDetails1(@RequestParam(required = true) String orderReturn) {
		try {
			if ("orderReturnDetails".equals(orderReturn)) {
				Iterable<Map<String, Object>> orderReturnDetails = orderReturnRepository.getOrderReturnDetails();
				return new ResponseEntity<>(orderReturnDetails, HttpStatus.OK);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body("The provided orderReturnDetails is not supported.");
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error retrieving orderReturn details: " + e.getMessage());
		}
	}

	@GetMapping("/order/return/details")
	public ResponseEntity<?> getReturnDetails1(@RequestParam(required = true) String orderReturn) {
		try {
			if ("returnDetails".equalsIgnoreCase(orderReturn)) {
				List<Map<String, Object>> returnDetails = orderReturnRepository.getReturnDetails();
				List<Map<String, Object>> orderMapList = new ArrayList<>();

				for (Map<String, Object> productDetails : returnDetails) {
					Map<String, Object> productMap = new HashMap<>();
					productMap.put("productImagesUploadUrl",
							"product/" + generateRandomNumber() + "/" + productDetails.get("product_images_id"));

					productMap.put("productId", productDetails.get("product_id"));
					productMap.put("productName", productDetails.get("product_name"));
					productMap.put("productListId", productDetails.get("product_list_id"));
					productMap.put("quantity", productDetails.get("quantity"));
					productMap.put("productImagesId", productDetails.get("product_images_id"));
					productMap.put("orderReturnId", productDetails.get("order_return_id"));
					productMap.put("returnStatus", productDetails.get("return_status"));
					productMap.put("reasonForReturn", productDetails.get("reason_for_return"));
					productMap.put("date", productDetails.get("date"));
					productMap.put("userId", productDetails.get("user_id"));
					productMap.put("userName", productDetails.get("user_name"));
					productMap.put("mobileNumber", productDetails.get("mobile_number"));
					productMap.put("alternateMobileNumber", productDetails.get("alternate_mobile_number"));
					productMap.put("totalPrice", productDetails.get("total_price"));
					orderMapList.add(productMap);
				}
				return ResponseEntity.ok(orderMapList);
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid parameter value");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/order1/return/{userId}")
	public ResponseEntity<?> getReturnDetails1333(@PathVariable long userId) {
		try {

			List<Map<String, Object>> returnDetails = orderReturnRepository.getUserReturnDetailsByUserIdList(userId);
			List<Map<String, Object>> orderMapList = new ArrayList<>();

			for (Map<String, Object> productDetails : returnDetails) {
				Map<String, Object> productMap = new HashMap<>();
				productMap.put("productImagesUploadUrl",
						"product/" + generateRandomNumber() + "/" + productDetails.get("product_images_id"));

				productMap.put("productId", productDetails.get("product_id"));
				productMap.put("productName", productDetails.get("product_name"));
				productMap.put("productListId", productDetails.get("product_list_id"));
				productMap.put("quantity", productDetails.get("quantity"));
				productMap.put("productImagesId", productDetails.get("product_images_id"));
				productMap.put("orderReturnId", productDetails.get("order_return_id"));
				productMap.put("returnStatus", productDetails.get("return_status"));
				productMap.put("reasonForReturn", productDetails.get("reason_for_return"));
				productMap.put("date", productDetails.get("returnDate"));
				productMap.put("userId", productDetails.get("user_id"));
				productMap.put("userName", productDetails.get("user_name"));
				productMap.put("mobileNumber", productDetails.get("mobile_number"));
				productMap.put("alternateMobileNumber", productDetails.get("alternate_mobile_number"));
				productMap.put("totalPrice", productDetails.get("total_price"));
				orderMapList.add(productMap);
			}
			return ResponseEntity.ok(orderMapList);

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("/order11/return/{userId}")
	public ResponseEntity<?> getCancelledOrderItems(@PathVariable long userId) {
		List<Map<String, Object>> mainList = new ArrayList<>(); // This list will hold the grouped orders

		List<Map<String, Object>> pendingRole = orderReturnRepository.getUserReturnDetailsByUserIdList(userId);
		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));
		for (Map.Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> pendingMap = new HashMap<>();
			List<Map<String, Object>> group = pendingLoop.getValue();
			Map<String, Object> firstItem = group.get(0);

			pendingMap.put("productId", firstItem.get("productId"));
			pendingMap.put("productName", firstItem.get("productName"));
			pendingMap.put("productListId", firstItem.get("productListId"));
			pendingMap.put("quantity", firstItem.get("quantity"));
			pendingMap.put("productImagesId", firstItem.get("productImagesId"));
			pendingMap.put("orderReturnId", firstItem.get("orderReturnId"));
			pendingMap.put("returnStatus", firstItem.get("returnStatus"));
			pendingMap.put("reasonForReturn", firstItem.get("reasonForReturn"));
			pendingMap.put("date", firstItem.get("returnDate"));
			pendingMap.put("userId", firstItem.get("userId"));
			pendingMap.put("userName", firstItem.get("userName"));
			pendingMap.put("mobileNumber", firstItem.get("mobileNumber"));
			pendingMap.put("alternateMobileNumber", firstItem.get("alternateMobileNumber"));
			pendingMap.put("totalPrice", firstItem.get("totalPrice"));

			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "varient/" + randomNumber + "/" + firstItem.get("productVarientImagesId");
			pendingMap.put("productVarientImageUrl", productVarientImageUrl);

			mainList.add(pendingMap);
		}

		return ResponseEntity.ok(mainList);
	}

	@GetMapping("/userOrderReturnDetails1")
	public ResponseEntity<Object> getUserReturnDetails11(@RequestParam(required = true) String orderReturn) {
		if ("orderReturnDetails".equals(orderReturn)) {
			try {
				List<Map<String, Object>> mainOrderReturnList = new ArrayList<>();
				List<Map<String, Object>> userRole = orderReturnRepository.getUserReturnDetails();

				Map<String, Map<String, Map<String, List<Map<String, Object>>>>> userGroupMap = userRole.stream()
						.collect(Collectors.groupingBy(action -> action.get("user_id").toString(),
								Collectors.groupingBy(action -> action.get("date").toString(),
										Collectors.groupingBy(action -> action.get("order_return_id").toString()))));

				for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> userLoop : userGroupMap
						.entrySet()) {
					Map<String, Object> userMap = new HashMap<>();
					userMap.put("userId", userLoop.getKey());

					List<Map<String, Object>> dateList = new ArrayList<>();
					for (Map.Entry<String, Map<String, List<Map<String, Object>>>> dateLoop : userLoop.getValue()
							.entrySet()) {
						Map<String, Object> dateMap = new HashMap<>();
						dateMap.put("date", dateLoop.getKey());

						List<Map<String, Object>> orderReturnList = new ArrayList<>();
						for (Map.Entry<String, List<Map<String, Object>>> orderReturnLoop : dateLoop.getValue()
								.entrySet()) {
							Map<String, Object> orderReturnMap = new HashMap<>();

							orderReturnMap.put("orderReturnId", orderReturnLoop.getKey());
							orderReturnMap.put("orderReturnId",
									orderReturnLoop.getValue().get(0).get("order_return_id"));
							orderReturnMap.put("orderItemListId",
									orderReturnLoop.getValue().get(0).get("order_item_list_id"));
							orderReturnMap.put("productId", orderReturnLoop.getValue().get(0).get("product_id"));
							orderReturnMap.put("productName", orderReturnLoop.getValue().get(0).get("product_name"));
							orderReturnMap.put("reasonForReturn",
									orderReturnLoop.getValue().get(0).get("reason_for_return"));
							orderReturnMap.put("returnStatus", orderReturnLoop.getValue().get(0).get("return_status"));
							orderReturnMap.put("productListId",
									orderReturnLoop.getValue().get(0).get("product_list_id"));
							orderReturnMap.put("quantity", orderReturnLoop.getValue().get(0).get("quantity"));
							orderReturnMap.put("totalPrice", orderReturnLoop.getValue().get(0).get("total_price"));

							orderReturnList.add(orderReturnMap);
						}
						dateMap.put("ListOfOrderReturnDetails", orderReturnList);
						dateList.add(dateMap);
					}
					userMap.put("userName", userRole.get(0).get("user_name"));
					userMap.put("mobileNumber", userRole.get(0).get("mobile_number"));
					userMap.put("alternateMobileNumber", userRole.get(0).get("alternate_mobile_number"));
					userMap.put("userDetails", dateList);
					mainOrderReturnList.add(userMap);
				}
				return ResponseEntity.ok(mainOrderReturnList);
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error fetching user order return details.");
			}

		} else {
			String errorMessage = "Invalid value for 'orderReturn'. Expected 'orderReturnDetails'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/orderReturnStatus/{id}")
	public List<Map<String, Object>> getReturnDetails(@PathVariable(value = "id") Long orderItemListId) {
		return orderReturnRepository.getReturnStatusById(orderItemListId);
	}

	@GetMapping("/orderReturnStatus/{userId}/{orderItemListId}")
	public List<Map<String, Object>> getReturnDetails(@PathVariable(value = "userId") Long userId,
			@PathVariable(value = "orderItemListId") Long orderItemListId) {
		return orderReturnRepository.getReturnStatusById(orderItemListId, userId);
	}

	@GetMapping("/order/return/{userId}")
	public ResponseEntity<?> getAllDataBetweenDates(@PathVariable long userId) {
		List<Map<String, Object>> mainList = new ArrayList<>();
		Map<String, Object> ob = new HashMap<>();

		List<Map<String, Object>> pendingRole = orderReturnRepository.getQualifications(userId);

		List<Map<String, Object>> pendingList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> pendingGroupMap = pendingRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));

		List<Map<String, Object>> deliveredRole = orderReturnRepository.getAllFamilyInformations(userId);

		List<Map<String, Object>> deliveredList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> deliveredGroupMap = deliveredRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));

		List<Map<String, Object>> rejectedRole = orderReturnRepository.getrejectedQualifications(userId);

		List<Map<String, Object>> rejectedList = new ArrayList<>();

		Map<String, List<Map<String, Object>>> rejectedGroupMap = rejectedRole.stream()
				.collect(Collectors.groupingBy(action -> action.get("orderItemListId").toString()));

		for (Entry<String, List<Map<String, Object>>> pendingLoop : pendingGroupMap.entrySet()) {
			Map<String, Object> pendingMap = new HashMap<>();
			pendingMap.put("orderItemListId", pendingLoop.getKey());
			pendingMap.put("productId", pendingLoop.getValue().get(0).get("productId"));
			pendingMap.put("productName", pendingLoop.getValue().get(0).get("productName"));
			pendingMap.put("productListId", pendingLoop.getValue().get(0).get("productListId"));
			pendingMap.put("quantity", pendingLoop.getValue().get(0).get("quantity"));
			pendingMap.put("productImagesId", pendingLoop.getValue().get(0).get("productImagesId"));
			pendingMap.put("orderReturnId", pendingLoop.getValue().get(0).get("orderReturnId"));
			pendingMap.put("returnStatus", pendingLoop.getValue().get(0).get("returnStatus"));
			pendingMap.put("reasonForReturn", pendingLoop.getValue().get(0).get("reasonForReturn"));
			pendingMap.put("date", pendingLoop.getValue().get(0).get("returnDate"));
			pendingMap.put("userId", pendingLoop.getValue().get(0).get("userId"));
			pendingMap.put("userName", pendingLoop.getValue().get(0).get("userName"));
			pendingMap.put("mobileNumber", pendingLoop.getValue().get(0).get("mobileNumber"));
			pendingMap.put("alternateMobileNumber", pendingLoop.getValue().get(0).get("alternateMobileNumber"));
			pendingMap.put("totalPrice", pendingLoop.getValue().get(0).get("totalPrice"));

			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ pendingLoop.getValue().get(0).get("productImagesId");
			pendingMap.put("productImageUrl", productVarientImageUrl);

			pendingList.add(pendingMap);
		}

		for (Entry<String, List<Map<String, Object>>> deliveredLoop : deliveredGroupMap.entrySet()) {
			Map<String, Object> deliveredMap = new HashMap<>();
			deliveredMap.put("orderItemListId", deliveredLoop.getKey());
			deliveredMap.put("productId", deliveredLoop.getValue().get(0).get("productId"));
			deliveredMap.put("productName", deliveredLoop.getValue().get(0).get("productName"));
			deliveredMap.put("productListId", deliveredLoop.getValue().get(0).get("productListId"));
			deliveredMap.put("quantity", deliveredLoop.getValue().get(0).get("quantity"));
			deliveredMap.put("productImagesId", deliveredLoop.getValue().get(0).get("productImagesId"));
			deliveredMap.put("orderReturnId", deliveredLoop.getValue().get(0).get("orderReturnId"));
			deliveredMap.put("returnStatus", deliveredLoop.getValue().get(0).get("returnStatus"));
			deliveredMap.put("reasonForReturn", deliveredLoop.getValue().get(0).get("reasonForReturn"));
			deliveredMap.put("date", deliveredLoop.getValue().get(0).get("returnDate"));
			deliveredMap.put("refundStatus", deliveredLoop.getValue().get(0).get("refundStatus"));
			deliveredMap.put("userId", deliveredLoop.getValue().get(0).get("userId"));
			deliveredMap.put("date", deliveredLoop.getValue().get(0).get("returnDate"));
			deliveredMap.put("refundPending", deliveredLoop.getValue().get(0).get("refundPending"));
			deliveredMap.put("orderRefundId", deliveredLoop.getValue().get(0).get("orderRefundId"));
			deliveredMap.put("refundAccepted", deliveredLoop.getValue().get(0).get("refundAccepted"));
			deliveredMap.put("bankId", deliveredLoop.getValue().get(0).get("bankId"));
			deliveredMap.put("refundDate", deliveredLoop.getValue().get(0).get("refundDate"));
			deliveredMap.put("mobileNumber", deliveredLoop.getValue().get(0).get("mobileNumber"));
			deliveredMap.put("alternateMobileNumber", deliveredLoop.getValue().get(0).get("alternateMobileNumber"));
			deliveredMap.put("totalPrice", deliveredLoop.getValue().get(0).get("totalPrice"));
			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ deliveredLoop.getValue().get(0).get("productImagesId");
			deliveredMap.put("productImageUrl", productVarientImageUrl);

			deliveredList.add(deliveredMap);
		}

		for (Entry<String, List<Map<String, Object>>> rejectedLoop : rejectedGroupMap.entrySet()) {
			Map<String, Object> rejectedMap = new HashMap<>();
			rejectedMap.put("orderItemListId", rejectedLoop.getKey());
			rejectedMap.put("productId", rejectedLoop.getValue().get(0).get("productId"));
			rejectedMap.put("productName", rejectedLoop.getValue().get(0).get("productName"));
			rejectedMap.put("productListId", rejectedLoop.getValue().get(0).get("productListId"));
			rejectedMap.put("quantity", rejectedLoop.getValue().get(0).get("quantity"));
			rejectedMap.put("productImagesId", rejectedLoop.getValue().get(0).get("productImagesId"));
			rejectedMap.put("orderReturnId", rejectedLoop.getValue().get(0).get("orderReturnId"));
			rejectedMap.put("returnStatus", rejectedLoop.getValue().get(0).get("returnStatus"));
			rejectedMap.put("reasonForReturn", rejectedLoop.getValue().get(0).get("reasonForReturn"));
			rejectedMap.put("date", rejectedLoop.getValue().get(0).get("returnDate"));
			rejectedMap.put("userId", rejectedLoop.getValue().get(0).get("userId"));
			rejectedMap.put("userName", rejectedLoop.getValue().get(0).get("userName"));
			rejectedMap.put("mobileNumber", rejectedLoop.getValue().get(0).get("mobileNumber"));
			rejectedMap.put("alternateMobileNumber", rejectedLoop.getValue().get(0).get("alternateMobileNumber"));
			rejectedMap.put("totalPrice", rejectedLoop.getValue().get(0).get("totalPrice"));

			int randomNumber = generateRandomNumber();
			String productVarientImageUrl = "product/" + randomNumber + "/"
					+ rejectedLoop.getValue().get(0).get("productImagesId");
			rejectedMap.put("productImageUrl", productVarientImageUrl);
			rejectedList.add(rejectedMap);
		}

		ob.put("pending", pendingList);
		ob.put("delivered", deliveredList);
		ob.put("rejected", rejectedList);
//	    mainList.add(ob);

		return ResponseEntity.ok(ob);
	}
}
