package com.example.kkBazar.service.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.user.AccountDeactivation;
import com.example.kkBazar.repository.user.AccountDeactivationRepository;

@Service
public class AccountDeactivationService {

	@Autowired
	private AccountDeactivationRepository accountDeactivationRepository;
	
	

	//view
		public List<AccountDeactivation> listAll() {
			return this.accountDeactivationRepository.findAll();
		}

	//save
		public AccountDeactivation SaveAccountDeactivation(AccountDeactivation accountDeactivation) {
			return accountDeactivationRepository.save(accountDeactivation);
		}
		
		

		//delete
			public void deleteAccountDeactivationId(Long id) {
				accountDeactivationRepository.deleteById(id);
			}
}
