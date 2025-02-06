package com.rewardtracker.service;
import com.rewardtracker.model.Customer;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.model.RewardPoints;
import com.rewardtracker.repository.CustomerRepo;
import com.rewardtracker.repository.CustomerTransactionRepo;
import com.rewardtracker.repository.RewardPointsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
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

    
      //Get reward points and additional transaction details by customer ID
     
    public Map<String, Object> getRewardPointsByCustomerId(Long customerId) {
        // Fetch reward points
        List<RewardPoints> rewardList = rewardPointsRepo.findByCustomerId(customerId);
        int totalPoints = rewardList.stream().mapToInt(RewardPoints::getPoints).sum();

        List<Map<String, Object>> monthlyPoints = rewardList.stream().map(reward -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", reward.getRewardMonth());
            map.put("year", reward.getRewardYear());
            map.put("points", reward.getPoints());
            return map;
        }).collect(Collectors.toList());

        // Fetch transactions for additional details
        List<CustomerTransaction> transactions = transactionRepo.findByCustomerId(customerId);
        int totalItems = transactions.size();
        double totalValue = transactions.stream().mapToDouble(CustomerTransaction::getAmount).sum();

        // Formatter for date in the pattern dd-MM-yy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        List<Map<String, Object>> transactionDetails = transactions.stream()
                .map(tx -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("amount", tx.getAmount());
                    // Assuming tx.getDate() returns a LocalDate or LocalDateTime instance.
                    map.put("date", tx.getDate().format(formatter));
                    return map;
                })
                .collect(Collectors.toList());

        // Build the response
        Map<String, Object> response = new HashMap<>();
        response.put("customerId", customerId);
        response.put("totalRewardPoints", totalPoints);
        response.put("monthlyPoints", monthlyPoints);
        response.put("totalItemsPurchased", totalItems);
        response.put("totalTransactionValue", totalValue);
        response.put("transactionDetails", transactionDetails);

        return response;
    }

    
     //Calculate and save reward points for a customer
    
    public Map<String, Object> calculateAndSaveRewardPoints(Long customerId) {
        Optional<Customer> customerOpt = customerRepo.findById(customerId);
        if (customerOpt.isPresent()) {
            Customer customer = customerOpt.get();
            List<CustomerTransaction> transactions = transactionRepo.findByCustomerId(customerId);
            System.out.println("Found " + transactions.size() + " transactions for customerId " + customerId);
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for customerId " + customerId);
                return Collections.emptyMap();
            }

            // Group transactions by year-month (e.g., "2025-1")
            Map<String, List<CustomerTransaction>> transactionsByMonth = transactions.stream()
                    .collect(Collectors.groupingBy(tx -> tx.getDate().getYear() + "-" + tx.getDate().getMonthValue()));

            List<Map<String, Object>> monthlyPointsList = new ArrayList<>();
            int totalPoints = 0;

            for (Map.Entry<String, List<CustomerTransaction>> entry : transactionsByMonth.entrySet()) {
                int monthPoints = entry.getValue().stream().mapToInt(this::calculatePointsForTransaction).sum();
                totalPoints += monthPoints;
                String[] parts = entry.getKey().split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);

                // Update if already exists, otherwise create new entry
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

                // Monthly points map
                Map<String, Object> monthlyData = new HashMap<>();
                monthlyData.put("month", month);
                monthlyData.put("year", year);
                monthlyData.put("points", monthPoints);
                monthlyPointsList.add(monthlyData);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("customerId", customerId);
            response.put("totalPoints", totalPoints);
            response.put("monthlyPoints", monthlyPointsList);

            return response;
        } else {
            System.out.println("Customer not found with id: " + customerId);
            return Collections.singletonMap("error", "Customer not found");
        }
    }

    
     //Logic to calculate reward points for a transaction
     
    private int calculatePointsForTransaction(CustomerTransaction transaction) {
        double amount = transaction.getAmount();
        int points = 0;
        if (amount > 100) {
            points += (int) ((amount - 100) * 2);
            amount = 100;
        }
        if (amount > 50) {
            points += (int) (amount - 50);
        }
        return points;
    }
}
