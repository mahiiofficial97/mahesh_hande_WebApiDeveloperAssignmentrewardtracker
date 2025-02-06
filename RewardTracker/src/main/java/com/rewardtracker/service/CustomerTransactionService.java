package com.rewardtracker.service;

import com.rewardtracker.globalExceptionhandling.ResourceNotFoundException;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.repository.CustomerTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerTransactionService {

    private final CustomerTransactionRepo transactionRepo;

    @Autowired
    public CustomerTransactionService(CustomerTransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    public CustomerTransaction saveTransaction(CustomerTransaction transaction) {
        return transactionRepo.save(transaction);
    }

    public List<CustomerTransaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepo.findByCustomerId(customerId);
    }

    public CustomerTransaction getTransactionById(Long id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }
    public List<CustomerTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }
    public void deleteTransaction(Long id) {
        CustomerTransaction transaction = getTransactionById(id);
        transactionRepo.delete(transaction);
    }
    
    public CustomerTransaction updateTransaction(Long id, CustomerTransaction transactionDetails) {
        CustomerTransaction existingTransaction = getTransactionById(id);
        existingTransaction.setAmount(transactionDetails.getAmount());
        existingTransaction.setDate(transactionDetails.getDate());
        existingTransaction.setCustomer(transactionDetails.getCustomer());
        existingTransaction.setSpentDetails(transactionDetails.getSpentDetails());
        return transactionRepo.save(existingTransaction);
    }
}