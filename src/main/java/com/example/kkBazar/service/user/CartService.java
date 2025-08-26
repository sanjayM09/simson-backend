package com.example.kkBazar.service.user;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.user.AddToCart;
import com.example.kkBazar.repository.user.CartRepository;

@Service
public class CartService {
	@Autowired
	private CartRepository cartRepository;

//view
	public List<AddToCart> listAll() {
		return this.cartRepository.findAll();
	}

	// save
	public AddToCart SaveCartDetails(AddToCart cart) {
		return cartRepository.save(cart);
	}

	// delete
	public void deleteCartId(Long id) {
		cartRepository.deleteById(id);
	}
	public AddToCart findById(Long id) {
		return cartRepository.findById(id).get();

	}
	public Optional<AddToCart> findByUserIdAndProductListId(long userId, long productListId,
			long productImagesId) {
		return cartRepository.findByUserIdAndProductListId(userId, productListId);
	}

	public void deleteCart(long addToCartId) {
		cartRepository.deleteById(addToCartId);
	}
}
