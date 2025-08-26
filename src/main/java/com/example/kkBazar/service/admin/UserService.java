package com.example.kkBazar.service.admin;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.admin.User;
import com.example.kkBazar.repository.admin.UserRepository;

@Service
public class UserService {
	@Autowired
	private UserRepository userRepository;

//view
	public List<User> listAll() {
		return userRepository.findAll();
	}

	

//save
	public User saveUserDetails(User user) {
		return userRepository.save(user);
	}

	// update
	public User findById(Long id) {
		return userRepository.findById(id).get();
	}
	public User findById(long userId) {
	    return userRepository.findById(userId).orElse(null);
	}

//delete
	public void deleteUserId(Long id) {
		userRepository.deleteById(id);
	}
	
	  public boolean isEmailExists(String email) {
	        User existingUser = userRepository.findByEmailId(email);
	        return existingUser != null;
	    }

	public void changeUserPassword(long userId, String newPassword) {
        Optional<User> optionalUser = userRepository.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setPassword(newPassword);
            userRepository.save(user);
        } else {
            throw new NoSuchElementException("User not found with ID: " + userId);
        }
    }
	
	public User findByEmailId(String emailId) {
        return userRepository.findByEmailId(emailId);
    }
	
	public List<Map<String, Object>> getUserDetailsWithId(Long userId) {
		return userRepository.getUserDetails(userId);
	}
}
