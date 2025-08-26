package com.example.kkBazar.service.user;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.kkBazar.entity.user.WishList;
import com.example.kkBazar.repository.user.WishListRepository;

@Service
public class WishListService {

	@Autowired
	private WishListRepository wishListRepository;

	// view
	public List<WishList> listAll() {
		return this.wishListRepository.findAll();
	}

	// save
	public WishList SaveWishListDetails(WishList wishList) {
		return wishListRepository.save(wishList);
	}

	// delete
	public void deleteWishListId(Long id) {
		wishListRepository.deleteById(id);
	}

	public Optional<WishList> findByUserIdAndProductListIdAndProductImagesId(long userId, long productListId, long productImagesId) {
		return wishListRepository.findByUserIdAndProductListIdAndProductImagesId(userId, productListId, productImagesId);
	}

	public void deleteWishList(long wishListId) {
		wishListRepository.deleteById(wishListId);
	}


}
