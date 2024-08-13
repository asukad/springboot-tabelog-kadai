ocument.addEventListener("DOMContentLoaded", function() {
    const stripe = Stripe('pk_test_51PZr0JLJ2LcWpnob4luZBrJD98cHEpLTAAelVVkNEnMIh6ccM0AWvTYjZYQdqaGB4NDX6K49ol4HynL66ijeO91p00UClyBd21'); // 公開可能キー
    const sessionId = subscriptionSessionId;

	  if (sessionId) {
	        stripe.redirectToCheckout({ sessionId: sessionId }).then(function (result) {
	            // エラーハンドリング
	            if (result.error) {
	                console.error(result.error.message);
	            }
	        });
	    }
	});
