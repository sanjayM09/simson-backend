package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.BankDetails;
import com.example.kkBazar.repository.user.BankDetailsRepository;

@Service
public class BankDetailsService {
	@Autowired
	private BankDetailsRepository bankRepo;

//view
	public List<BankDetails> listAll() {
		return this.bankRepo.findAll();
	}

//save
	public BankDetails SaveBankDetails(BankDetails bank) {
		return bankRepo.save(bank);
	}

	// edit
	public BankDetails findById(Long bankId) {
		return bankRepo.findById(bankId).get();
	}

	// delete
	public void deleteBankId(Long id) {
		bankRepo.deleteById(id);
	}
}
