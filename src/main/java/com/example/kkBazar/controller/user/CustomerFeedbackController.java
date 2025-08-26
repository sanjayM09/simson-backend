package com.example.kkBazar.controller.user;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.kkBazar.entity.user.CustomerFeedback;
import com.example.kkBazar.entity.user.CustomerFeedbackList;
import com.example.kkBazar.repository.user.CustomerFeedbackRepository;
import com.example.kkBazar.service.user.CustomerFeedbackListService;
import com.example.kkBazar.service.user.CustomerFeedbackService;

@RestController
@CrossOrigin
public class CustomerFeedbackController {

	@Autowired
	private CustomerFeedbackService customerFeedbackService;

	@Autowired
	private CustomerFeedbackListService customerFeedbackListService;

	@Autowired
	private CustomerFeedbackRepository customerFeedbackRepository;

	@PostMapping("/customerFeedback/save")
	public ResponseEntity<String> saveCustomerFeedback(@RequestParam("userId") long userId,
			@RequestParam("description") String description, @RequestParam("productListId") long productListId,
			@RequestParam("starRate") int starRate, @RequestParam("image") MultipartFile image) throws SQLException {

		try {
			if (starRate < 1 || starRate > 5) {
				return ResponseEntity.badRequest().body("starRate must be between 1 and 5.");
			}

			CustomerFeedback customerFeedback = new CustomerFeedback();
			CustomerFeedbackList customerFeedbackList = new CustomerFeedbackList();

			customerFeedback.setUserId(userId);

			customerFeedbackList.setDate(LocalDate.now());

			starRate = Math.max(1, Math.min(5, starRate));
			customerFeedbackList.setStarRate(starRate);

			customerFeedbackList.setDescription(description);
			customerFeedbackList.setProductListId(productListId);
			customerFeedbackList.setImage(convertToBlob(image));

			customerFeedback.setCustomerFeedbackList(Collections.singletonList(customerFeedbackList));

			customerFeedbackService.SaveCustomerFeedback(customerFeedback);

			return ResponseEntity.ok("CustomerFeedback details saved successfully.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while saving the customerFeedback: " + e.getMessage());
		}
	}

	private Blob convertToBlob(MultipartFile file) throws IOException, SQLException {
		if (file != null && !file.isEmpty()) {
			byte[] bytes = file.getBytes();
			return new javax.sql.rowset.serial.SerialBlob(bytes);
		} else {
			return null;
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("/feedback/view")
	public ResponseEntity<?> displayAllImages(@RequestParam(required = true) String feedback) {
		if ("feedbackDetails".equals(feedback)) {
			List<CustomerFeedback> feedbackList = customerFeedbackService.listAll();
			List<Map<String, Object>> imageObjectsList = new ArrayList<>();

			for (CustomerFeedback feedbackItem : feedbackList) {
				for (CustomerFeedbackList feedbackListItem : feedbackItem.getCustomerFeedbackList()) {
					Map<String, Object> imageObjects = new HashMap<>();
					int randomNumber = generateRandomNumber();
					String fileExtension = getFileExtensionForImage(feedbackListItem.getUrl());
					String url = "feedback/" + randomNumber + "/" + feedbackListItem.getCustomerFeedbackListId() + "."
							+ fileExtension;

					imageObjects.put("customerFeedbackId", feedbackItem.getCustomerFeedbackId());
					imageObjects.put("userId", feedbackItem.getUserId());
					imageObjects.put("description", feedbackListItem.getDescription());
					imageObjects.put("customerFeedbackListId", feedbackListItem.getCustomerFeedbackListId());
					imageObjects.put("productListId", feedbackListItem.getProductListId());
					imageObjects.put("date", feedbackListItem.getDate());
					imageObjects.put("starRate", feedbackListItem.getStarRate());
					imageObjects.put("url", url);

					imageObjectsList.add(imageObjects);
				}
			}
			return ResponseEntity.ok(imageObjectsList);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}
	}

	@GetMapping("/customerFeedback/view")
	public ResponseEntity<Object> getAllFeedback(@RequestParam(required = true) String feedback) {
		if ("feedbacks".equals(feedback)) {
			List<Map<String, Object>> feedbackList = new ArrayList<>();
			List<Map<String, Object>> feedbackRole = customerFeedbackRepository.getFeedbackDetails();
			Map<String, List<Map<String, Object>>> feedbackGroupMap = feedbackRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("customer_feedback_id").toString()));

			for (Entry<String, List<Map<String, Object>>> feedbackLoop : feedbackGroupMap.entrySet()) {
				Map<String, Object> feedbackMap = new HashMap<>();
				feedbackMap.put("customerFeedbackId", Long.parseLong(feedbackLoop.getKey()));
				feedbackMap.put("userId", feedbackLoop.getValue().get(0).get("user_id"));
				feedbackMap.put("userName", feedbackLoop.getValue().get(0).get("user_name"));
				List<Map<String, Object>> feedbacksList = new ArrayList<>();
				for (Map<String, Object> feedbacksLoop : feedbackLoop.getValue()) {
					Map<String, Object> feedbacksMap = new HashMap<>();
					feedbacksMap.put("customerFeedbackListId", feedbacksLoop.get("customer_feedback_list_id"));
					feedbacksMap.put("date", feedbacksLoop.get("date"));
					feedbacksMap.put("description", feedbacksLoop.get("description"));
					feedbacksMap.put("productListId", feedbacksLoop.get("product_list_id"));
					feedbacksMap.put("starRate", feedbacksLoop.get("star_rate"));
					Object urlObject = feedbacksLoop.get("url");
					String fileExtension = getFileExtensionForImage((urlObject != null) ? urlObject.toString() : null);

					int randomNumber = generateRandomNumber();
					BigInteger uniqueId = (BigInteger) feedbacksLoop.get("customer_feedback_list_id");

					String url = "feedback/" + randomNumber + "/" + uniqueId + "." + fileExtension;
					feedbacksMap.put("url", url);

					feedbacksList.add(feedbacksMap);
				}
				feedbackMap.put("FeedbackList", feedbacksList);
				feedbackList.add(feedbackMap);
			}

			return ResponseEntity.ok(feedbackList);
		} else {
			String errorMessage = "Invalid value for 'feedback'. Expected 'feedbacks'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}
	

	@GetMapping("feedback/{randomNumber}/{id:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		String fileExtension = parts[1];

		Long feedbackListId;
		try {
			feedbackListId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build(); // Invalid feedback list ID format
		}

		CustomerFeedbackList feedbackListItem = customerFeedbackListService
				.findCustomerFeedbackListById(feedbackListId);
		if (feedbackListItem == null) {
			return ResponseEntity.notFound().build();
		}

		byte[] imageBytes;
		try {
			imageBytes = feedbackListItem.getImage().getBytes(1, (int) feedbackListItem.getImage().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		if ("png".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else if ("jpg".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else {
			headers.setContentType(MediaType.IMAGE_JPEG);
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	
	private String getFileExtensionForImage(String url) {
		if (url == null || url.isEmpty()) {
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
	

	@DeleteMapping("/customerFeedback/delete/{customerFeedbackId}")
	public ResponseEntity<String> deleteCustomerFeedbackDetail(
			@PathVariable("customerFeedbackId") Long customerFeedbackId) {
		customerFeedbackService.deleteCustomerFeedbackById(customerFeedbackId);
		return ResponseEntity.ok("CustomerFeedback details deleted successfully");
	}

}