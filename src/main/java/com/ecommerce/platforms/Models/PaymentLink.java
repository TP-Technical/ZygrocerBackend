package com.ecommerce.platforms.Models;


import jakarta.persistence.*;

@Entity
@Table(name = "zygrocer_payment_links")
public class PaymentLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "razorpayLinkId")
    private String razorpayPaymentLinkId;
    private String shortUrl;
    @Column(name = "reference_id")
    private String referenceId;
    private String status;
    private int amount;
    private String currency;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "customer_phone")
    private String customerPhone;

    // 👇 link to User
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    
    @Column(name = "razorpay_payment_id")
    private String razorpayPaymentId;

    public String getRazorpayPaymentId() { return razorpayPaymentId; }
    public void setRazorpayPaymentId(String razorpayPaymentId) { this.razorpayPaymentId = razorpayPaymentId; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazorpayPaymentLinkId() { return razorpayPaymentLinkId; }
    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) { this.razorpayPaymentLinkId = razorpayPaymentLinkId; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}