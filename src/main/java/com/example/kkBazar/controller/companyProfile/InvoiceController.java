package com.example.kkBazar.controller.companyProfile;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.companyProfile.Invoice;
import com.example.kkBazar.entity.companyProfile.InvoiceList;
import com.example.kkBazar.entity.user.OrderItem;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.repository.companyProfile.InvoiceRepository;
import com.example.kkBazar.repository.user.OrderItemRepository;
import com.example.kkBazar.service.companyProfile.InvoiceService;

@RestController
@CrossOrigin
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@Autowired
	private InvoiceRepository invoiceRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@PostMapping("/invoice/save")
	public ResponseEntity<?> saveInvoiceDetails(@RequestBody Invoice invoice) {
		try {
			invoice.setCreatedAt(new Date(System.currentTimeMillis()));
			Optional<Invoice> optionalInvoiceItem = invoiceRepository.findByOrderItemId(invoice.getOrderItemId());
			if (optionalInvoiceItem.isPresent()) {
				return ResponseEntity.badRequest().body("order is already stored");
			}

			LocalDate currentDate = LocalDate.now();
			invoice.setDate(currentDate);
			invoice.setCompanyId(1);
			List<InvoiceList> invoiceList = new ArrayList<>();

			Optional<OrderItem> optionalOrderItem = orderItemRepository.findById(invoice.getOrderItemId());

			if (optionalOrderItem.isPresent()) {
				OrderItem orderItem = optionalOrderItem.get();
				orderItem.setInvoiceStatus("InvoiceCompleted");
				orderItem.setInvoiceFlag(true);
				List<OrderItemList> orderItemList = getOrderItemList(orderItem.getOrderItemList());

				for (OrderItemList orderItemListItem : orderItemList) {
				    // Check if the current order is not cancelled
				    if (!"cancelled".equalsIgnoreCase(orderItemListItem.getOrderStatus())) {
				        // Change the order status to confirmed
				        orderItemListItem.setOrderStatus("confirmed");
				        orderItemListItem.setConfirmed(true);

				        // If the order status is confirmed, create an InvoiceList entry
				        if ("confirmed".equalsIgnoreCase(orderItemListItem.getOrderStatus())) {
				            InvoiceList invoiceListItem = new InvoiceList();
				            
				            invoiceListItem.setOrderItemListId(orderItemListItem.getOrderItemListId());
				            invoiceListItem.setDate(currentDate);

				            invoiceListItem.setUserId(orderItem.getUserId());
				            invoiceList.add(invoiceListItem);
				        }
				    }
				}
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("OrderItem not found with ID: " + invoice.getOrderItemId());
			}

			invoice.setInvoiceList(invoiceList);

			invoiceService.SaveInvoiceDetails(invoice);

			long id = invoice.getInvoiceId();
			return ResponseEntity.ok("Invoice Details saved successfully. Invoice ID: " + id);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error saving invoice: " + e.getMessage());
		}
	}

	private List<OrderItemList> getOrderItemList(List<OrderItemList> orderItemList) {
		return orderItemList;
	}

	@GetMapping("/invoice/details/{id}")
	public Map<String, Object> getInvoiceDetails(@PathVariable("id") Long orderItemListId) {
		try {
			return invoiceRepository.getInvoiceDetails(orderItemListId);
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyMap();
		}
	}

//	@GetMapping("/invoice/detail/view/{id}")
//	public ResponseEntity<Object> getInvoiceDetail5(@PathVariable(value = "id") Long orderItemId) {
//	    try {
//	        List<Map<String, Object>> mainList = new ArrayList<>();
//	        List<Map<String, Object>> orderItemRole = invoiceRepository.getInvoiceDetailsByOrderItemId(orderItemId);
//
//	        Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderItemGroupMap = orderItemRole
//	                .stream()
//	                .collect(Collectors.groupingBy(action -> getString(action, "orderItemId"),
//	                        Collectors.groupingBy(action -> getString(action, "orderItemListId"),
//	                                Collectors.groupingBy(action -> getString(action, "productListId"),
//	                                        Collectors.groupingBy(action -> getString(action, "companyId"),
//	                                                Collectors.groupingBy(action -> getString(action, "userId")))))));
//
//	        for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderLoop : orderItemGroupMap
//	                .entrySet()) {
//	            Map<String, Object> orderMap = new HashMap<>();
//	            orderMap.put("orderItemId", orderLoop.getKey());
//
//	            List<Map<String, Object>> orderItemList = new ArrayList<>();
//	            for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> orderItemListLoop : orderLoop
//	                    .getValue().entrySet()) {
//	                Map<String, Object> orderItemListMap = new HashMap<>();
//	                orderItemListMap.put("orderItemListId", orderItemListLoop.getKey());
//
//	                List<Map<String, Object>> productList = new ArrayList<>();
//	                for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : orderItemListLoop
//	                        .getValue().entrySet()) {
//	                    Map<String, Object> productListMap = new HashMap<>();
//	                    productListMap.put("productListId", productListLoop.getKey());
//
//	                    List<Map<String, Object>> companyList = new ArrayList<>();
//	                    for (Map.Entry<String, Map<String, List<Map<String, Object>>>> companyLoop : productListLoop
//	                            .getValue().entrySet()) {
//	                        Map<String, Object> companyMap = new HashMap<>();
//	                        companyMap.put("companyId", companyLoop.getKey());
//
//	                        List<Map<String, Object>> userList = new ArrayList<>();
//	                        for (Map.Entry<String, List<Map<String, Object>>> userLoop : companyLoop
//	                                .getValue().entrySet()) {
//	                            Map<String, Object> userMap = new HashMap<>();
//	                            String userId = userLoop.getKey();
//	                            userMap.put("userId", userLoop.getKey());
//	                            Map<String, Object> firstItem = userLoop.getValue().get(0);
//	                            userMap.put("userName", firstItem.get("userName"));
//	                            userMap.put("userMail", firstItem.get("userMail"));
//	                            userMap.put("userMobileNumber", firstItem.get("userMobileNumber"));
//	                            userMap.put("userAddressId", firstItem.get("userAddressId"));
//	                            userMap.put("addressType", firstItem.get("addressType"));
//	                            userMap.put("userCity", firstItem.get("userCity"));
//	                            userMap.put("userCountry", firstItem.get("userCountry"));
//	                            userMap.put("userPostalCode", firstItem.get("userPostalCode"));
//	                            userMap.put("userState", firstItem.get("userState"));
//	                            userMap.put("userStreetAddress", firstItem.get("userStreetAddress"));
//	                       
//	                            
//	                            
//	                            orderMap.put("invoiceId", firstItem.get("invoiceId"));
//	                            companyMap.put("companyAddress", firstItem.get("companyAddress"));
//	                            companyMap.put("companyName", firstItem.get("companyName"));
//	                            companyMap.put("companyCountry", firstItem.get("companyCountry"));
//	                            companyMap.put("companyEmail", firstItem.get("companyEmail"));
//	                            companyMap.put("companyLocation", firstItem.get("companyLocation"));
//	                            companyMap.put("companyPhoneNumber", firstItem.get("companyPhoneNumber"));
//	                            companyMap.put("companyPincode", firstItem.get("companyPincode"));
//	                            companyMap.put("companyState", firstItem.get("companyState"));
//	                            orderMap.put("totalAmount", firstItem.get("totalAmount"));
//	                            orderMap.put("totalItems", firstItem.get("totalItems"));
//	                            
//	                            orderItemListMap.put("quantity", firstItem.get("quantity"));
//	                            orderItemListMap.put("totalPrice", firstItem.get("totalPrice"));
//	                            orderItemListMap.put("orderDate", firstItem.get("orderDate"));
//	                            orderItemListMap.put("totalItems", firstItem.get("totalItems"));
//	                            orderItemListMap.put("totalItems", firstItem.get("totalItems"));
//	                            
//	                            productListMap.put("gst", firstItem.get("gst"));
//	                            productListMap.put("gstTaxAmount", firstItem.get("gstTaxAmount"));
//	                            productListMap.put("productId", firstItem.get("productId"));
//	                            productListMap.put("productName", firstItem.get("productName"));
//	                       
//	                            
//	                            
//	                            userList.add(userMap);
//	                        }
//	                        companyMap.put("userDetails", userList);
//	                        companyList.add(companyMap);
//	                    }
//	                    productListMap.put("companyDetails", companyList);
//	                    productList.add(productListMap);
//	                }
//	                orderItemListMap.put("productListDetails", productList);
//	                orderItemList.add(orderItemListMap);
//	            }
//	            orderMap.put("orderItemDetails", orderItemList);
//	            mainList.add(orderMap);
//	        }
//	        return ResponseEntity.ok(mainList);
//	    } catch (Exception e) {
//	        String errorMessage = "Error processing order item details.";
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
//	    }
//	}
	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/invoice/detail/view/{id}")
	public ResponseEntity<Object> getInvoiceDetail5(@PathVariable(value = "id") Long orderItemId) {
		try {
			List<Map<String, Object>> mainList = new ArrayList<>();
			List<Map<String, Object>> orderItemRole = invoiceRepository.getInvoiceDetailsByOrderItemId(orderItemId);

			Map<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderItemGroupMap = orderItemRole
					.stream()
					.collect(Collectors.groupingBy(action -> getString(action, "orderItemId"),
							Collectors.groupingBy(action -> getString(action, "orderItemListId"),
									Collectors.groupingBy(action -> getString(action, "productListId"),
											Collectors.groupingBy(action -> getString(action, "companyId"),
													Collectors.groupingBy(action -> getString(action, "userId")))))));

			for (Map.Entry<String, Map<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>>> orderLoop : orderItemGroupMap
					.entrySet()) {
				Map<String, Object> orderMap = new HashMap<>();
				orderMap.put("orderItemId", orderLoop.getKey());

				List<Map<String, Object>> orderItemList = new ArrayList<>();
				for (Map.Entry<String, Map<String, Map<String, Map<String, List<Map<String, Object>>>>>> orderItemListLoop : orderLoop
						.getValue().entrySet()) {
					Map<String, Object> orderItemListMap = new HashMap<>();
					orderItemListMap.put("orderItemListId", orderItemListLoop.getKey());

					List<Map<String, Object>> productList = new ArrayList<>();
					for (Map.Entry<String, Map<String, Map<String, List<Map<String, Object>>>>> productListLoop : orderItemListLoop
							.getValue().entrySet()) {
						Map<String, Object> productListMap = new HashMap<>();
						productListMap.put("productListId", productListLoop.getKey());

						List<Map<String, Object>> companyList = new ArrayList<>();
						for (Map.Entry<String, Map<String, List<Map<String, Object>>>> companyLoop : productListLoop
								.getValue().entrySet()) {
							Map<String, Object> companyMap = new HashMap<>();
							companyMap.put("companyId", companyLoop.getKey());

							List<Map<String, Object>> userList = new ArrayList<>();
							for (Map.Entry<String, List<Map<String, Object>>> userLoop : companyLoop.getValue()
									.entrySet()) {
								Map<String, Object> userMap = new HashMap<>();
								String userId = userLoop.getKey();

								userMap.put("userId", userLoop.getKey());

								Map<String, Object> firstItem = userLoop.getValue().get(0);

								int randomNumber = generateRandomNumber();
								String fileExtension = getFileExtensionForImage(firstItem);
								String url = "company/" + randomNumber + "/" + firstItem.get("CompanyId") + "."
										+ fileExtension;
								String url1 = "signature/" + randomNumber + "/" + firstItem.get("CompanyId") + "."
										+ fileExtension;
								orderMap.put("userName", firstItem.get("userName"));
								orderMap.put("userMail", firstItem.get("userMail"));
								orderMap.put("userMobileNumber", firstItem.get("userMobileNumber"));
								orderMap.put("userAddressId", firstItem.get("userAddressId"));
								orderMap.put("companyProfile", url);
								orderMap.put("signature", url1);
								orderMap.put("addressType", firstItem.get("addressType"));
								orderMap.put("userCity", firstItem.get("userCity"));
								orderMap.put("userDistrict", firstItem.get("district"));
								orderMap.put("userCountry", firstItem.get("userCountry"));
								orderMap.put("userPostalCode", firstItem.get("userPostalCode"));
								orderMap.put("userState", firstItem.get("userState"));
								orderMap.put("userStreetAddress", firstItem.get("userStreetAddress"));
								orderMap.put("date", firstItem.get("date"));												
								orderMap.put("invoiceId", firstItem.get("invoiceId"));
								orderMap.put("companyAddress", firstItem.get("companyAddress"));
								orderMap.put("companyName", firstItem.get("companyName"));
								orderMap.put("companyCountry", firstItem.get("companyCountry"));
								orderMap.put("companyEmail", firstItem.get("companyEmail"));
								orderMap.put("companyLocation", firstItem.get("companyLocation"));
								orderMap.put("companyPhoneNumber", firstItem.get("companyPhoneNumber"));
								orderMap.put("companyPincode", firstItem.get("companyPincode"));
								orderMap.put("companyState", firstItem.get("companyState"));
								orderMap.put("totalAmount", firstItem.get("totalAmount"));
								orderMap.put("totalItems", firstItem.get("totalItems"));
								orderItemListMap.put("quantity", firstItem.get("quantity"));
								orderItemListMap.put("totalAmount", firstItem.get("total_amount"));
								orderItemListMap.put("totalPrice", firstItem.get("totalPrice"));
								orderItemListMap.put("orderDate", firstItem.get("orderDate"));
								orderItemListMap.put("totalItems", firstItem.get("totalItems"));
								orderItemListMap.put("totalItems", firstItem.get("totalItems"));
								orderItemListMap.put("gst", firstItem.get("gst"));
								orderItemListMap.put("gstTaxAmount", firstItem.get("gstTaxAmount"));
								orderItemListMap.put("productId", firstItem.get("productId"));
								orderItemListMap.put("orderId", firstItem.get("orderId"));
								orderItemListMap.put("productName", firstItem.get("productName"));

								userList.add(userMap);
							}

							companyList.add(companyMap);
						}

						productList.add(productListMap);
					}

					orderItemList.add(orderItemListMap);
				}
				orderMap.put("orderItemDetails", orderItemList);
				mainList.add(orderMap);
			}
			return ResponseEntity.ok(mainList);
		} catch (Exception e) {
			String errorMessage = "Error processing order item details.";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	private String getFileExtensionForImage(Map<String, Object> firstItem) {
		if (firstItem == null) {
			return "jpg";
		}

		Object urlObject = firstItem.get("url");
		if (urlObject == null || !(urlObject instanceof String)) {
			return "jpg";
		}

		String url = (String) urlObject;
		if (url.isEmpty()) {
			return "jpg";
		}

		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}

	private String getString(Map<String, Object> map, String key) {
		return map.get(key) != null ? map.get(key).toString() : "";
	}

	@PutMapping("/invoiceStatus/edit/{id}")
	public ResponseEntity<Invoice> updateInvoice(@PathVariable("id") Long invoiceId, @RequestBody Invoice invoice) {
		try {
			Invoice existingInvoice = invoiceService.findById(invoiceId);

			if (existingInvoice == null) {
				return ResponseEntity.notFound().build();
			}

			if ("approved".equalsIgnoreCase(invoice.getStatusType())) {
				existingInvoice.setStatus(true);
			}
			existingInvoice.setUpdatedAt(new Date(System.currentTimeMillis()));
			existingInvoice.setStatusType(invoice.getStatusType());
			existingInvoice.setInvoiceDate(invoice.getInvoiceDate());

			invoiceService.SaveInvoiceDetails(existingInvoice);
			return ResponseEntity.ok(existingInvoice);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

}
