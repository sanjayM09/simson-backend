package com.example.kkBazar.repository.addProduct;

import java.sql.Blob;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.kkBazar.entity.product.Product;
import com.example.kkBazar.entity.product.ProductImages;

public interface ProductImagesRepository extends JpaRepository<ProductImages, Long> {

	@Modifying
	@Query(value = "INSERT INTO product_images (product_id, product_images_upload_url, product_images_upload) VALUES (:productId, :productImagesUploadUrl, :productImagesUpload)", nativeQuery = true)
	@Transactional
	void saveProductDetails(@Param("productId") long productId,
			@Param("productImagesUploadUrl") String productImagesUploadUrl,
			@Param("productImagesUpload") byte[] productImagesUpload);

	@Modifying
	@Query(value = "INSERT INTO product_varient_images (product_list_id, product_varient_image_url, product_varient_image) VALUES (:productListId, :productVarientImageUrl, :productVarientImage)", nativeQuery = true)
	@Transactional
	void saveVarientImageDetails(@Param("productListId") long productListId,
			@Param("productVarientImageUrl") String productVarientImageUrl,
			@Param("productVarientImage") byte[] productVarientImage);

	@Modifying
	@Query(value = "insert into product_varient(varient_name, varient_value, product_list_id) values (:varientName, :varientValue, :productListId)", nativeQuery = true)
	@Transactional
	void saveVarientDetails(@Param("varientName") String varientName, @Param("varientValue") String varientValue,
			@Param("productListId") long productListId);

}
