package com.example.kkBazar.controller.dashboard;

import java.io.IOException;
import java.sql.Blob;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.kkBazar.entity.dashboard.Dashboard3;
import com.example.kkBazar.repository.dashboard.Dashboard3Repository;
import com.example.kkBazar.service.dashboard.Dashboard3Service;

@RestController
@CrossOrigin
public class Dashboard3Controller {

	@Autowired
	private Dashboard3Service dashboard3Service;

	@Autowired
	private Dashboard3Repository dashboard3Repository;

	@PostMapping("/carousel/save")
	public ResponseEntity<String> saveDashboars(@RequestParam("categoryId") long categoryId,
			@RequestParam("title") String title, @RequestParam("description") String description,
			@RequestParam("fileUpload") MultipartFile categoryImage) throws SQLException {

		try {
			Dashboard3 dashboard = new Dashboard3();
			dashboard.setCategoryId(categoryId);
			dashboard.setDescription(description);
			dashboard.setTitle(title);
			dashboard.setFileUpload(convertToBlob(categoryImage));
			dashboard.setStatus(true);
			dashboard.setCreatedAt(new Date(System.currentTimeMillis()));
			dashboard3Service.SaveDashboard(dashboard);

			return ResponseEntity.ok("Carousel Details saved successfully.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while saving the category: " + e.getMessage());
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

	@GetMapping("/carousel/view")
	public ResponseEntity<Object> getAllRoleByEmployee(@RequestParam(required = true) String carousel) {
		if ("carouselDetails".equals(carousel)) {
			List<Map<String, Object>> dashboardList = new ArrayList<>();
			List<Map<String, Object>> dashboardRole = dashboard3Repository.getAllDashboard3Details();
			Map<String, List<Map<String, Object>>> dashboardGroupMap = dashboardRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("dashboard3id").toString()));

			for (Entry<String, List<Map<String, Object>>> dashboardLoop : dashboardGroupMap.entrySet()) {
				Map<String, Object> dashboardMap = new HashMap<>();
				String dashboard3Id = dashboardLoop.getKey();
				dashboardMap.put("dashboard3Id", Long.parseLong(dashboardLoop.getKey()));
				dashboardMap.put("description", dashboardLoop.getValue().get(0).get("description"));
				dashboardMap.put("title", dashboardLoop.getValue().get(0).get("title"));
				dashboardMap.put("categoryId", dashboardLoop.getValue().get(0).get("category_id"));
				dashboardMap.put("categoryName", dashboardLoop.getValue().get(0).get("category_name"));
				dashboardMap.put("status", dashboardLoop.getValue().get(0).get("status"));

				int randomNumber = generateRandomNumber();
				String dashboardImageUrl = "dashboard3/" + randomNumber + "/" + dashboard3Id;
				dashboardMap.put("url", dashboardImageUrl);

				dashboardList.add(dashboardMap);
			}

			return ResponseEntity.ok(dashboardList);
		} else {
			String errorMessage = "Invalid value for 'department'. Expected 'Department'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	@GetMapping("/carousel/view/admin")
	public ResponseEntity<Object> getAllRoleByEmployeeAdmin(@RequestParam(required = true) String Admin) {
		if ("carouselDetails".equals(Admin)) {
			List<Map<String, Object>> dashboardList = new ArrayList<>();
			List<Map<String, Object>> dashboardRole = dashboard3Repository.getAllDashboard3DetailsAdmin();
			Map<String, List<Map<String, Object>>> dashboardGroupMap = dashboardRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("dashboard3id").toString()));

			for (Entry<String, List<Map<String, Object>>> dashboardLoop : dashboardGroupMap.entrySet()) {
				Map<String, Object> dashboardMap = new HashMap<>();
				String dashboard3Id = dashboardLoop.getKey();
				dashboardMap.put("dashboard3Id", Long.parseLong(dashboardLoop.getKey()));
				dashboardMap.put("description", dashboardLoop.getValue().get(0).get("description"));
				dashboardMap.put("title", dashboardLoop.getValue().get(0).get("title"));
				dashboardMap.put("categoryId", dashboardLoop.getValue().get(0).get("category_id"));
				dashboardMap.put("categoryName", dashboardLoop.getValue().get(0).get("category_name"));
				dashboardMap.put("status", dashboardLoop.getValue().get(0).get("status"));

				int randomNumber = generateRandomNumber();
				String dashboardImageUrl = "dashboard3/" + randomNumber + "/" + dashboard3Id;
				dashboardMap.put("url", dashboardImageUrl);

				dashboardList.add(dashboardMap);
			}

			return ResponseEntity.ok(dashboardList);
		} else {
			String errorMessage = "Invalid value for 'department'. Expected 'Department'.";
			return ResponseEntity.badRequest().body(errorMessage);
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}
	

	@GetMapping("/dashboard3/{randomNumber}/{id}")
	public ResponseEntity<ByteArrayResource> serveFile(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") Long id) {
		Optional<Dashboard3> complaintsOptional = dashboard3Service.getById1(id);
		if (complaintsOptional.isPresent()) {
			Dashboard3 complaints = complaintsOptional.get();
			String filename = "file_" + randomNumber + "_" + id;
			byte[] fileBytes;
			try {
				fileBytes = complaints.getFileUpload().getBytes(1, (int) complaints.getFileUpload().length());
			} catch (SQLException e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}

			String extension = determineFileExtension(fileBytes);
			MediaType mediaType = determineMediaType(extension);

			ByteArrayResource resource = new ByteArrayResource(fileBytes);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(mediaType);
			headers.set(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + filename + "." + extension);
			return ResponseEntity.ok().headers(headers).body(resource);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	private String determineFileExtension(byte[] fileBytes) {
		try {
			String fileSignature = bytesToHex(Arrays.copyOfRange(fileBytes, 0, 4));
			if (fileSignature.startsWith("89504E47")) {
				return "png";
			} else if (fileSignature.startsWith("FFD8FF")) {
				return "jpg";
			} else if (fileSignature.startsWith("52494646")) {
				return "webp";
			} else if (fileSignature.startsWith("47494638")) {
				return "gif";
			} else if (fileSignature.startsWith("66747970") || fileSignature.startsWith("00000020")) {
				return "mp4";
			} else if (fileSignature.startsWith("25504446")) {
				return "pdf";
			}
		} catch (Exception e) {
			
		}
		return "unknown";
	}

	private MediaType determineMediaType(String extension) {
		switch (extension) {
		case "png":
			return MediaType.IMAGE_PNG;
		case "jpg":
			return MediaType.IMAGE_JPEG;
		case "pdf":
			return MediaType.APPLICATION_PDF;
		case "webp":
			return MediaType.parseMediaType("image/webp");
		case "gif":
			return MediaType.parseMediaType("image/gif");
		case "mp4":
			return MediaType.parseMediaType("video/mp4");
		default:
			return MediaType.APPLICATION_OCTET_STREAM;
		}
	}

	private String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02X", b));
		}
		return sb.toString();
	}

	@PutMapping("/dashboard3/edit/{dashboard3Id}")
	public ResponseEntity<String> updateCategory(@PathVariable long dashboard3Id,
			@RequestParam(value = "fileUpload", required = false) MultipartFile file,
			@RequestParam(value = "categoryId", required = false) long categoryId,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "description", required = false) String description) {
		try {
			Dashboard3 dashboard = dashboard3Service.findDashboardById(dashboard3Id);

			if (dashboard == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
			}
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				dashboard.setFileUpload(blob);
			}
			if (categoryId != 0) {
				dashboard.setCategoryId(categoryId);
			}
			if (title != null) {
				dashboard.setTitle(title);
			}
			if (description != null) {
				dashboard.setDescription(description);
			}
			dashboard.setUpdatedAt(new Date(System.currentTimeMillis()));
			dashboard3Service.SaveDashboard(dashboard);

			return ResponseEntity.ok("Dashboard updated successfully. Dashboard ID: " + dashboard3Id);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating dashboard.");
		}
	}

	@PutMapping("/dashboard3/status/{id}")
	public ResponseEntity<Dashboard3> updateDashboard1Status(@PathVariable("id") Long dashboard1Id,
			@RequestBody Dashboard3 dashboard) {
		try {

			Dashboard3 existingDashboard3 = dashboard3Service.findDashboardById(dashboard1Id);

			if (existingDashboard3 == null) {
				return ResponseEntity.notFound().build();
			}
			existingDashboard3.setDashboardStatus(dashboard.getDashboardStatus());
			if (dashboard.getDashboardStatus().equals("inactive")) {
				existingDashboard3.setStatus(false);
			} else {
				existingDashboard3.setStatus(true);
			}

			dashboard3Service.SaveDashboard(existingDashboard3);
			return ResponseEntity.ok(existingDashboard3);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
}
