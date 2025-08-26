package com.example.kkBazar.controller.user;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import com.example.kkBazar.entity.user.OrderRefund;

import com.example.kkBazar.repository.user.OrderIemListRepository;
import com.example.kkBazar.repository.user.OrderRefundRepository;
import com.example.kkBazar.service.user.OrderRefundService;


@RestController
@CrossOrigin
public class OrderRefundController {

	@Autowired
	private OrderRefundService orderRefundService;

	@Autowired
	private OrderRefundRepository orderRefundRepository;

	@Autowired
	private OrderIemListRepository orderItemListRepository;

	@GetMapping("/orderrefund/views")
	public ResponseEntity<?> getOrderReturnDetails(@RequestParam(required = true) String OrderRefund) {
		try {
			if ("OrderRefundDetails".equals(OrderRefund)) {
				Iterable<OrderRefund> orderReturnDetails = orderRefundService.listAll();
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

	@PostMapping("/orderRefund/save")
	public ResponseEntity<Object> saveOrderReturnDetails(@RequestBody OrderRefund orderReturn) {

		try {
			orderReturn.setCreatedAt(new Date(System.currentTimeMillis()));
			orderReturn.setRefundDate(LocalDate.now());
			orderReturn.setReturnStatus("refundRequestPending");
			orderRefundService.SaveOrderReturnDetails(orderReturn);
			OrderItemList orderItemList = orderItemListRepository.findById(orderReturn.getOrderItemListId())
					.orElse(null);
			if (orderItemList != null) {
				orderItemList.setOrderStatus("refundRequestPending");
				orderItemList.setReturnPending(true);
				orderItemList.setDelivered(false);
				orderItemListRepository.save(orderItemList);
			}
			return ResponseEntity.ok("Your Order return request submitted successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to submit order return request.");
		}
	}

	@PutMapping("/orderrefund/status/{orderRefundId}")
	public ResponseEntity<?> updateReturnStatus(@PathVariable("orderRefundId") Long orderRefundId,
			@RequestBody OrderRefund requestBody) {

		try {
			OrderRefund existingReturn = orderRefundService.findById(orderRefundId);

			if (existingReturn == null) {
				return ResponseEntity.notFound().build();
			}

			String previousReturnStatus = existingReturn.getReturnStatus();
			String newReturnStatus = requestBody.getReturnStatus();
			existingReturn.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingReturn.setReturnStatus(newReturnStatus);

			if ("accepted".equals(newReturnStatus)) {
				existingReturn.setAccepted(true);
				existingReturn.setRejected(false);
				existingReturn.setPending(false);
				existingReturn.setRefundDate(LocalDate.now());

			} else if ("rejected".equals(newReturnStatus)) {
				existingReturn.setAccepted(false);
				existingReturn.setPending(false);
				existingReturn.setRejected(true);

			} else {
				existingReturn.setAccepted(false);
				existingReturn.setPending(false);
				existingReturn.setRejected(false);
			}

			orderRefundService.SaveOrderReturnDetails(existingReturn);

			if (!previousReturnStatus.equals(newReturnStatus)) {
				OrderItemList orderItemList = orderItemListRepository.findById(existingReturn.getOrderItemListId())
						.orElse(null);
				if (orderItemList != null) {

					if ("accepted".equals(newReturnStatus)) {
						orderItemList.setReturnAccepted(true);
						orderItemList.setReturnRejected(false);
						orderItemList.setReturnPending(false);
						orderItemList.setOrderStatus("refundAccepted");
					} else if ("rejected".equals(newReturnStatus)) {
						orderItemList.setReturnAccepted(false);
						orderItemList.setReturnRejected(true);
						orderItemList.setReturnPending(false);
						orderItemList.setOrderStatus("refundRejected");
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
	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}
	@GetMapping("/order/refund")
	public ResponseEntity<?> getReturnDetails1333(@RequestParam(required = true) String orderRefund) {
		try {
			if ("refundDetails".equalsIgnoreCase(orderRefund)) {
			List<Map<String, Object>> returnDetails = orderRefundRepository.getUserReturnDetailsByUserIdList();
			List<Map<String, Object>> orderMapList = new ArrayList<>();

			for (Map<String, Object> productDetails : returnDetails) {
				Map<String, Object> productMap = new HashMap<>();
				String resumeUrl = "product/" + generateRandomNumber() + "/" + productDetails.get("productImagesId");

				productMap.put("productImagesUploadUrl", resumeUrl);
				productMap.putAll(productDetails);
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

}
