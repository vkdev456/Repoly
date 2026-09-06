package com.repoly.backend.exception;

public class UserAlreadyExsistsException extends RuntimeException{
      public UserAlreadyExsistsException(String message){
           super(message);
      }
}