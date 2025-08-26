package com.example.kkBazar.controller.addProduct;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.example.kkBazar.entity.payment.CCAvenueRequest;
import com.example.kkBazar.entity.payment.PaymentRequest;

@RestController
@CrossOrigin

public class PaymentController {

	private final String ccAvenueUrl = "http://www.ideaux.in";
	private final String merchantId = "2895899";
	private final String accessCode = "AVHP17KI11CI24PHIC";
	private final String workingKey = "CD9085F36ED4EDE43738D9431052F928";

	@PostMapping("/initiatePayment")
	public ResponseEntity<String> initiatePayment(@RequestBody PaymentRequest paymentRequest) {
		String endpoint = ccAvenueUrl + "/transaction/initiate";
		HttpHeaders headers = new HttpHeaders();

		CCAvenueRequest ccAvenueRequest = new CCAvenueRequest(merchantId, accessCode, workingKey,
				paymentRequest.getAmount(), paymentRequest.getOrderId(), paymentRequest.getRedirectUrl());

		HttpEntity<CCAvenueRequest> httpEntity = new HttpEntity<>(ccAvenueRequest, headers);

		try {
			System.out.println("Request received: " + ccAvenueRequest.toString());

			ResponseEntity<String> response = new RestTemplate().exchange(endpoint, HttpMethod.POST, httpEntity,
					String.class);

			System.out.println("Response received: " + response.getBody());

			return ResponseEntity.ok(response.getBody());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
			return ResponseEntity.status(500).body("Error: " + e.getMessage());
		}
	}

	@GetMapping("/success")
	public String paymentSuccess() {
		return "payment-success";
	}
}