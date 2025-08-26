//package com.example.kkBazar.service.user;
//
//
//import java.io.IOException;
//import org.springframework.core.io.ClassPathResource;
//
//import javax.annotation.PostConstruct;
//import java.io.InputStream;
//import org.springframework.stereotype.Service;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.Message;
//import com.google.firebase.messaging.Notification;
//
//@Service
//public class FirebaseNotificationService {
//
//	
//	 @PostConstruct
//	    public void initializeFirebase() {
//	        try {
//	            // Check if the default app is already initialized
//	            if (FirebaseApp.getApps().isEmpty()) {
//	                // Load the service account key JSON file from the resources folder
//	                InputStream serviceAccount = new ClassPathResource("kk-bazar-firebase-adminsdk-n9ml6-0c28bddac3.json").getInputStream();
//
//	                FirebaseOptions options = new FirebaseOptions.Builder()
//	                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//	                    .build();
//
//	                FirebaseApp.initializeApp(options);
//	                System.out.println("FirebaseApp initialized successfully.");
//	            } else {
//	                System.out.println("FirebaseApp [DEFAULT] already initialized.");
//	            }
//	        } catch (IOException e) {
//	            System.err.println("Error initializing Firebase: " + e.getMessage());
//	            e.printStackTrace();
//	        }
//	    }
//	  
//	  
//	  public void sendNotificationToUser(String token, String title, String body) {
//		    try {
//		        Notification notification = Notification.builder()
//		            .setTitle(title)
//		            .setBody(body)
//		            .build();
//
//		        Message message = Message.builder()
//		            .setNotification(notification)
//		            .setToken(token)
//		            .build();
//
//		        String response = FirebaseMessaging.getInstance().send(message);
//		        System.out.println("Successfully sent message: " + response);
//
//		    } catch (Exception e) {
//		        System.err.println("Error sending notification: " + e.getMessage());
//		        e.printStackTrace();
//		    }
//		}
//
//}
