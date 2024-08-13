package com.example.nagoyameshi.service;

import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.nagoyameshi.entity.Role;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.FreeMemberSignupForm;
import com.example.nagoyameshi.form.PasswordEditForm;
import com.example.nagoyameshi.form.PremiumMemberSignupForm;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.CardRepository;
import com.example.nagoyameshi.repository.RoleRepository;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.repository.VerificationTokenRepository;

@Service
public class UserService {
	private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CardRepository cardRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
    		CardRepository cardRepository, VerificationTokenRepository verificationTokenRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;        
        this.passwordEncoder = passwordEncoder;
        this.cardRepository = cardRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }    
    
    @Transactional
    public User create(FreeMemberSignupForm signupForm) {
    	User user = new User();
        Role role = roleRepository.findByName("ROLE_FREE");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setRole(role);
        user.setEnabled(false);

        return userRepository.save(user);
    }


    @Transactional
    public User create(PremiumMemberSignupForm signupForm) {
        User user = new User();
        Role role = roleRepository.findByName("ROLE_PREMIUM");

        user.setName(signupForm.getName());
        user.setFurigana(signupForm.getFurigana());
        user.setAge(signupForm.getAge());
        user.setAddress(signupForm.getAddress());
        user.setPhoneNumber(signupForm.getPhoneNumber());
        user.setEmail(signupForm.getEmail());
        user.setPassword(passwordEncoder.encode(signupForm.getPassword()));
        user.setCreditCardNumber(signupForm.getCreditCardNumber());
        user.setOccupation(signupForm.getOccupation());        
        user.setRole(role);
        user.setEnabled(false);

        return userRepository.save(user);
    }
        
 // メールアドレスが登録済みかどうかをチェックする
    public boolean isEmailRegistered(String email) {
        User user = userRepository.findByEmail(email);  
        return user != null;
    }   
    
    // パスワードとパスワード（確認用）の入力値が一致するかどうかをチェックする
    public boolean isSamePassword(String password, String passwordConfirmation) {
        return password.equals(passwordConfirmation);
    }     
    
 // ユーザーを有効にする
    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true); 
        userRepository.save(user);
    }   
    
    @Transactional
    public void update(UserEditForm userEditForm) {
        User user = userRepository.getReferenceById(userEditForm.getId());
        
        user.setEmail(userEditForm.getEmail());  
        user.setName(userEditForm.getName());
        user.setFurigana(userEditForm.getFurigana());
        user.setAge(userEditForm.getAge());
        user.setAddress(userEditForm.getAddress());
        user.setPhoneNumber(userEditForm.getPhoneNumber());        
        user.setOccupation(userEditForm.getOccupation());
        user.setCreditCardNumber(userEditForm.getCreditCardNumber());
        
        userRepository.save(user);
    }    
     
    
 // メールアドレスが変更されたかどうかをチェックする
    public boolean isEmailChanged(UserEditForm userEditForm) {
        User currentUser = userRepository.getReferenceById(userEditForm.getId());
        return !userEditForm.getEmail().equals(currentUser.getEmail());      
    }    
 
    @Transactional
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);        
    }
    
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

	public void update(Map<String, String> paymentIntentObject) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	public void refreshAuthenticationByRole(String string) {
		// TODO 自動生成されたメソッド・スタブ
		
	}

	@Transactional
	public void create(@Valid PasswordEditForm passwordEditForm) {		
		User user = userRepository.getReferenceById(passwordEditForm.getId());
		
		user.setPassword(passwordEncoder.encode(passwordEditForm.getPassword()));
		
		userRepository.save(user);
	}

	public void update(String email) {
		// TODO 自動生成されたメソッド・スタブ
		
	}  
	
	@Transactional
    public void deleteUser(Integer userId) {
        // verification_tokensテーブルの関連データを削除
        verificationTokenRepository.deleteByUserId(userId);

        // usersテーブルのデータを削除
        userRepository.deleteById(userId);
    }
}
