package com.assignment.booking.service;


import com.assignment.booking.DTO.UserDTO;
import com.assignment.booking.entity.User;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User getUserById(Integer integer);
    User getUserByName(String name);
    ResponseEntity<?> signUpUser(UserDTO user) throws Exception;

}
