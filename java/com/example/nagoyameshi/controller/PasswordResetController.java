package com.example.nagoyameshi.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.PasswordEditForm;
import com.example.nagoyameshi.form.PasswordResetForm;
import com.example.nagoyameshi.service.PasswordResetService;

@Controller
@RequestMapping("/password/reset")
public class PasswordResetController {
	
	private final PasswordResetService passwordResetService;	
	private final JavaMailSender mailSender;

    PasswordResetController(PasswordResetService passwordResetService, JavaMailSender mailSender) {
        this.passwordResetService = passwordResetService;
    	this.mailSender = mailSender;
    }

    @GetMapping
    public String showPasswordResetForm(Model model) {
        model.addAttribute("passwordResetForm", new PasswordResetForm());
        return "auth/passwordReset";
    }

    @PostMapping
    public String handlePasswordReset(@Valid PasswordResetForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/passwordReset";
        }

        String email = form.getEmail();
        User user = passwordResetService.findUserByEmail(email);

        if (user == null) {
            model.addAttribute("errorMessage", "メールアドレスが登録されていません。");
            return "auth/passwordReset";
        }

        String token = UUID.randomUUID().toString();
        passwordResetService.createPasswordResetTokenForUser(user, token);

        String resetUrl = "http://localhost:8080/password/reset/edit?token=" + token;

        SimpleMailMessage emailMessage = new SimpleMailMessage();
        emailMessage.setTo(email);
        emailMessage.setSubject("パスワードリセットリクエスト");
        emailMessage.setText("以下のURLよりリセットしてください。\n" + resetUrl + "\n");

        try {
            mailSender.send(emailMessage);
            model.addAttribute("successMessage", "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");
        } catch (MailException e) {
            model.addAttribute("errorMessage", "メール送信に失敗しました。");
            e.printStackTrace(); // デバッグ用にスタックトレースをコンソールに出力
        }

        return "auth/passwordReset";
    }

    @GetMapping("/edit")
    public String showPasswordEditForm(@RequestParam("token") String token, Model model) {
        String result = passwordResetService.validatePasswordResetToken(token);
        if (result != null) {
            model.addAttribute("errorMessage", "トークンが無効または期限切れです。");
            return "auth/passwordReset";
        }

        PasswordEditForm passwordEditForm = new PasswordEditForm();
        passwordEditForm.setToken(token);
        model.addAttribute("passwordEditForm", passwordEditForm);
        return "auth/passwordEdit";
    }
    
    @PostMapping("/update")
    public String updatePassword(@Valid PasswordEditForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/passwordEdit";
        }

        String result = passwordResetService.validatePasswordResetToken(form.getToken());
        if (result != null) {
            model.addAttribute("errorMessage", "トークンが無効または期限切れです。");
            return "auth/passwordEdit";
        }
        
        User user = passwordResetService.getUserByPasswordResetToken(form.getToken());
        if (user == null) {
            model.addAttribute("errorMessage", "ユーザーが見つかりませんでした。");
            return "auth/passwordEdit";
        }
        
        passwordResetService.updatePassword(user, form.getPassword());
        
        return "redirect:/login";
    }
}