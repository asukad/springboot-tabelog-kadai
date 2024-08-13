package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserEditForm {
	@NotNull
    private Integer id;
	
    @NotBlank(message = "メールアドレスを入力してください。")
    private String email;  
    
    @NotBlank(message = "氏名を入力してください。")
    private String name;
    
    @NotBlank(message = "フリガナを入力してください。")
    private String furigana;
    
    @NotNull(message = "年齢を入力してください。")
    private Integer age;
    
    @NotBlank(message = "住所を入力してください。")
    private String address;
    
    @NotBlank(message = "電話番号を入力してください。")
    private String phoneNumber;
    
    @NotBlank(message = "職業を入力してください。")
    private String occupation;
    
    @NotBlank(message = "クレジットカードNo.を入力してください。")
    private String creditCardNumber;
}
