package com.rewardtracker.service;

import com.rewardtracker.model.Customer;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.model.RewardPoints;
import com.rewardtracker.repository.CustomerRepo;
import com.rewardtracker.repository.CustomerTransactionRepo;
import com.rewardtracker.repository.RewardPointsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RewardPointsService {

    @Autowired
    private RewardPointsRepo rewardPointsRepo;

    @Autowired
    private CustomerTransactionRepo transactionRepo;

    @Autowired
    private CustomerRepo customerRepo;

    
     //we have to Retrieve reward points for a given customer.
    //method retrieves all reward point records for the specified customer,
     // sorts them in descending order by year and month (latest first), calculates
   
    public Map<String, Object> getRewardPointsByCustomerId(Long customerId) {
        // Retrieve reward point records for the customer
        List<RewardPoints> rewardList = rewardPointsRepo.findByCustomerId(customerId);

        // Sort records by reward year and month in descending order (latest first)
        rewardList.sort((a, b) -> {
            if (b.getRewardYear() != a.getRewardYear()) {
                return Integer.compare(b.getRewardYear(), a.getRewardYear());
            }
            return Integer.compare(b.getRewardMonth(), a.getRewardMonth());
        });

        // Calculate the total reward points
        int totalPoints = rewardList.stream().mapToInt(RewardPoints::getPoints).sum();

        // Map each reward record to a monthly data map with month name, year, and points
        List<Map<String, Object>> monthlyPoints = rewardList.stream().map(reward -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", Month.of(reward.getRewardMonth()).name());
            map.put("year", reward.getRewardYear());
            map.put("points", reward.getPoints());
            return map;
        }).collect(Collectors.toList());

        // Build and return the response map
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("totalRewardPoints", totalPoints);
        response.put("monthlyPoints", monthlyPoints);
        return response;
    }

    
      //Calculate and save reward points based on customer transactions.
     //This method verifies the customer's existence, retrieves their transactions,
     //groups transactions by month (using the format "year-month"), calculates points

    public Map<String, Object> calculateAndSaveRewardPoints(Long customerId) {
        // Validate that the customer exists
        Optional<Customer> customerOpt = customerRepo.findById(customerId);
        if (customerOpt.isEmpty()) {
            return Collections.singletonMap("error", "Customer not found");
        }
        Customer customer = customerOpt.get();

        // Retrieve all transactions for the customer
        List<CustomerTransaction> transactions = transactionRepo.findByCustomerId(customerId);
        if (transactions.isEmpty()) {
            return Collections.singletonMap("message", "No transactions found for this customer.");
        }

        // Group transactions by year and month (e.g., "2025-1")
        Map<String, List<CustomerTransaction>> transactionsByMonth = transactions.stream()
                .collect(Collectors.groupingBy(tx -> tx.getDate().getYear() + "-" + tx.getDate().getMonthValue()));

        List<Map<String, Object>> monthlyPointsList = new ArrayList<>();
        int totalPoints = 0;

        // Process each month-group of transactions
        for (Map.Entry<String, List<CustomerTransaction>> entry : transactionsByMonth.entrySet()) {
            // Calculate total points for the month using the helper method
            int monthPoints = entry.getValue().stream().mapToInt(this::calculatePointsForTransaction).sum();
            totalPoints += monthPoints;

            // Extract the year and month from the group key
            String[] parts = entry.getKey().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            // Check if a reward record already exists for this month; update if it does, else create a new one
            RewardPoints existingReward = rewardPointsRepo.findByCustomerIdAndRewardMonthAndRewardYear(customer.getId(), month, year);
            if (existingReward != null) {
                existingReward.setPoints(monthPoints);
                rewardPointsRepo.save(existingReward);
            } else {
                RewardPoints newReward = new RewardPoints();
                newReward.setCustomer(customer);
                newReward.setRewardYear(year);
                newReward.setRewardMonth(month);
                newReward.setPoints(monthPoints);
                rewardPointsRepo.save(newReward);
            }

            // Build monthly data for the response
            Map<String, Object> monthlyData = new HashMap<>();
            monthlyData.put("month", Month.of(month).name());
            monthlyData.put("year", year);
            monthlyData.put("points", monthPoints);
            monthlyPointsList.add(monthlyData);
        }

        // Sort the monthly breakdown by year and month in descending order (latest first)
        monthlyPointsList.sort((a, b) -> {
            int yearComparison = Integer.compare((int) b.get("year"), (int) a.get("year"));
            if (yearComparison != 0) {
                return yearComparison;
            }
            return Integer.compare(Month.valueOf((String) b.get("month")).getValue(),
                    Month.valueOf((String) a.get("month")).getValue());
        });

        // Build and return the final response map
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("totalPoints", totalPoints);
        response.put("monthlyPoints", monthlyPointsList);
        return response;
    }

    //Helper method: Calculate reward points for a single transaction.
    // Helper method: Calculate transaction points
    private int calculatePointsForTransaction(CustomerTransaction transaction) {
        double amount = transaction.getAmount();
        int points = 0;
        // Award 2 points for each dollar above $100 and cap the amount to $100
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            amount = 100;
        }
        // Award 1 point for each dollar above $50 (up to $100)
        if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }
}
