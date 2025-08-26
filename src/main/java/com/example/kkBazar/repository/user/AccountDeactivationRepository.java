package com.example.kkBazar.repository.user;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.user.AccountDeactivation;

public interface AccountDeactivationRepository extends JpaRepository<AccountDeactivation, Long> {

}
