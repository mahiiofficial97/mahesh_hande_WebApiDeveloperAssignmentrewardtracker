package com.rewardtracker.controller;


import com.rewardtracker.model.Customer;
import com.rewardtracker.model.JsonResponseClass;
import com.rewardtracker.service.CustomerService;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    
    //save Customer
    @PostMapping("/create")
    public JsonResponseClass createCustomer(@RequestBody Customer customer) {
        customerService.createCustomer(customer);
        return new JsonResponseClass("200", "Customer saved successfully with id ="+customer.getId(),"success");
    }

    //get all customer
    @GetMapping
    public JsonResponseClass getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return new JsonResponseClass("200", "Customers retrieved successfully"+customers, "success");
    }

    //getcx by id
    @GetMapping("/{id}")
    public ResponseEntity<JsonResponseClass> getCustomerById(@PathVariable("id") Long id) {
        JsonResponseClass resp = new JsonResponseClass();

        Optional<Customer> cx = customerService.getCustomerById(id);

        if (cx.isPresent()) {
            resp.setStatus("200");
            resp.setMessage(cx.get().toString());
            resp.setResult("successful");
            return ResponseEntity.ok(resp);  // Return HTTP 200
        } else {
            resp.setStatus("404");
            resp.setMessage("Data with ID " + id + " not found");
            resp.setResult("unsuccessful");
            return ResponseEntity.status(404).body(resp);  // Return HTTP 404
        }
    }
    
    
    
 

    //update by id
    @PutMapping("/{id}")
    public JsonResponseClass updateCustomer(@PathVariable Long id, @RequestBody Customer customerDetails) {
        Optional<Customer> updatedCustomer = customerService.updateCustomer(id, customerDetails);
        if (updatedCustomer.isPresent()) {
            return new JsonResponseClass("200", "Customer updated successfully", "success");
        } else {
            return new JsonResponseClass("404", "Customer not found", "failure");
        }
    }
    
//delete by id
    @DeleteMapping("/{id}")
    public JsonResponseClass deleteCustomer(@PathVariable Long id) {
        boolean isDeleted = customerService.deleteCustomer(id);
        if (isDeleted) {
            return new JsonResponseClass("200", "Customer deleted successfully", "success");
        } else {
            return new JsonResponseClass("404", "Customer not found", "failure");
        }
    }
    
    
    @PostMapping("/login")
    public JsonResponseClass loginUser(@RequestBody Customer customer, HttpSession session) {
        return customerService.loginUser(customer, session);
    }

    @PostMapping("/logout")
    public JsonResponseClass logoutUser(HttpSession session) {
        return customerService.logoutUser(session);
    }
}
