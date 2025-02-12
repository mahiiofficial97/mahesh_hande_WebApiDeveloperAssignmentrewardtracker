package com.rewardtracker.service;

import com.rewardtracker.globalExceptionhandling.ResourceNotFoundException;
import com.rewardtracker.model.CustomerTransaction;
import com.rewardtracker.repository.CustomerTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerTransactionService {

    private final CustomerTransactionRepo transactionRepo;

    @Autowired
    public CustomerTransactionService(CustomerTransactionRepo transactionRepo) {
        this.transactionRepo = transactionRepo;
    }

    /**
     * Save a new customer transaction.
     */
    public CustomerTransaction saveTransaction(CustomerTransaction transaction) {
        return transactionRepo.save(transaction);
    }

    /**
     * Retrieve all transactions for a given customer.
     */
    public List<CustomerTransaction> getTransactionsByCustomerId(Long customerId) {
        return transactionRepo.findByCustomerId(customerId);
    }

    /**
     * Retrieve a transaction by its ID.
     */
    public CustomerTransaction getTransactionById(Long id) {
        return transactionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + id));
    }

    /**
     * Retrieve all transactions.
     */
    public List<CustomerTransaction> getAllTransactions() {
        return transactionRepo.findAll();
    }

    /**
     * Delete a transaction.
     * <p>
     * Only a user with the ADMIN role can delete transactions.
     * </p>
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTransaction(Long id) {
        CustomerTransaction transaction = getTransactionById(id);
        transactionRepo.delete(transaction);
    }

    /**
     * Update an existing transaction.
     * <p>
     * Only a user with the ADMIN role can update transactions.
     * </p>
     */
    @PreAuthorize("hasRole('ADMIN')")
    public CustomerTransaction updateTransaction(Long id, CustomerTransaction transactionDetails) {
        CustomerTransaction existingTransaction = getTransactionById(id);
        existingTransaction.setAmount(transactionDetails.getAmount());
        existingTransaction.setDate(transactionDetails.getDate());
        existingTransaction.setCustomer(transactionDetails.getCustomer());
        existingTransaction.setSpentDetails(transactionDetails.getSpentDetails());
        return transactionRepo.save(existingTransaction);
    }
}
