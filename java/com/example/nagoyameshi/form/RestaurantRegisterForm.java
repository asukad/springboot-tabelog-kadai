package com.example.nagoyameshi.form;

import java.time.LocalTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import com.example.nagoyameshi.entity.Category;

import lombok.Data;

@Data
public class RestaurantRegisterForm {
	@NotBlank(message = "店舗名を入力してください。")
    private String name;
	
	@NotBlank(message = "カテゴリーを入力してください。")
	public Category category;		
        
    private MultipartFile imageFile;
    
    @NotBlank(message = "説明を入力してください。")
    private String description;   
    
    @NotNull(message = "予算を入力してください。")
    @Min(value = 1, message = "料金は1円以上に設定してください。")
    private String priceRange;  
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "開店時間を入力してください。")
	public LocalTime getOpenAt() {
		
		return null;
	}

    @NotBlank(message = "閉店時間を入力してください。")
    public LocalTime getCloseAt() {
		
		return null;
	}
    
    @NotBlank(message = "定休日を入力してください。")
    private String closedOn;
    
    @NotNull(message = "席数を入力してください。")
    @Min(value = 1, message = "席数は1席以上に設定してください。")
    private Integer capacity;

    
}     
    

