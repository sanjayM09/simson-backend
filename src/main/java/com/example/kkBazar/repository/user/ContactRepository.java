package com.example.kkBazar.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.user.Contact;

public interface ContactRepository extends JpaRepository<Contact, Long> {

}
