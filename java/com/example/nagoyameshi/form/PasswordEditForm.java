package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;

public class PasswordEditForm {
	@NotBlank(message = "パスワードを入力してください。")
    private String password;

    @NotBlank(message = "確認用パスワードを入力してください。")
    private String confirmPassword;

    private String token;

    // Getters and Setters
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
