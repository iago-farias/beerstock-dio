package com.beerstock.controller;

import com.beerstock.dto.BeerDTO;
import com.beerstock.dto.QuantityDTO;
import com.beerstock.exception.BeerAlreadyRegisteredException;
import com.beerstock.exception.BeerNotFoundException;
import com.beerstock.exception.BeerStockExceededException;
import com.beerstock.exception.BeerStockLessThanZero;
import com.beerstock.service.BeerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/beer")
public class BeerController {
    @Autowired
    private BeerService beerService;

    @PostMapping
    public ResponseEntity<BeerDTO> createBeer(@RequestBody @Valid BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        BeerDTO createdBeer = beerService.createBeer(beerDTO);

        return ResponseEntity.ok().body(createdBeer);
    }

    @GetMapping("/{beerName}")
    public ResponseEntity<BeerDTO> findByName(@PathVariable(value = "beerName") String beerName) throws BeerNotFoundException {
        BeerDTO beer = beerService.findByName(beerName);

        return ResponseEntity.ok().body(beer);
    }

    @GetMapping
    public ResponseEntity<List<BeerDTO>> listAll() {
        List<BeerDTO> beerList = beerService.listAll();

        return ResponseEntity.ok().body(beerList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BeerDTO> updateBeer(@PathVariable(value = "id")Long id, @RequestBody @Valid BeerDTO beerDTO) throws BeerNotFoundException, BeerAlreadyRegisteredException {
        BeerDTO updatedBeer = beerService.updateBeer(id, beerDTO);

        return ResponseEntity.ok().body(updatedBeer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<BeerDTO> deleteById(@PathVariable(value = "id") Long id) throws BeerNotFoundException {
        BeerDTO deletedBeer = beerService.deleteById(id);

        return ResponseEntity.ok().body(deletedBeer);
    }

    @PatchMapping("/{id}/increment")
    public ResponseEntity<BeerDTO> increment(@PathVariable(value = "id") Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockExceededException {
        BeerDTO updatedBeer = beerService.increment(id,quantityDTO.getQuantity());

        return ResponseEntity.ok().body(updatedBeer);
    }

    @PatchMapping("/{id}/decrement")
    public ResponseEntity<BeerDTO> decrement(@PathVariable(value = "id") Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws BeerNotFoundException, BeerStockLessThanZero {
        BeerDTO updatedBeer = beerService.decrement(id, quantityDTO.getQuantity());

        return ResponseEntity.ok().body(updatedBeer);
    }
}
