package com.example.kkBazar.entity.admin;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
@Service
public class EmailOtpService {

	
	 @Autowired
	    private JavaMailSender mailSender;

	    public void sendOtp(String to, String otp) {
	        MimeMessage mimeMessage = mailSender.createMimeMessage();
	        MimeMessageHelper helper;

	        try {
	            helper = new MimeMessageHelper(mimeMessage, true);
	            helper.setSubject("Your OTP Code");
	            helper.setTo(to);
	            helper.setText("Your OTP code is: " + otp, true);
	            mailSender.send(mimeMessage);
	        } catch (MessagingException e) {
	            e.printStackTrace();
	            // Handle exception
	        }
	    }
}
