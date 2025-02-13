package com.rewardtracker.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;

@Getter
@Setter
@Entity
@ToString(exclude = "customer")  // Prevent recursion in toString()
public class CustomerTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Transaction amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Transaction amount must be greater than 0")
    private Double amount;
    
    @NotNull(message = "Transaction date is required")
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate date;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Customer is required")
    @JsonBackReference  // Prevent infinite recursion in JSON
    private Customer customer;
    
    @NotBlank(message = "Spent details are required")
    private String spentDetails;
}
