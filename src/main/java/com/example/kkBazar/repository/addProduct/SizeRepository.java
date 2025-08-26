package com.example.kkBazar.repository.addProduct;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.kkBazar.entity.addProduct.Size;

public interface SizeRepository extends JpaRepository<Size, Long> {

	Optional<Size> findBySizeName(String sizeName);

}
