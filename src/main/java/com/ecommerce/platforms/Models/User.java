package com.ecommerce.platforms.Models;


import jakarta.persistence.*;

@Entity
@Table(name = "users_zygrocer_ecommerce")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
            
	@Column(unique = true, nullable = false)
    private String email;  

	@Column(unique = true, nullable = false)
    private String phoneNumber;
	
    private String userName;
    private String password;
    
    private String razorpay_customer_id;
    
    private String razorpay_entity;
    
    

    public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

    public String getRazorpay_customer_id() {
		return razorpay_customer_id;
	}

	public void setRazorpay_customer_id(String razorpay_customer_id) {
		this.razorpay_customer_id = razorpay_customer_id;
	}

	public String getRazorpay_entity() {
		return razorpay_entity;
	}

	public void setRazorpay_entity(String razorpay_entity) {
		this.razorpay_entity = razorpay_entity;
	}

	public User() {}



	

	public User(int id, String email, String phoneNumber, String userName, String password, String razorpay_customer_id,
			String razorpay_entity) {
		super();
		this.id = id;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.userName = userName;
		this.password = password;
		this.razorpay_customer_id = razorpay_customer_id;
		this.razorpay_entity = razorpay_entity;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", phoneNumber=" + phoneNumber + ", userName=" + userName
				+ ", password=" + password + ", razorpay_customer_id=" + razorpay_customer_id + ", razorpay_entity="
				+ razorpay_entity + "]";
	}



}
