package com.example.kkBazar.service.user;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.kkBazar.entity.user.Contact;
import com.example.kkBazar.repository.user.ContactRepository;



@Service
public class ContactService {
	
	
	@Autowired
	private ContactRepository repo;

	public Iterable<Contact> listAll() {
		return this.repo.findAll();

	}

	public void SaveorUpdate(Contact contact) {
		repo.save(contact);
	}

	public Contact findById(Long contactId) {
		return repo.findById(contactId).get();

	}
	
	public void deleteById(Long contactId) {
		repo.deleteById(contactId);

	}

}
