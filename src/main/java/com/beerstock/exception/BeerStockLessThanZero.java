package com.beerstock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BeerStockLessThanZero extends Exception{
    public BeerStockLessThanZero(Long id){
        super("Beer stock with ID: " + id + " cannot be less than zero.");
    }
}
