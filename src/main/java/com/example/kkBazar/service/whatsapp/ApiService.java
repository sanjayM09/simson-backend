package com.example.kkBazar.service.whatsapp;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {

	private final String apiUrl = "http://bulkwhatsapp.live/whatsapp/api/send";
	private final String apiKey = "f988c8a585074c2c86914159ba7b1dc4";

	public String sendSMS(String mobileNumber, String message, String img1) {
		RestTemplate restTemplate = new RestTemplate();

		String url = apiUrl + "?apikey=" + apiKey + "&mobile=" + mobileNumber + "&msg=" + message + "&img1=" + img1;

		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, null, String.class);

		if (response.getStatusCode().is2xxSuccessful()) {
			return response.getBody();
		} else {
			return "Failed to send SMS. HTTP Status Code: " + response.getStatusCodeValue();
		}
	}
}
	
