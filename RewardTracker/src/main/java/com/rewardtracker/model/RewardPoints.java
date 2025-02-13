package com.rewardtracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor  // Default constructor required by JPA
@Entity
@Table(name = "reward_points")
public class RewardPoints {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Reward month is required")
    @Column(name = "reward_month")
    private int rewardMonth;
    
    @NotNull(message = "Reward year is required")
    @Column(name = "reward_year")
    private int rewardYear;
    
    @NotNull(message = "Points are required")
    private int points;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @NotNull(message = "Customer is required")
    @JsonBackReference  // Prevents infinite recursion in REST API response
    private Customer customer;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;  // Tracks when the reward was added

    // Custom constructor to match the parameters used in RewardPointsService
    public RewardPoints(int rewardMonth, int rewardYear, int points, Customer customer) {
        this.rewardMonth = rewardMonth;
        this.rewardYear = rewardYear;
        this.points = points;
        this.customer = customer;
    }
}
