package com.ecommerce.platforms.Repos;

import java.util.Optional;

import com.ecommerce.platforms.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;




@Repository
public interface UserRepository  extends JpaRepository<User, Integer> {
	 Optional<User> findByEmail(String email);
	 Optional<User> findByPhoneNumber(String phoneNumber);
	

}
