package com.rewardtracker.controller;

import com.rewardtracker.globalExceptionhandling.ResourceNotFoundException;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.service.CustomerTransactionService;
import com.rewardtracker.service.RewardPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/customer-transactions")
public class CustomerTransactionController {

    @Autowired
    private RewardPointsService rewardPointsService;

    private final CustomerTransactionService transactionService;

    @Autowired
    public CustomerTransactionController(CustomerTransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    /**
     * Create a new transaction and return a composite response with reward points.
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody CustomerTransaction transaction) {
        // Save the transaction
        CustomerTransaction savedTransaction = transactionService.saveTransaction(transaction);
        // Retrieve reward points for the customer associated with the transaction
        Long customerId = savedTransaction.getCustomer().getId();
        Map<String, Object> rewardResponse = rewardPointsService.calculateAndSaveRewardPoints(customerId);
        // Build composite response
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("transaction", savedTransaction);
        response.put("rewardPoints", rewardResponse);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Retrieve all transactions.
     */
    @GetMapping
    public ResponseEntity<List<CustomerTransaction>> getAllTransactions() {
        List<CustomerTransaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Retrieve transactions for a given customer.
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerTransaction>> getTransactionsByCustomerId(@PathVariable Long customerId) {
        List<CustomerTransaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        if (transactions.isEmpty()) {
            throw new ResourceNotFoundException("No transactions found for customer with ID " + customerId);
        }
        return ResponseEntity.ok(transactions);
    }

    /**
     * Update a transaction.
     * The update logic is secured in the service (only ADMIN can update).
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomerTransaction> updateTransaction(@PathVariable Long id,
                                                                  @RequestBody CustomerTransaction transactionDetails) {
        CustomerTransaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    /**
     * Delete a transaction.
     * The delete logic is secured in the service (only ADMIN can delete).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully!");
    }
}
