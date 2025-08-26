package com.example.kkBazar.controller.user;

import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.kkBazar.entity.product.ProductList;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.repository.addProduct.ProductListRepository;
import com.example.kkBazar.repository.user.OrderItemRepository;
import com.example.kkBazar.service.user.OrderItemListService;

@RestController
@CrossOrigin
public class OrderItemListController {
	
	@Autowired
	private ProductListRepository productListRepository;

	@Autowired
	private OrderItemRepository orderItemRepository;

	@Autowired
	private OrderItemListService orderItemListService;

	private static final Logger logger = LoggerFactory.getLogger(OrderItemController.class);

	@PutMapping("/order/status/{orderItemListId}")
	public ResponseEntity<?> updateOrderStatus(@PathVariable("orderItemListId") Long orderItemListId,
			@RequestBody OrderItemList requestBody) {

		try {
			long id = requestBody.getUserOrderId();
			List<Map<String, Object>> existingItemWithInvoice = orderItemRepository.getQualifications28888(id);

			if (existingItemWithInvoice.isEmpty()) {
				logger.warn("No invoice entry found for orderItemListId: {}", orderItemListId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invoice entry is not allowed.");
			}

			OrderItemList existingOrderItemList = orderItemListService.findById(orderItemListId);
			if (existingOrderItemList == null) {
				logger.warn("OrderItemList not found for orderItemListId: {}", orderItemListId);
				return ResponseEntity.notFound().build();
			}

			if (existingOrderItemList.getOrderStatus().equals("returnRequestPending")
					|| existingOrderItemList.getOrderStatus().equals("returnAccepted")
					|| existingOrderItemList.getOrderStatus().equals("returnRejected")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The product is already in return and cannot be delivered");
			}

			existingOrderItemList.setOrderStatus(requestBody.getOrderStatus());
			if ("delivered".equals(existingOrderItemList.getOrderStatus())) {
				existingOrderItemList.setDelivered(true);
				existingOrderItemList.setConfirmed(false);
				existingOrderItemList.setPending(false);
				existingOrderItemList.setDeliveredDate(LocalDate.now());
			} else if ("confirmed".equals(existingOrderItemList.getOrderStatus())) {
				existingOrderItemList.setConfirmed(true);
				existingOrderItemList.setDelivered(false);
				existingOrderItemList.setPending(false);
				existingOrderItemList.setConfirmedDate(LocalDate.now());
			} else {
				existingOrderItemList.setCancelled(false);
				existingOrderItemList.setConfirmed(false);
				existingOrderItemList.setDelivered(false);
				existingOrderItemList.setPending(false);
			}

			orderItemListService.SaveOrderItemListDetails(existingOrderItemList);

			logger.info("Order status updated for orderItemListId: {}", orderItemListId);
			return ResponseEntity.ok(existingOrderItemList);

		}  catch (Exception e) {
			logger.error("Error updating order status for orderItemListId: {}", orderItemListId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An error occurred while updating the order status.");
		}
	}

	@PutMapping("/orderStatus/{orderItemListId}")
	public ResponseEntity<?> updateOrderStatus2(@PathVariable("orderItemListId") Long orderItemListId,
	                                           @RequestBody OrderItemList requestBody) {

	    try {
	        OrderItemList existingOrderItemList = orderItemListService.findById(orderItemListId);
	        if (existingOrderItemList == null) {
	            return ResponseEntity.notFound().build();
	        }

	        if ("cancelled".equals(existingOrderItemList.getOrderStatus())) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
	                    .body("Your order is already cancelled and cannot be changed");
	        }

	        existingOrderItemList.setReason(requestBody.getReason());
	        existingOrderItemList.setOrderStatus(requestBody.getOrderStatus());

	        if ("cancelled".equals(existingOrderItemList.getOrderStatus())) {
	        	existingOrderItemList.setCancelledDate(LocalDate.now());
	            ProductList product = productListRepository.findById(existingOrderItemList.getProductListId()).orElse(null);
	            if (product == null) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product not found.");
	            }

	            double newQuantity = product.getQuantity() + existingOrderItemList.getQuantity();
	            product.setQuantity(newQuantity);

	            productListRepository.save(product);

	            existingOrderItemList.setCancelled(true);
	            existingOrderItemList.setDelivered(false);
	            existingOrderItemList.setReturnPending(false);
	            existingOrderItemList.setConfirmed(false);
	        } else {
	            existingOrderItemList.setCancelled(false);
	        }

	        orderItemListService.SaveOrderItemListDetails(existingOrderItemList);

	        return ResponseEntity.ok("Your order has been cancelled successfully.");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to cancel order.");
	    }
	}



	@PutMapping("/orderItemList/pdf/{orderItemListId}")
	public ResponseEntity<?> updateOrderStatusOrder(@PathVariable("orderItemListId") Long orderItemListId,
	                                           @RequestBody OrderItemList updatedOrderItemList) {
	    try {
	        OrderItemList existingOrderItemList = orderItemListService.findById(orderItemListId);
	        if (existingOrderItemList == null) {
	            return ResponseEntity.notFound().build();
	        }
	        String base64Image = existingOrderItemList.getPdfUrl();
	        if (base64Image != null) {
	            try {
	                byte[] imageBytes = Base64.getDecoder().decode(base64Image);
	                Blob blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
	                existingOrderItemList.setPdf(blob);
	            } catch (SQLException e) {
	                e.printStackTrace();
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                        .body("Error while processing PDF data");
	            }
	        }
	        existingOrderItemList.setInvoicePdf(true);
	        orderItemListService.SaveOrderItemListDetails(existingOrderItemList);
	        long id = existingOrderItemList.getOrderItemListId();
	        int randomNumber = generateRandomNumber();
	        String fileExtension = getFileExtensionForImage(updatedOrderItemList);
			String imageUrl = "https://dev.api.simsongarments.com/" + "orderItemList/" + randomNumber + "/" + id + "."+ fileExtension;
	        existingOrderItemList.setUrl(imageUrl);
	        orderItemListService.SaveOrderItemListDetails(existingOrderItemList);
	        return ResponseEntity.ok(existingOrderItemList);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the order item list");
	    }
	}


	@GetMapping("orderItemList/{randomNumber}/{id:.+}")
	public ResponseEntity<?> serveImage(@PathVariable("randomNumber") int randomNumber, @PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		if (id == null || !id.contains(".")) { 
			return ResponseEntity.badRequest().body("Invalid ID format.");
		}
		String fileExtension = parts[1];
		Long imageId;

		try {

			imageId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build();
		}
		OrderItemList image = orderItemListService.findById(imageId);

		if (image == null) {
			return ResponseEntity.notFound().build();
		}
		byte[] imageBytes;
		try {
			imageBytes = image.getPdf().getBytes(1, (int) image.getPdf().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		MediaType mediaType = determineMediaType(fileExtension);

		headers.setContentType(mediaType);

		return ResponseEntity.ok().headers(headers).body(resource);
	}
	
	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	private String getFileExtensionForImage(OrderItemList pdf) {
		if (pdf == null || pdf.getPdfUrl() == null || pdf.getPdfUrl().isEmpty()) {
			return "pdf";
		}

		String url = pdf.getPdfUrl();
		int lastIndex = url.lastIndexOf('.');
		if (lastIndex != -1) {
			return url.substring(lastIndex + 1).toLowerCase(); // Extract the extension from the URL
		}

		return "pdf";
	}

	private MediaType determineMediaType(String extension) {
		switch (extension) {

		case "pdf":
			return MediaType.APPLICATION_PDF;

		default:
			return MediaType.APPLICATION_PDF;
		}
	}

	
	@PutMapping("/orderStatus/edit/{id}")
	public ResponseEntity<Map<String, Object>> updateOrderStatus1(@PathVariable("id") Long orderItemListId,
			@RequestBody OrderItemList orderItemList) {
		try {
			OrderItemList existingOrder = orderItemListService.findById(orderItemListId);

			if (existingOrder == null) {
				return ResponseEntity.notFound().build();
			}

			if (existingOrder.isCancelled()) {
				Map<String, Object> alreadyCancelledResponse = new HashMap<>();
				alreadyCancelledResponse.put("message", "Your order is already cancelled.");
				return ResponseEntity.badRequest().body(alreadyCancelledResponse);
			}
			existingOrder.setCancelled(orderItemList.isCancelled());
			orderItemListService.SaveOrderItemListDetails(existingOrder);

			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Your order cancelled successfully.");

			return ResponseEntity.ok(successResponse);
		} catch (Exception e) {
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Failed to order cancelled.");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
		}
	}
}
