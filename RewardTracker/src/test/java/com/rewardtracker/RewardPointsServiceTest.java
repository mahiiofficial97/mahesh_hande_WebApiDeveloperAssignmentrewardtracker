package com.rewardtracker;

import com.rewardtracker.model.RewardPoints;
import com.rewardtracker.repository.RewardPointsRepo;
import com.rewardtracker.service.RewardPointsService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RewardPointsServiceTest {

    @Mock
    private RewardPointsRepo rewardPointsRepo;

    @InjectMocks
    private RewardPointsService rewardPointsService;

    private RewardPoints rewardJune;
    private RewardPoints rewardJuly;

    @BeforeEach
    public void setUp() {
        // Create a reward record for June 2025 with 30 points.
        rewardJune = new RewardPoints();
        rewardJune.setRewardMonth(6);  // 6 represents June
        rewardJune.setRewardYear(2025);
        rewardJune.setPoints(40);

        // Create a reward record for July 2025 with 20 points.
        rewardJuly = new RewardPoints();
        rewardJuly.setRewardMonth(7);  // 7 represents July
        rewardJuly.setRewardYear(2025);
        rewardJuly.setPoints(20);
    }

    @Test
    public void testGetRewardPointsByCustomerId() {
        // Given: a customer with ID 1 has two reward records (June and July 2025)
        Long customerId = 1L;
        List<RewardPoints> rewards = Arrays.asList(rewardJune, rewardJuly);
        when(rewardPointsRepo.findByCustomerId(customerId)).thenReturn(rewards);

        // When: the service method is invoked
        Map<String, Object> result = rewardPointsService.getRewardPointsByCustomerId(customerId);

        // Print the result to console for debugging
        System.out.println("Test Output: " + result);

        // Then: verify the returned values
        assertThat(result).isNotNull();
        assertThat(result.get("customerId")).isEqualTo(customerId);
        assertThat(result.get("totalRewardPoints")).isEqualTo(50);

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> monthlyPoints = (List<Map<String, Object>>) result.get("monthlyPoints");
        assertThat(monthlyPoints).hasSize(2);

        // Verify sorting order: July should come before June
        Map<String, Object> firstEntry = monthlyPoints.get(0);
        assertThat(firstEntry.get("month")).isEqualTo("JULY");
        assertThat(firstEntry.get("year")).isEqualTo(2025);
        assertThat(firstEntry.get("points")).isEqualTo(20);

        Map<String, Object> secondEntry = monthlyPoints.get(1);
        assertThat(secondEntry.get("month")).isEqualTo("JUNE");
        assertThat(secondEntry.get("year")).isEqualTo(2025);
        assertThat(secondEntry.get("points")).isEqualTo(30);
    }
}
