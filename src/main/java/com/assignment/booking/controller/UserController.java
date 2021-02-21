package com.assignment.booking.controller;

import com.assignment.booking.DTO.UserDTO;
import com.assignment.booking.SpringApplicationContext;
import com.assignment.booking.entity.User;

import com.assignment.booking.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO user) throws Exception {

        return userService.signUpUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id){
        return userService.getUserById(id);
    }


//    @GetMapping("/logOut")
//    public void logout(){
////        System.out.println("hi");
////        Authentication authentication = SecurityContextHolder
////                .getContext().getAuthentication();
////        System.out.println(authentication.isAuthenticated());
////        authentication.setAuthenticated(false);
////        System.out.println(authentication.isAuthenticated());
//        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                   null , null, null);
//        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//    }
}
