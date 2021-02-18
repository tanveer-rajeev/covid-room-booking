package com.assignment.booking.service;


import com.assignment.booking.entity.User;

import org.springframework.security.core.userdetails.UserDetailsService;


public interface UserService extends UserDetailsService {
    User getUserById(Integer integer);
    User getUserByName(String name);
    User signUpUser(User user) throws Exception;

}
