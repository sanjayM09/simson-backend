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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.kkBazar.entity.dashboard.Dashboard4;
import com.example.kkBazar.repository.dashboard.Dashboard4Repository;
import com.example.kkBazar.service.dashboard.Dashboard4Service;

@RestController
@CrossOrigin
public class Dashboard4Controller {

	@Autowired
	private Dashboard4Service dashboard4Service;
	@Autowired
	private Dashboard4Repository dashboard4Repository;

	@PostMapping("/carousel2/save")
	public ResponseEntity<String> saveDashboards(@RequestParam("categoryId") long categoryId,
			@RequestParam("title") String title, @RequestParam("fileUpload") MultipartFile categoryImage)
			throws SQLException {

		try {
			Dashboard4 dashboard = new Dashboard4();
			dashboard.setCategoryId(categoryId);
			dashboard.setCreatedAt(new Date(System.currentTimeMillis()));
			dashboard.setTitle(title);
			dashboard.setFileUpload(convertToBlob(categoryImage));
			dashboard.setStatus(true);
			dashboard4Service.SaveDashboard(dashboard);

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

	@GetMapping("/carousel2/view")
	public ResponseEntity<Object> getAllRoleByEmployee(@RequestParam(required = true) String carousel) {
		if ("carouselDetails".equals(carousel)) {
			List<Map<String, Object>> dashboardList = new ArrayList<>();
			List<Map<String, Object>> dashboardRole = dashboard4Repository.getAllDashboard4Details();
			Map<String, List<Map<String, Object>>> dashboardGroupMap = dashboardRole.stream()
					.collect(Collectors.groupingBy(action -> action.get("dashboard4id").toString()));

			for (Entry<String, List<Map<String, Object>>> dashboardLoop : dashboardGroupMap.entrySet()) {
				Map<String, Object> dashboardMap = new HashMap<>();
				String dashboard4Id = dashboardLoop.getKey();
				dashboardMap.put("dashboard4Id", Long.parseLong(dashboardLoop.getKey()));
				dashboardMap.put("title", dashboardLoop.getValue().get(0).get("title"));
				dashboardMap.put("categoryId", dashboardLoop.getValue().get(0).get("category_id"));
				dashboardMap.put("categoryName", dashboardLoop.getValue().get(0).get("category_name"));

				int randomNumber = generateRandomNumber();
				String dashboardImageUrl = "dashboard4/" + randomNumber + "/" + dashboard4Id;
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

	@GetMapping("/dashboard4/{randomNumber}/{id}")
	public ResponseEntity<ByteArrayResource> serveFile(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") Long id) {
		Optional<Dashboard4> complaintsOptional = dashboard4Service.getById1(id);
		if (complaintsOptional.isPresent()) {
			Dashboard4 complaints = complaintsOptional.get();
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

	@PutMapping("/dashboard4/edit/{dashboard4Id}")
	public ResponseEntity<String> updateCategory(@PathVariable long dashboard4Id,
			@RequestParam(value = "fileUpload", required = false) MultipartFile file,
			@RequestParam(value = "title", required = false) String title,
			@RequestParam(value = "categoryId", required = false) Long categoryId) {
		try {
			Dashboard4 dashboard = dashboard4Service.findDashboardById(dashboard4Id);

			if (dashboard == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Category not found.");
			}
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				dashboard.setFileUpload(blob);
			}

			if (title != null) {
				dashboard.setTitle(title);
			}
			if (categoryId != null) {
				dashboard.setCategoryId(categoryId);
			}
			dashboard.setUpdatedAt(new Date(System.currentTimeMillis()));
			dashboard4Service.SaveDashboard(dashboard);

			return ResponseEntity.ok("Dashboard updated successfully. Dashboard ID: " + dashboard4Id);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating dashboard.");
		}
	}

}
