package com.assignment.blog.ServiceImplementation;
import com.assignment.blog.Exception.ResourceNotFoundException;
import com.assignment.blog.Utility.Validation;
import com.assignment.blog.entity.User;
import com.assignment.blog.repository.UserRepository;
import com.assignment.blog.security.MyUserDetails;
import com.assignment.blog.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;


@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImplementation(UserRepository userRepository , ModelMapper modelMapper ,
                                     BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository        = userRepository;
        this.modelMapper           = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }



    @Override
    public User getUserById(Integer id) {
        return userRepository
                .findById(id)
                .stream()
                .filter(user -> user
                        .getId()
                        .equals(id))
                .findFirst()
                .orElseThrow(()->new ResourceNotFoundException("user not found: "+id));
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByUserName(name);
    }

    @Override
    public User signUpUser(User user) throws Exception {

        if (userRepository.findByUserName(user.getUserName()) != null) {
            throw new ResourceNotFoundException("User name already exist: "+user.getUserName());
        }

        if(Validation.checkPasswordValidation(user.getPassword())){
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }else {
            throw new ResourceNotFoundException("Password should be 6 character and contains with any symbol");
        }

        return userRepository.save(user);
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(userName);

        if (user == null) {
            throw new ResourceNotFoundException("User not found " + userName);
        }

        return new MyUserDetails(user.getUserName() , user.getPassword() , new ArrayList<>() , true , true , true ,
                true);

    }
}
