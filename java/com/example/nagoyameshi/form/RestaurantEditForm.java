package com.example.nagoyameshi.form;

import java.time.LocalTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantEditForm {
	@NotNull
    private Integer id;    
	
	@NotBlank(message = "店舗名を入力してください。")
    private String name;
	
	@NotBlank(message = "カテゴリーを入力してください。")
	public Category category;
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String description;   
    
    @NotBlank(message = "予算を入力してください。")    
    private String priceRange;  
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "開店時間を入力してください。")
	public LocalTime openAt;		
    
    @NotBlank(message = "閉店時間を入力してください。")
	public LocalTime closeAt;
    
    @NotBlank(message = "定休日を入力してください。")
    private String closedOn;
    
    @NotBlank(message = "席数を入力してください。")    
    private Integer capacity;
       
}
