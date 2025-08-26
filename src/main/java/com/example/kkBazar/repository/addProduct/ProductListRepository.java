package com.example.kkBazar.repository.addProduct;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.product.ProductList;

public interface ProductListRepository extends JpaRepository<ProductList, Long> {

	@Query(value = "select p.product_id,p.product_name,pl.product_list_id from product as p"
			+ " join product_list as pl on pl.product_id = p.product_id"
			+ " where pl.product_list_id = :product_list_id", nativeQuery = true)
	List<Map<String, Object>> getProductName(Long product_list_id);

}
