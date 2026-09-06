package com.repoly.backend.exception;

public class InvalidCredentialsException extends RuntimeException{
        public InvalidCredentialsException(String message){
            super(message);
        }
}