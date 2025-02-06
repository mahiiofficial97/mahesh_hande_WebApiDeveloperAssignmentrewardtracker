package com.rewardtracker.controller;

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

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerTransaction>> getTransactionsByCustomerId(@PathVariable Long customerId) {
        List<CustomerTransaction> transactions = transactionService.getTransactionsByCustomerId(customerId);
        return ResponseEntity.ok(transactions);
    }

  

    @PutMapping("/{id}")
    public ResponseEntity<CustomerTransaction> updateTransaction(@PathVariable Long id, @RequestBody CustomerTransaction transactionDetails) {
        CustomerTransaction updatedTransaction = transactionService.updateTransaction(id, transactionDetails);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
