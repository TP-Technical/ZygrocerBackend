package com.ecommerce.platforms.Services;

import java.util.List;
import java.util.Optional;

import com.ecommerce.platforms.Models.PaymentLink;
import com.ecommerce.platforms.Repos.PaymentLinkRepository;
import org.springframework.stereotype.Service;


@Service
public class PaymentLinkService {
    private final PaymentLinkRepository repository;

    public PaymentLinkService(PaymentLinkRepository repository) {
        this.repository = repository;
    }

    public PaymentLink save(PaymentLink link) {
        return repository.save(link);
    }

    public List<PaymentLink> getLinksByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    // Find PaymentLink by Razorpay payment link ID
    public Optional<PaymentLink> findByRazorpayPaymentLinkId(String razorpayPaymentLinkId) {
        return repository.findByRazorpayPaymentLinkId(razorpayPaymentLinkId);
    }
    
    
    public Optional<PaymentLink> findByReferenceId(String referenceId) {
        return repository.findByReferenceId(referenceId);
    }
}
