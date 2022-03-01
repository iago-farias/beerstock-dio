package com.beerstock.service;

import com.beerstock.dto.BeerDTO;
import com.beerstock.entity.Beer;
import com.beerstock.exception.BeerAlreadyRegisteredException;
import com.beerstock.exception.BeerNotFoundException;
import com.beerstock.exception.BeerStockExceededException;
import com.beerstock.exception.BeerStockLessThanZero;
import com.beerstock.mapper.BeerMapper;
import com.beerstock.repository.BeerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeerService {
    @Autowired
    private BeerRepository beerRepository;
    private final BeerMapper beerMapper = BeerMapper.INSTANCE;

    public BeerDTO createBeer(BeerDTO beerDTO) throws BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());

        Beer beer = beerMapper.beerDTOToBeer(beerDTO);

        Beer createdBeer = beerRepository.save(beer);

        return beerMapper.beerToBeerDTO(createdBeer);
    }

    public BeerDTO findByName(String beerName) throws BeerNotFoundException{
        Beer beer = verifyIfExists(beerName);

        return beerMapper.beerToBeerDTO(beer);
    }

    public List<BeerDTO> listAll(){
        return beerRepository.findAll()
                .stream()
                .map(beerMapper::beerToBeerDTO)
                .collect(Collectors.toList());
    }

    public BeerDTO deleteById(Long id) throws BeerNotFoundException{
       Beer beerDeleted = verifyIfExists(id);

       beerRepository.deleteById(id);

       return beerMapper.beerToBeerDTO(beerDeleted);
    }

    public BeerDTO updateBeer(Long id, BeerDTO beerDTO) throws BeerNotFoundException, BeerAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(beerDTO.getName());
        verifyIfExists(id);

        beerDTO.setId(id);

        Beer updatedBeer = beerRepository.save(beerMapper.beerDTOToBeer(beerDTO));

        return beerMapper.beerToBeerDTO(updatedBeer);
    }

    private Beer verifyIfIsAlreadyRegistered(String name) throws BeerAlreadyRegisteredException{
        Optional<Beer> beer = beerRepository.findByName(name);

        if (beer.isPresent()){
            throw new BeerAlreadyRegisteredException(name);
        }

        if (beer.isEmpty()){
            return null;
        }

        return beer.get();
    }

    private Beer verifyIfExists(String name) throws BeerNotFoundException{
        Optional<Beer> beer = beerRepository.findByName(name);

        if (beer.isEmpty()){
            throw new BeerNotFoundException(name);
        }

        return beer.get();
    }

    private Beer verifyIfExists(Long id) throws BeerNotFoundException{
        Optional<Beer> beer = beerRepository.findById(id);

        if (beer.isEmpty()){
            throw new BeerNotFoundException(id);
        }

        return beer.get();
    }

    public BeerDTO increment(Long id, int quantityToIncrement) throws BeerNotFoundException, BeerStockExceededException {
       Beer beer = verifyIfExists(id);

       if(beer.getQuantity() + quantityToIncrement > beer.getMaxQuantity()){
           throw new BeerStockExceededException(id, quantityToIncrement);
       }

       beer.setQuantity(beer.getQuantity() + quantityToIncrement);
       Beer updatedBeer = beerRepository.save(beer);

       return beerMapper.beerToBeerDTO(updatedBeer);
    }

    public BeerDTO decrement(Long id, int quantityToDecrement) throws BeerNotFoundException, BeerStockLessThanZero {
        Beer beer = verifyIfExists(id);

        if(beer.getQuantity() - quantityToDecrement < 0){
            throw new BeerStockLessThanZero(id);
        }

        beer.setQuantity(beer.getQuantity() - quantityToDecrement);
        Beer updatedBeer = beerRepository.save(beer);

        return beerMapper.beerToBeerDTO(updatedBeer);
    }
}
