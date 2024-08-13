package com.example.nagoyameshi.service;

import java.util.Map;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionRetrieveParams;

@Service
public class StripeService {
	@Value("${stripe.api-key}")
    private String stripeApiKey;

	 private final UserService userService;
    
    public StripeService(UserService userService) {
        this.userService = userService;
    }  
  
    public String createStripeSubscriptionSession(HttpServletRequest request, User user) {
        Stripe.apiKey = stripeApiKey;

        SessionCreateParams params = SessionCreateParams.builder()
            .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
            .addLineItem(
                SessionCreateParams.LineItem.builder()
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("jpy")
                            .setUnitAmount(300L)
                            .setRecurring(
                                SessionCreateParams.LineItem.PriceData.Recurring.builder()
                                    .setInterval(SessionCreateParams.LineItem.PriceData.Recurring.Interval.MONTH)
                                    .build()
                            )
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName("月額有料プラン")
                                    .build()
                            )
                            .build()
                    )
                    .setQuantity(1L)
                    .build()
            )
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .setSuccessUrl(request.getRequestURL().toString().replaceAll("/subscription/register", "") + "/subscription/success")
            .setCancelUrl(request.getRequestURL().toString().replaceAll("/subscription/register", "") + "/subscription/cancel")
            .build();

        try {
            Session session = Session.create(params);
            return session.getId();
        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
   	}

    public void processSubscriptionSessionCompleted(Event event) {
   	    Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();
   	    optionalStripeObject.ifPresent(stripeObject -> {
   	     
   	Session session = (Session) stripeObject;
   	 
   	if (session.getPaymentIntent() != null) {
   	            
   	SessionRetrieveParams params = SessionRetrieveParams.builder().addExpand("payment_intent").build();

   	            try {
   	                session = Session.retrieve(session.getId(), params, null);
   	                Map<String, String> paymentIntentObject = session.getPaymentIntentObject().getMetadata();
   	                userService.update(paymentIntentObject);
   	            } 
   	catch (StripeException e) {
   	                e.printStackTrace();
   	            }
   	        } 
   	               
   	else {
   	            Map<String, String> metadata = session.getMetadata();
   	            userService.update(metadata);
   	        }
   	    });
   	}
}

