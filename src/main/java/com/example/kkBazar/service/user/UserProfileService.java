package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.companyProfile.Company;
import com.example.kkBazar.entity.user.UserProfile;
import com.example.kkBazar.repository.UserProfileRepository;

@Service
public class UserProfileService {

	@Autowired
	private UserProfileRepository userProfileRepository;

	public List<UserProfile> getUser() {
		return userProfileRepository.findAll();
	}

	public void saveUserProfile(UserProfile userProfile) {
		this.userProfileRepository.save(userProfile);
	}

	public UserProfile findUserProfileById(Long id) {
		return userProfileRepository.findById(id).get();
	}

	// delete
	public void deleteUserProfileId(Long id) {
		userProfileRepository.deleteById(id);
	}
	
	public UserProfile getUserProfileById(Long userProfileId) {

        return userProfileRepository.findById(userProfileId).orElse(null);
    }
}
