package com.example.kkBazar.repository.addProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.addProduct.Varient;

public interface VarientRepository extends JpaRepository<Varient, Long> {

	Optional<Varient> findByVarientName(String varientName);

}
