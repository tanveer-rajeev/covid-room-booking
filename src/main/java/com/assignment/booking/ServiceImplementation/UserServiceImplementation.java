package com.assignment.booking.ServiceImplementation;

import com.assignment.booking.DTO.UserDTO;
import com.assignment.booking.Exception.ResourceNotFoundException;
import com.assignment.booking.Utility.Validation;
import com.assignment.booking.entity.User;
import com.assignment.booking.repository.UserRepository;
import com.assignment.booking.security.MyUserDetails;
import com.assignment.booking.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.logging.Logger;


@Service
public class UserServiceImplementation implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private Logger logger = Logger.getLogger(getClass().getName());

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
                .orElseThrow(() -> new ResourceNotFoundException("user not found: " + id));
    }

    @Override
    public User getUserByName(String name) {
        return userRepository.findByUsername(name);
    }


    @Override
    public ResponseEntity<?> signUpUser(UserDTO userDTO) throws Exception {


        if (userRepository.findByUsername(userDTO.getUsername()) != null) {
            throw new ResourceNotFoundException("- User name already exist: " + userDTO.getUsername());
        }
        if (userDTO.getUsername().length() < 3) {
            throw new ResourceNotFoundException("~ User name should be at least 3 characters ");
        }


        if (Validation.checkPasswordValidation(userDTO.getPassword())) {
            userDTO.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));
        } else {
            throw new Exception("> Password should be 6 characters and contains with any symbol");
        }

        User user = modelMapper.map(userDTO,User.class);
        user = userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.ACCEPTED);
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(userName);

        if (user == null) {
            throw new ResourceNotFoundException("- User name not found in the system: " );
        }

        return new MyUserDetails(user.getUsername() , user.getPassword() , new ArrayList<>() , true , true , true ,
                true);

    }
}
