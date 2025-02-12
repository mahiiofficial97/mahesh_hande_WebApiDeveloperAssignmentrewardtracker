package com.rewardtracker.service;

import com.rewardtracker.model.Customer;
import com.rewardtracker.model.JsonResponseClass;
import com.rewardtracker.repository.CustomerRepo;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepo customerRepo;

    public String createCustomer(Customer customer) {
        Customer existingCustomer = customerRepo.findByEmail(customer.getEmail());
        if (existingCustomer != null) {
            logger.warn("Customer with email {} already exists", customer.getEmail());
            return "Customer with email " + customer.getEmail() + " already exists.";
        }
        // Save the customer and retrieve the saved entity (with generated id)
        Customer savedCustomer = customerRepo.save(customer);
        logger.info("Customer created successfully with email {}", savedCustomer.getEmail());
        return "Customer created successfully with id = " + savedCustomer.getId();
    }

    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        Optional<Customer> customer = customerRepo.findById(id);
        if (customer.isPresent()) {
            logger.info("Customer found with ID {}", id);
        } else {
            logger.warn("Customer with ID {} not found", id);
        }
        return customer;
    }

    public Optional<Customer> updateCustomer(Long id, Customer customerDetails) {
        return customerRepo.findById(id).map(customer -> {
            customer.setName(customerDetails.getName());
            customer.setEmail(customerDetails.getEmail());
            customer.setPassword(customerDetails.getPassword());
            return customerRepo.save(customer);
        });
    }

    public boolean deleteCustomer(Long id) {
        return customerRepo.findById(id).map(customer -> {
            // Remove related customer transactions first
            customer.getSpentDetails().clear();  //  Clears the list in JPA
            customerRepo.save(customer);  // Update to reflect the change

            customerRepo.delete(customer);  //  Now delete the customer
            return true;
        }).orElse(false);
    }

    public JsonResponseClass loginUser(Customer customer, HttpSession session) {
    	JsonResponseClass response = new JsonResponseClass();

        if (session.getAttribute("userInstance") != null) {
            response.setMessage("You are already logged in!");
            response.setResult("success");
            response.setStatus("200");
            return response;
        }

        Customer existingCustomer = customerRepo.findByEmail(customer.getEmail());
        if (existingCustomer != null) {
            if (customer.getPassword().equals(existingCustomer.getPassword())) { // Plain text password comparison
                session.setAttribute("userInstance", existingCustomer);
                response.setMessage("Login successful!");
                response.setResult("success");
                response.setStatus("200");
            } else {
                response.setMessage("Invalid credentials! Incorrect password.");
                response.setResult("failure");
                response.setStatus("401");
            }
        } else {
            response.setMessage("No account found with this email.");
            response.setResult("unsuccessful");
            response.setStatus("404");
        }

        return response;
    }

    public JsonResponseClass logoutUser(HttpSession session) {
    	JsonResponseClass response = new JsonResponseClass();

        if (session.getAttribute("userInstance") == null) {
            response.setMessage("You are already logged out!");
            response.setResult("success");
            response.setStatus("200");
        } else {
            session.invalidate();
            response.setMessage("Logout successful!");
            response.setResult("success");
            response.setStatus("200");
        }

        return response;
    }
}
