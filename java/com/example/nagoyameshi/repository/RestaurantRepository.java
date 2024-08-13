package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> {
	Page<Restaurant> findByNameLike(String keyword, Pageable pageable);
	
	Page<Restaurant> findByCategory_NameLike(String categoryName, Pageable pageable);
	
	Page<Restaurant> findByPriceRange(String priceRange, Pageable pageable);	       	
	   
    Page<Restaurant> findAllByOrderByPriceRange(Pageable pageable); 
        
    List<Restaurant> findTop5ByOrderByCreatedAtDesc();
	
	List<Restaurant> findByNameContaining(String keyword);

	
      
}
