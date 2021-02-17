package com.assignment.blog.controller;

import com.assignment.blog.Exception.ResourceNotFoundException;
import com.assignment.blog.entity.User;

import com.assignment.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin("*")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public User createUser(@Valid @RequestBody User user) throws Exception {

//        if(bindingResult.hasErrors()){
//            throw new ResourceNotFoundException("username/password invalid");
//        }
        return userService.signUpUser(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id){
        return userService.getUserById(id);
    }

}
