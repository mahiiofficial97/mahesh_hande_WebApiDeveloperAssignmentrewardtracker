package com.rewardtracker.controller;

import com.rewardtracker.model.Admin;
import com.rewardtracker.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admins")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }
    

    /**
     * Create a new admin.
     *
     * Example URL: http://localhost:8080/admins
     * Example Payload:
     * {
     *   "username": "admin",
     *   "password": "admin123",
     *   "email": "admin@example.com"
     * }
     *
     * Note: The role is set to "ADMIN" by default in the Admin entity.
     *
     * @param admin the admin data.
     * @return the created Admin.
     */
    @PostMapping
    public ResponseEntity<Admin> createAdmin(@RequestBody Admin admin) {
        Admin savedAdmin = adminService.createAdmin(admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }
}
