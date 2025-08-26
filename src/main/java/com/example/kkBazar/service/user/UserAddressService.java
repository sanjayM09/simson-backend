package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.UserAddress;
import com.example.kkBazar.repository.user.UserAddressRepository;

@Service
public class UserAddressService {

	@Autowired
	private UserAddressRepository userAddressRepository;
	
	
	public List<UserAddress> listAll() {
		return userAddressRepository.findAll();
	}

	public void saveAddressDetails(UserAddress userAddress) {
		this.userAddressRepository.save(userAddress);
	}

	public UserAddress findAddressById(Long id) {
		return userAddressRepository.findById(id).get();
	}

	public void deleteUserAddressById(Long userAddressId) {
		userAddressRepository.deleteById(userAddressId);
	}
	
}
