package com.example.nagoyameshi.controller;

import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.StripeService;
import com.example.nagoyameshi.service.UserService;
import com.stripe.Stripe;
import com.stripe.model.Subscription;

@Controller
@RequestMapping("/subscription")
public class SubscriptionController {
	private final UserRepository userRepository;
	private final UserService userService;
	
	@Autowired
    private StripeService stripeService1;
    
    @Value("${stripe.api-key}")
    private String stripeApiKey;
	
	public SubscriptionController (UserRepository userRepository,UserService userService){
		this.userRepository = userRepository;
		this.userService = userService;		
	}	
			
	@GetMapping("/register")
    public String index(Model model, HttpServletRequest httpServletRequest, @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        User user = userDetailsImpl.getUser();
        String subscriptionSessionId = stripeService1.createStripeSubscriptionSession(httpServletRequest, user);
        model.addAttribute("subscriptionSessionId", subscriptionSessionId);
        return "subscription/register";
    }
    
    @GetMapping("/success")
    public String success(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, HttpServletRequest request, HttpServletResponse response) {
        // ユーザーのロールを更新
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        Map<String, String> paymentIntentObject = new HashMap<>();
        paymentIntentObject.put("userId", String.valueOf(user.getId()));
        paymentIntentObject.put("roleName", "ROLE_PREMIUM");
        userService.update(paymentIntentObject);

        userService.refreshAuthenticationByRole("ROLE_PREMIUM");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        request.getSession().invalidate();

        return "redirect:/login?reserved";
    }
    
    @PostMapping("/create")
    public String create() {
        return "subscription/create";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "subscription/cancel";
    }

    @PostMapping("/delete")
    public String delete(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, RedirectAttributes redirectAttributes) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());

        Map<String, String> paymentIntentObject = new HashMap<>();
        paymentIntentObject.put("userId", String.valueOf(user.getId()));
        paymentIntentObject.put("roleName", "ROLE_FREE");

        userService.update(paymentIntentObject);
        userService.refreshAuthenticationByRole("ROLE_FREE");
        redirectAttributes.addFlashAttribute("successMessage", "有料プランを解約しました。");

        return "redirect:/";
    }
    
    @PostMapping("/cancel-subscription")
    public ResponseEntity<Map<String, Object>> cancelSubscription(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {
        Stripe.apiKey = stripeApiKey;
        String userId = String.valueOf(userDetailsImpl.getUser().getId());

        // ユーザーIDからサブスクリプションIDを取得する処理を追加
        String subscriptionId = getSubscriptionIdByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        try {
            Subscription subscription = Subscription.retrieve(subscriptionId);
            subscription.cancel();

            // ユーザーのステータスを無料会員に切り替える処理を追加
            updateUserStatusToFree(userId);

            response.put("success", true);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    private String getSubscriptionIdByUserId(String userId) {
        // ユーザーIDからサブスクリプションIDを取得するロジックを実装
        return "sub_1Hh1Y2IyNTgGDV2e9f9Z0eY2"; // 仮のサブスクリプションID
    }

    private void updateUserStatusToFree(String userId) {
        // ユーザーのステータスを無料会員に切り替えるロジックを実装
        User user = userRepository.getReferenceById(Integer.parseInt(userId));
        Map<String, String> paymentIntentObject = new HashMap<>();
        paymentIntentObject.put("userId", userId);
        paymentIntentObject.put("roleName", "ROLE_FREE");
        userService.update(paymentIntentObject);
        userService.refreshAuthenticationByRole("ROLE_FREE");
    }
}

