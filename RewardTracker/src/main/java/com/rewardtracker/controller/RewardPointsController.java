package com.rewardtracker.controller;

import com.rewardtracker.service.RewardPointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/reward")
public class RewardPointsController {

    @Autowired
    private RewardPointsService rewardPointsService;

    // Get reward points by customer ID
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<Map<String, Object>> getRewardPoints(@PathVariable Long customerId) {
        Map<String, Object> response = rewardPointsService.getRewardPointsByCustomerId(customerId);
        return ResponseEntity.ok(response);
    }

    // Calculate and save reward points, return total reward points
    @PostMapping("/calculate/{customerId}")
    public ResponseEntity<Map<String, Object>> calculateRewardPoints(@PathVariable Long customerId) {
        Map<String, Object> response = rewardPointsService.calculateAndSaveRewardPoints(customerId);
        return ResponseEntity.ok(response);
    }
}
