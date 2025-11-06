package com.ecommerce.platforms.Repos;

import java.util.List;
import java.util.Optional;

import com.ecommerce.platforms.Models.PaymentLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PaymentLinkRepository extends JpaRepository<PaymentLink, Long> {

    List<PaymentLink> findByUserId(int userId);



	Optional<PaymentLink> findByRazorpayPaymentLinkId(String razorpayPaymentLinkId);
	

    Optional<PaymentLink> findByReferenceId(String referenceId);

}