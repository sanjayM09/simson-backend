package com.example.kkBazar.controller.companyProfile;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.kkBazar.entity.companyProfile.Company;
import com.example.kkBazar.service.companyProfile.CompanyService;

@RestController
@CrossOrigin
public class CompanyController {
	@Autowired
	private CompanyService companyService;

	@PostMapping("/companyProfile/save")
	public ResponseEntity<?> saveCandidate(@RequestParam("companyName") String companyName,
			@RequestParam("address") String address, @RequestParam("pincode") int pincode,
			@RequestParam("state") String state, @RequestParam("country") String country,
			@RequestParam("location") String location, @RequestParam("phoneNumber") long phoneNumber,
			@RequestParam("email") String email, @RequestParam("profile") MultipartFile profile,
			@RequestParam("gst") String gst, @RequestParam("signature") MultipartFile signature) throws SQLException {

		try {
			if (isNullOrEmpty(email)) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "Email cannot be empty");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}

			if (!isValidEmail(email)) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "Invalid email format. Please provide a valid email address.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}

			if (phoneNumber == 0 || String.valueOf(phoneNumber).length() < 10) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "PhoneNumber should be exactly 10 digits");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}
			Company company = new Company();
			company.setAddress(address);
			company.setCompanyName(companyName);
			company.setCountry(country);
			company.setEmail(email);
			company.setLocation(location);
			company.setPhoneNumber(phoneNumber);
			company.setPincode(pincode);
			company.setState(state);
			company.setProfile(convertToBlob(profile));

			companyService.SaveCompanyDetails(company);

			return ResponseEntity.ok("CompanyProfile Details saved successfully.");
		} catch (IOException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Error occurred while saving the company: " + e.getMessage());
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

	private boolean isNullOrEmpty(String value) {
		return value == null || value.isEmpty();
	}

	private boolean isValidEmail(String email) {
		return email != null && email.contains("@");
	}

	@GetMapping("/company")
	public ResponseEntity<?> displayAllImages(@RequestParam(required = true) String company) {
		if ("companyDetails".equals(company)) {
			List<Company> images = companyService.listAll();
			Map<String, Object> imageObjects = new HashMap<>();
			for (Company image : images) {
				int randomNumber = generateRandomNumber();
				String fileExtension = getFileExtensionForImage(image);
				String url = "company/" + randomNumber + "/" + image.getCompanyId() + "." + fileExtension;
				String url1 = "signature/" + randomNumber + "/" + image.getCompanyId() + "." + fileExtension;
				image.setUrl(url);
				image.setSignatureUrl(url1);
				imageObjects.put("companyId", image.getCompanyId());
				imageObjects.put("companyName", image.getCompanyName());
				imageObjects.put("url", image.getUrl());
				imageObjects.put("signatureUrl", image.getSignatureUrl());
				imageObjects.put("address", image.getAddress());
				imageObjects.put("country", image.getCountry());
				imageObjects.put("email", image.getEmail());
				imageObjects.put("gst", image.getGst());
				imageObjects.put("state", image.getState());
				imageObjects.put("location", image.getLocation());
				imageObjects.put("pincode", image.getPincode());
				imageObjects.put("phoneNumber1", image.getPhoneNumber());
			}
			return ResponseEntity.ok(imageObjects);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
		}
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	@GetMapping("signature/{randomNumber}/{id:.+}")
	public ResponseEntity<Resource> serveImagesignature(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		String fileExtension = parts[1];

		Long imageId;
		try {
			imageId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build(); 
		}

		Company image = companyService.findById(imageId);
		if (image == null) {
			return ResponseEntity.notFound().build();
		}
		   if (image.getSignature() == null) {
		        return ResponseEntity.status(HttpStatus.NO_CONTENT).build(); // No signature available
		    }
		byte[] imageBytes;
		try {
			imageBytes = image.getSignature().getBytes(1, (int) image.getSignature().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		if ("jpg".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if ("png".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else {

			headers.setContentType(MediaType.IMAGE_JPEG);
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}

	@GetMapping("company/{randomNumber}/{id:.+}")
	public ResponseEntity<Resource> serveImage(@PathVariable("randomNumber") int randomNumber,
			@PathVariable("id") String id) {
		String[] parts = id.split("\\.");
		if (parts.length != 2) {
			return ResponseEntity.badRequest().build();
		}
		String fileExtension = parts[1];

		Long imageId;
		try {
			imageId = Long.parseLong(parts[0]);
		} catch (NumberFormatException e) {
			return ResponseEntity.badRequest().build(); 
		}

		Company image = companyService.findById(imageId);
		if (image == null) {
			return ResponseEntity.notFound().build();
		}

		byte[] imageBytes;
		try {
			imageBytes = image.getProfile().getBytes(1, (int) image.getProfile().length());
		} catch (SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}

		ByteArrayResource resource = new ByteArrayResource(imageBytes);
		HttpHeaders headers = new HttpHeaders();

		if ("jpg".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_JPEG);
		} else if ("png".equalsIgnoreCase(fileExtension)) {
			headers.setContentType(MediaType.IMAGE_PNG);
		} else {

			headers.setContentType(MediaType.IMAGE_JPEG);
		}

		return ResponseEntity.ok().headers(headers).body(resource);
	}
	private String getFileExtensionForImage(Company image) {
		if (image == null || image.getUrl() == null || image.getUrl().isEmpty()) {
			return "jpg";
		}
		String url = image.getUrl();
		if (url.endsWith(".png")) {
			return "png";
		} else if (url.endsWith(".jpg")) {
			return "jpg";
		} else {
			return "jpg";
		}
	}

	@PutMapping("/company/edit/{companyId}")
	public ResponseEntity<?> updateCompany(@PathVariable long companyId,
			@RequestParam(value = "profile", required = false) MultipartFile file,
			@RequestParam(value = "companyName", required = false) String companyName,
			@RequestParam(value = "country", required = false) String country,
			@RequestParam(value = "email", required = false) String email,
			@RequestParam(value = "state", required = false) String state,
			@RequestParam(value = "address", required = false) String address,
			@RequestParam(value = "location", required = false) String location,
			@RequestParam(value = "pincode", required = false) Integer pincode,
			@RequestParam(value = "phoneNumber", required = false) Long phoneNumber,
			@RequestParam(value ="gst", required = false) String gst, 
			@RequestParam(value ="signature", required = false) MultipartFile signature) {
		try {
			Company company = companyService.findById(companyId);
			if (isNullOrEmpty(email)) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "Email cannot be empty");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}

			if (!isValidEmail(email)) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "Invalid email format. Please provide a valid email address.");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}

			if (phoneNumber == null || String.valueOf(phoneNumber).length() < 10) {
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put("Message", "PhoneNumber1 should be exactly 10 digits");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
			}
			if (company == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Company not found.");
			}
			if (file != null && !file.isEmpty()) {
				byte[] bytes = file.getBytes();
				Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
				company.setProfile(blob);
			}
			if (signature != null && !signature.isEmpty()) {
				byte[] bytes1 = signature.getBytes();
				Blob blob1 = new javax.sql.rowset.serial.SerialBlob(bytes1);
				company.setSignature(blob1);
			}
			if (companyName != null) {
				company.setCompanyName(companyName);
			}
			if (country != null) {
				company.setCountry(country);
			}
			if (email != null) {
				company.setEmail(email);
			}

			if (location != null) {
				company.setLocation(location);
			}
			if (gst != null) {
				company.setGst(gst);
			}
			if (state != null) {
				company.setState(state);
			}
			if (address != null) {
				company.setAddress(address);
			}
			if (phoneNumber != null) {
				company.setPhoneNumber(phoneNumber);
			}
			if (pincode != null) {
				company.setPincode(pincode);
			}
			companyService.SaveCompanyDetails(company);

			return ResponseEntity.ok("Company updated successfully. Company ID: " + companyId);
		} catch (IOException | SQLException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating company.");
		}
	}

	@DeleteMapping("/companydelete/{companyId}")
	public ResponseEntity<String> deleteTitle(@PathVariable("companyId") Long companyId) {
		companyService.deleteCompanyId(companyId);
		return ResponseEntity.ok("company details deleted successfully");

	}
}
