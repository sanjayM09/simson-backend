package com.example.kkBazar.service.whatsapp;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.kkBazar.entity.user.OrderItem;
import com.example.kkBazar.entity.user.OrderItemList;
import com.example.kkBazar.service.user.OrderItemService;
import com.example.kkBazar.service.whatsapp.pdf.Pdf;
import com.example.kkBazar.service.whatsapp.pdf.PdfRepository;
import com.example.kkBazar.service.whatsapp.pdf.PdfService;

@RestController
@CrossOrigin
public class ApiController {

	@Autowired
	private ApiService apiService;

	@Autowired
	private PdfService pdfService;

	@Autowired
	private OrderItemService orderItemService;

	@Autowired
	private PdfRepository pdfRepository;

	@Autowired
	private ApiPdfService apiPdfService;

	@GetMapping("/send-sms")
	public ResponseEntity<String> sendSms(@RequestParam String mobileNumber, @RequestParam String message,
			@RequestParam String img1) {
		String response = apiService.sendSMS(mobileNumber, message, img1);
		return ResponseEntity.ok(response);
	}

	private int generateRandomNumber() {
		Random random = new Random();
		return random.nextInt(1000000);
	}

	private String getFileExtensionForImage(Pdf pdf) {
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

	@PostMapping("/send-pdf")
	public ResponseEntity<?> sendWhatsappPdf(@RequestBody Pdf pdf) {
		String base64Image = pdf.getPdfUrl();
		if (base64Image != null) {
			byte[] imageBytes = Base64.getDecoder().decode(base64Image);
			Blob blob = null;
			try {
				blob = new javax.sql.rowset.serial.SerialBlob(imageBytes);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			pdf.setPdf(blob);
		}
		pdfService.savePdf(pdf);

		long id = pdf.getPdfId();
		int randomNumber = generateRandomNumber();
		String fileExtension = getFileExtensionForImage(pdf);
		String imageUrl = "https://dev.api.simsongarments.com/" + "pdf/" + randomNumber + "/" + id + "."
				+ fileExtension;
		pdf.setUrl(imageUrl);
		pdfService.savePdf(pdf);
		String message = pdf.getMessage();
		String mobileNumber = pdf.getMobileNumber();
		String pdfUrl = pdf.getUrl();
		String response = apiPdfService.sendPdf(mobileNumber, message, pdfUrl);
		Map<String, Object> ob = new HashMap<>();
		ob.put("Message", "Successfully saved pdf");
		return ResponseEntity.ok(response);
	}

	@GetMapping("pdf/{randomNumber}/{id:.+}")
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

		Pdf image = pdfService.findById(imageId);

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

	@GetMapping("/invoicePdfDownload/{id}/{orderItemId}")
	public List<Map<String, Object>> getInvoicePdf(@PathVariable("id") Long userId,
			@PathVariable("orderItemId") Long orderItemId) {
		List<Map<String, Object>> invoiceList = new ArrayList<>();
		List<Map<String, Object>> getInvoice = pdfRepository.getInvoicePdf(userId, orderItemId);
		Map<String, Object> invoiceMap = new HashMap<>();
		int randomNumber = generateRandomNumber();
		for (Map<String, Object> invoiceLoop : getInvoice) {
			String url = "pdf/" + randomNumber + "/" + invoiceLoop.get("pdfId");
			invoiceMap.put("url", url);
			invoiceMap.putAll(invoiceLoop);

			invoiceList.add(invoiceMap);
		}
		return invoiceList;
	}
}
