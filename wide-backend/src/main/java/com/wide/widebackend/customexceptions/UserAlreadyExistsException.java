package com.wide.widebackend.customexceptions;

public class UserAlreadyExistsException extends Exception{

    public UserAlreadyExistsException(String message, Throwable cause){
        super(message,cause);
    }

    public UserAlreadyExistsException(String message){
        super(message);
    }
}
