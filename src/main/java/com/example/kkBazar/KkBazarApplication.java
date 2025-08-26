package com.example.kkBazar;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KkBazarApplication {

	public static void main(String[] args) {

		SpringApplication.run(KkBazarApplication.class, args);

		InetAddress localhost;
		try {
			localhost = InetAddress.getLocalHost();
			System.out.println("Localhost IP Address: " + localhost);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}
	
//	@Bean
//	FirebaseMessaging firebasellessaging() throws IOException {
//	GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource("kk-bazar-firebase-adminsdk-n9ml6-0c28bddac3.json").getInputStream());
//	FirebaseOptions firebaseOptions = FirebaseOptions.builder().setCredentials(googleCredentials).build();
//	FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions, "my-app");
//	return FirebaseMessaging.getInstance(app);
//			
//	}
}
