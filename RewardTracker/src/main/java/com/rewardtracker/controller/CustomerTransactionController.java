package com.rewardtracker.controller;

import com.rewardtracker.globalExceptionhandling.ResourceNotFoundException;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.service.CustomerTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/customer-transactions")
public class CustomerTransactionController {

    private final CustomerTransactionService transactionService;

    @Autowired
    public CustomerTransactionController(CustomerTransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<CustomerTransaction> createTransaction(@RequestBody CustomerTransaction transaction) {
        CustomerTransaction savedTransaction = transactionService.saveTransaction(transaction);
        return ResponseEntity.status(201).body(savedTransaction);
    }

    @GetMapping
    public ResponseEntity<List<CustomerTransaction>> getAllTransactions() {
        List<CustomerTransaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }
//glo exception
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerTransaction>> getTransactionsByCustomerId(@PathVariable Long customerId) {
        List<CustomerTransaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        if (transactions.isEmpty()) {
            // Throw a ResourceNotFoundException if no transactions found
            throw new ResourceNotFoundException("No transactions found for customer with ID " + customerId);
        }
        
        return ResponseEntity.ok(transactions);
    }
  

    @PutMapping("/{id}")
    public ResponseEntity<CustomerTransaction> updateTransaction(@PathVariable Long id, @RequestBody CustomerTransaction transactionDetails) {
        CustomerTransaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.ok("Transaction deleted successfully!");
    }

}
