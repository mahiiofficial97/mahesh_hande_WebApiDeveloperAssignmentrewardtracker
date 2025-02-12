package com.rewardtracker.service;

import com.rewardtracker.model.Admin;
import com.rewardtracker.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    /**
     * Create a new Admin.
     * @param admin the admin details to save.
     * @return the saved Admin.
     */
    public Admin createAdmin(Admin admin) {
        // You can add additional logic here (e.g., validation, password encoding, etc.)
        return adminRepository.save(admin);
    }
}
