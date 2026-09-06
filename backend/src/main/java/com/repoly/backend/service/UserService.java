package com.repoly.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.repoly.backend.dto.LoginDto;
import com.repoly.backend.dto.SignupDto;
import com.repoly.backend.entity.User;
import com.repoly.backend.exception.InvalidCredentialsException;
import com.repoly.backend.exception.UserAlreadyExsistsException;
import com.repoly.backend.repository.UserRepositorty;

@Service 
public class UserService {

    @Autowired
    private UserRepositorty userRepo;

    @Autowired
    private JwtService jwtService;

    public User signup(SignupDto signup) {

        User username = userRepo.getByUsername(signup.getUsername());
        if (username != null) {
            throw new UserAlreadyExsistsException("user already exists");
        }

        User user = new User();
        user.setEmail(signup.getEmail());
        user.setPassword(signup.getPassword());
        user.setUsername(signup.getUsername());

        return userRepo.save(user);
        
    }

    public String login(LoginDto login) {

        User user = userRepo.getByUsername(login.getUsername());

        if (user == null || !user.getPassword().equals(login.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        return jwtService.generateToken(user.getUsername());
    }

}
