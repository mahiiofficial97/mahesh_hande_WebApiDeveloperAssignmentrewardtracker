package com.rewardtracker.repository;

import com.rewardtracker.model.Customer;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepo extends JpaRepository<Customer, Long> {

	
	@Query("SELECT a FROM Customer a WHERE  a.email=:email")
	Customer findByEmail(@Param ("email")String email);
}
