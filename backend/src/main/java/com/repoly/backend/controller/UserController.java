package com.repoly.backend.controller;

import java.util.Map;   
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.repoly.backend.dto.LoginDto;
import com.repoly.backend.dto.SignupDto;
import com.repoly.backend.entity.User;
import com.repoly.backend.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupDto signup){
        User user=userService.signup(signup);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("signupsuccessfull",user.getUsername()));
        
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody LoginDto login){
        String response=userService.login(login);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
}