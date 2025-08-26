package com.example.kkBazar.repository.user;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.kkBazar.entity.user.OrderItemList;

public interface OrderIemListRepository extends JpaRepository<OrderItemList, Long>{
	
	
	@Query(value="select pl.return_count,pl.return_type,oil.order_item_list_id,oil.product_list_id,oil.delivered_date from order_item_list as oil"
			+ " join product_list as pl on pl.product_list_id=oil.product_list_id"
			+ " where oil.order_item_list_id=:order_item_list_id", nativeQuery = true)
	List<Map<String, Object>> getAllRetuenDetails(Long order_item_list_id);

}
