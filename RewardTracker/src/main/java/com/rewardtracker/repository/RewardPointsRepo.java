package com.rewardtracker.repository;

import com.rewardtracker.model.RewardPoints;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardPointsRepo extends JpaRepository<RewardPoints, Long> {

    //  Find all reward points for a customer
    List<RewardPoints> findByCustomerId(Long customerId);
    void deleteByCustomerId(Long customerId);


    // Find reward points for a specific customer, month, and year
    RewardPoints findByCustomerIdAndRewardMonthAndRewardYear(Long customerId, int rewardMonth, int rewardYear);
}
