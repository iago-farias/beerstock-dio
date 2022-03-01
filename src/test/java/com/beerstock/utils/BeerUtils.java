package com.beerstock.utils;

import com.beerstock.dto.BeerDTO;
import com.beerstock.entity.Beer;
import com.beerstock.enums.BeerType;

public class BeerUtils {
    private static final Long id = 1L;
    private static final String name = "Brahma";
    private static final String brand = "Ambev";
    private static final Integer maxQuantity = 50;
    private static final Integer quantity = 10;
    private static final BeerType type = BeerType.LARGER;

    public static BeerDTO createFakeDTO(){
        return BeerDTO
                .builder()
                .id(id)
                .name(name)
                .brand(brand)
                .maxQuantity(maxQuantity)
                .quantity(quantity)
                .type(type)
                .build();
    }

    public static Beer createFakeEntity(){
        return Beer
                .builder()
                .id(id)
                .name(name)
                .brand(brand)
                .maxQuantity(maxQuantity)
                .quantity(quantity)
                .type(type)
                .build();
    }

}
