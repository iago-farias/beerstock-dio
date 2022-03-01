package com.beerstock.service;

import com.beerstock.dto.BeerDTO;
import com.beerstock.entity.Beer;
import com.beerstock.exception.BeerAlreadyRegisteredException;
import com.beerstock.exception.BeerNotFoundException;
import com.beerstock.exception.BeerStockExceededException;
import com.beerstock.exception.BeerStockLessThanZero;
import com.beerstock.mapper.BeerMapper;
import com.beerstock.repository.BeerRepository;
import com.beerstock.utils.BeerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BeerServiceTest {

    @Mock
    private BeerRepository beerRepository;

    private BeerMapper beerMapper = BeerMapper.INSTANCE;

    @InjectMocks
    private BeerService underTest;

    @Test
    void whenBeerInformedThenItShouldBeCreated() throws BeerAlreadyRegisteredException {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();
        Beer expectedSavedBeer = beerMapper.beerDTOToBeer(beerDTO);

        // when
        Mockito.when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.save(expectedSavedBeer)).thenReturn(expectedSavedBeer);

        BeerDTO returnedValue = underTest.createBeer(beerDTO);

        // then
        assertThat(returnedValue).isEqualTo(beerDTO);
    }

    @Test
    void whenBeerInformedThenItShouldThrowBeerAlreadyRegisteredException() {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();
        Beer beerRegistered = BeerUtils.createFakeEntity();

        // when
        Mockito.when(beerRepository.findByName(beerDTO.getName())).thenReturn(Optional.of(beerRegistered));

        // then
        assertThatThrownBy(() -> underTest.createBeer(beerDTO))
                .isInstanceOf(BeerAlreadyRegisteredException.class)
                .hasMessage("Beer with name "+ beerDTO.getName()+ " already registered in the system.");

    }

    @Test
    void whenValidBeerNameIsGivenThenShouldReturnABeer() throws BeerNotFoundException {
        // given
        BeerDTO expectedFoundBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedFoundBeer = beerMapper.beerDTOToBeer(expectedFoundBeerDTO);

        // when
        Mockito.when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.of(expectedFoundBeer));

        BeerDTO returnedBeer = underTest.findByName(expectedFoundBeerDTO.getName());

        // then
        assertThat(returnedBeer).isEqualTo(expectedFoundBeerDTO);

    }

    @Test
    void whenNoRegisteredBeerNameIsGivenThenShouldThrowBeerNotFoundException() {
        // given
        BeerDTO expectedFoundBeerDTO = BeerUtils.createFakeDTO();

        // when
        Mockito.when(beerRepository.findByName(expectedFoundBeerDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTest.findByName(expectedFoundBeerDTO.getName()))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessage("Beer with name "+ expectedFoundBeerDTO.getName() + " not found.");
    }

    @Test
    void whenListBeerIsCalledThenReturnListOfBeers(){
        //given
        BeerDTO expectedFoundBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedFoundBeerDTO);

        //when
        Mockito.when(beerRepository.findAll()).thenReturn(Collections.singletonList(expectedBeer));
        List<BeerDTO> returnedListDTO = underTest.listAll();

        //then
        assertThat(returnedListDTO.get(0)).isEqualTo(expectedFoundBeerDTO);
    }

    @Test
    void whenListBeerIsCalledThenReturnEmptyListOfBeers(){
        //when
        Mockito.when(beerRepository.findAll()).thenReturn(Collections.EMPTY_LIST);
        List<BeerDTO> returnedListDTO = underTest.listAll();

        //then
        assertThat(returnedListDTO).isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    void whenDeleteBeerIsCalledWithAValidBeerIdThenShouldReturnDeletedBeer() throws BeerNotFoundException {
        //given
        BeerDTO expectedFoundBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedFoundBeerDTO);

        //when
        Mockito.when(beerRepository.findById(expectedFoundBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        BeerDTO returnedBeer = underTest.deleteById(expectedFoundBeerDTO.getId());

        //then
        Mockito.verify(beerRepository, Mockito.times(1)).findById(expectedFoundBeerDTO.getId());
        Mockito.verify(beerRepository, Mockito.times(1)).deleteById(expectedFoundBeerDTO.getId());

        assertThat(returnedBeer).isEqualTo(expectedFoundBeerDTO);
    }

    @Test
    void whenDeleteBeerIsCalledWithAInvalidBeerIdThenThrowBeerNotFoundException() throws BeerNotFoundException {
        //given
        BeerDTO expectedFoundBeerDTO = BeerUtils.createFakeDTO();

        //when
        Mockito.when(beerRepository.findById(expectedFoundBeerDTO.getId())).thenReturn(Optional.empty());

        //then
        Mockito.verify(beerRepository, Mockito.times(0)).deleteById(expectedFoundBeerDTO.getId());

        assertThatThrownBy(() ->  underTest.deleteById(expectedFoundBeerDTO.getId()))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessage("Beer with id "+ expectedFoundBeerDTO.getId() + " not found");
    }

    @Test
    void whenUpdateBeerIsCalledWithValidIdAndValidNameShouldUpdateAndReturnBeer() throws BeerNotFoundException, BeerAlreadyRegisteredException {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedUpdatedBeerDTO);

        //when
        Mockito.when(beerRepository.findByName(expectedUpdatedBeerDTO.getName())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.findById(expectedUpdatedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        Mockito.when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        BeerDTO returnedBeer = underTest.updateBeer(expectedUpdatedBeerDTO.getId(),expectedUpdatedBeerDTO);

        //then
        assertThat(returnedBeer).isEqualTo(expectedUpdatedBeerDTO);
    }

    @Test
    void whenUpdateBeerIsCalledWithInvalidBeerNameShouldThrowBeerAlreadyRegisteredException() {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedUpdatedBeer = beerMapper.beerDTOToBeer(expectedUpdatedBeerDTO);

        //when
        Mockito.when(beerRepository.findByName(expectedUpdatedBeerDTO.getName())).thenReturn(Optional.of(expectedUpdatedBeer));

        //then
        assertThatThrownBy(() -> underTest.updateBeer(expectedUpdatedBeerDTO.getId(), expectedUpdatedBeerDTO))
                .isInstanceOf(BeerAlreadyRegisteredException.class)
                .hasMessage("Beer with name "+ expectedUpdatedBeerDTO.getName() + " already registered in the system.");

        Mockito.verify(beerRepository, Mockito.never()).findById(expectedUpdatedBeerDTO.getId());
        Mockito.verify(beerRepository, Mockito.never()).save(expectedUpdatedBeer);
    }

    @Test
    void whenUpdateBeerIsCalledWithInvalidIdShouldThrowBeerNotFoundException() {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedUpdatedBeer = beerMapper.beerDTOToBeer(expectedUpdatedBeerDTO);

        //when
        Mockito.when(beerRepository.findByName(expectedUpdatedBeerDTO.getName())).thenReturn(Optional.empty());
        Mockito.when(beerRepository.findById(expectedUpdatedBeerDTO.getId())).thenReturn(Optional.empty());

        //then
        assertThatThrownBy(() -> underTest.updateBeer(expectedUpdatedBeerDTO.getId(), expectedUpdatedBeerDTO))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessage("Beer with id "+ expectedUpdatedBeerDTO.getId() + " not found");

        Mockito.verify(beerRepository, Mockito.never()).save(expectedUpdatedBeer);
    }

    @Test
    void whenIncrementIsCalledThenIncrementBeerStock() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() + quantityToIncrement;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        Mockito.when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // then
        BeerDTO incrementedBeerDTO = underTest.increment(expectedBeerDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement).isEqualTo(incrementedBeerDTO.getQuantity());
        assertThat(expectedQuantityAfterIncrement).isLessThan(incrementedBeerDTO.getMaxQuantity());
    }

    @Test
    void whenIncrementIsCalledWithInvalidBeerIdShouldThrowBeerNotFoundException() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToIncrement = 10;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTest.increment(expectedBeerDTO.getId(), quantityToIncrement ))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessage("Beer with id "+ expectedBeerDTO.getId() + " not found");

        Mockito.verify(beerRepository, Mockito.never()).save(expectedBeer);
    }


    @Test
    void whenIncrementIsGreaterThanMaxShouldThrowBeerStockExceededException() throws BeerNotFoundException, BeerStockExceededException {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToIncrement = 100;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        // then
        assertThatThrownBy(() -> underTest.increment(expectedBeerDTO.getId(), quantityToIncrement ))
                .isInstanceOf(BeerStockExceededException.class)
                .hasMessage("Beers with "+ expectedBeerDTO.getId() +" ID to increment informed exceeds the max stock capacity: " + quantityToIncrement);

        Mockito.verify(beerRepository, Mockito.never()).save(expectedBeer);
    }

    @Test
    void whenDecrementIsCalledThenDecrementBeerStock() throws BeerNotFoundException, BeerStockLessThanZero {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToDecrement = 5;
        int expectedQuantityAfterIncrement = expectedBeerDTO.getQuantity() - quantityToDecrement;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));
        Mockito.when(beerRepository.save(expectedBeer)).thenReturn(expectedBeer);

        // then
        BeerDTO incrementedBeerDTO = underTest.decrement(expectedBeerDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterIncrement).isEqualTo(incrementedBeerDTO.getQuantity());
    }

    @Test
    void whenDecrementIsCalledWithInvalidBeerIdShouldThrowBeerNotFoundException() {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToDecrement = 5;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> underTest.decrement(expectedBeerDTO.getId(), quantityToDecrement ))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessage("Beer with id "+ expectedBeerDTO.getId() + " not found");

        Mockito.verify(beerRepository, Mockito.never()).save(expectedBeer);
    }

    @Test
    void whenDecrementIsCalledAndStockWillBeLessThanZeroShouldThrowBeerStockLessThanZeroException() {
        //given
        BeerDTO expectedBeerDTO = BeerUtils.createFakeDTO();
        Beer expectedBeer = beerMapper.beerDTOToBeer(expectedBeerDTO);
        int quantityToDecrement = 11;

        //when
        Mockito.when(beerRepository.findById(expectedBeerDTO.getId())).thenReturn(Optional.of(expectedBeer));

        //then
        assertThatThrownBy(() -> underTest.decrement(expectedBeerDTO.getId(), quantityToDecrement))
                .isInstanceOf(BeerStockLessThanZero.class)
                .hasMessage("Beer stock with ID: " + expectedBeerDTO.getId() + " cannot be less than zero.");

        Mockito.verify(beerRepository, Mockito.never()).save(expectedBeer);
    }

}
