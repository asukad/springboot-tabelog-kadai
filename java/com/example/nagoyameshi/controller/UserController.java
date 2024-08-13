package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.repository.VerificationTokenRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
    private final UserRepository userRepository;    
    private final UserService userService;
    private final VerificationTokenRepository verificationTokenRepository;    
    
    public UserController(UserRepository userRepository, UserService userService, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;    
        this.userService = userService; 
        this.verificationTokenRepository= verificationTokenRepository;
    }    
    
    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {         
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        
        model.addAttribute("user", user);
        model.addAttribute("isPremiumUser", isPremiumUser(user));
        
        return "user/index";
    }

    private boolean isPremiumUser(User user) {
        return user.getRole() != null && "ROLE_PREMIUM".equals(user.getRole().getName());
    }
    
    
    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {        
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());  
        UserEditForm userEditForm = new UserEditForm(user.getId(), user.getEmail(), user.getName(), user.getFurigana(), user.getAge(), 
        		user.getAddress(), user.getPhoneNumber(), user.getOccupation(), user.getCreditCardNumber());
        
        model.addAttribute("userEditForm", userEditForm);
        model.addAttribute("isPremiumUser", isPremiumUser(user));
        
        return "user/edit";
    }   
    
    @PostMapping("/update")
    public String update(@ModelAttribute @Validated UserEditForm userEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        // メールアドレスが変更されており、かつ登録済みであれば、BindingResultオブジェクトにエラー内容を追加する
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(bindingResult.getObjectName(), "email", "すでに登録済みのメールアドレスです。");
            bindingResult.addError(fieldError);                       
        }
        
        if (bindingResult.hasErrors()) {
            return "user/edit";
        }
        
        userService.update(userEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        
        return "redirect:/user";
    } 
    
    @Transactional
    @PostMapping("/delete")
    public String delete(@RequestParam("userId") Integer userId, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {     
        // verification_tokensテーブルの関連データを削除
        verificationTokenRepository.deleteByUserId(userId);
        
        // usersテーブルのデータを削除
        userRepository.deleteById(userId);
                
        redirectAttributes.addFlashAttribute("successMessage", "会員情報を削除しました。");
        
     // ログアウト処理
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        return "redirect:/";
    } 
         
}
