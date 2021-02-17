package com.assignment.blog.security;

import com.assignment.blog.Exception.ResourceNotFoundException;
import com.assignment.blog.SpringApplicationContext;
import com.assignment.blog.entity.User;
import com.assignment.blog.service.UserService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class CurrentLoggedInUser {


        public static User getUserEntity() {
            Authentication authentication = SecurityContextHolder
                    .getContext().getAuthentication();
            String username = authentication.getName();

            UserService employeeService =
                    (UserService) SpringApplicationContext.getBean("userServiceImplementation");
            return employeeService.getUserByName(username);

        }


}
