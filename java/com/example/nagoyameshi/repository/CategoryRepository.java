package com.example.nagoyameshi.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.nagoyameshi.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	public Page<Category> findByNameLike(String keyword, Pageable pageable);
	
	public Page<Category> findAllByOrderByCreatedAtDesc(Pageable pageable);
	
	public List<Category> findTop10ByOrderByCreatedAtDesc();

	
}
