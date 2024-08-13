package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.Data;

@Data
public class PremiumMemberSignupForm {
	@NotBlank(message = "メールアドレスを入力してください。")
	@Email(message = "メールアドレスは正しい形式で入力してください。")
	private String email;
	    
	@NotBlank(message = "パスワードを入力してください。")
	@Length(min = 8, message = "パスワードは8文字以上で入力してください。")
	private String password;  
	   
	@NotBlank(message = "パスワード（確認用）を入力してください。")
	private String passwordConfirmation;   
	     
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
	    
	@NotBlank(message = "クレジットカードNo.を入力してください。")
	private String creditCardNumber;         

	@NotBlank(message = "職業を入力してください。")
	private String  occupation;
}
