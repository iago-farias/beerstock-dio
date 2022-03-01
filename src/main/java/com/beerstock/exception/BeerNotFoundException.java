package com.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BeerNotFoundException extends Exception{
    public BeerNotFoundException(String beerName){
        super("Beer with name "+ beerName + " not found.");
    }

    public BeerNotFoundException(Long id){
        super("Beer with id "+ id + " not found");
    }
}
