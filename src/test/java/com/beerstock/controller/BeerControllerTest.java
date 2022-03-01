package com.beerstock.controller;

import com.beerstock.dto.BeerDTO;
import com.beerstock.dto.QuantityDTO;
import com.beerstock.exception.BeerAlreadyRegisteredException;
import com.beerstock.exception.BeerNotFoundException;
import com.beerstock.exception.BeerStockExceededException;
import com.beerstock.exception.BeerStockLessThanZero;
import com.beerstock.service.BeerService;
import com.beerstock.utils.BeerUtils;
import com.beerstock.utils.JsonConvertionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class BeerControllerTest {
    private static final String BEER_API_URL_PATH = "/api/v1/beer";
    private static final long VALID_BEER_ID = 1L;
    private static final long INVALID_BEER_ID = 2L;
    private static final String BEER_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String BEER_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private BeerService beerService;

    @InjectMocks
    private BeerController beerController;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(beerController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((viewName, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenShouldCreateBeer() throws Exception {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();

        // when
        Mockito.when(beerService.createBeer(beerDTO)).thenReturn(beerDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(beerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(beerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenShouldReturnAnError() throws Exception {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();
        beerDTO.setName(null);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post(BEER_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonConvertionUtils.asJsonString(beerDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidBeerNameThenReturnBeer() throws Exception {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();

        // when
        Mockito.when(beerService.findByName(beerDTO.getName())).thenReturn(beerDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(beerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(beerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithNotRegisteredBeerNameThenReturnNotFoundStatus() throws Exception {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();

        // when
        Mockito.when(beerService.findByName(beerDTO.getName())).thenThrow(BeerNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH + "/" + beerDTO.getName())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListBeersIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        BeerDTO beerDTO = BeerUtils.createFakeDTO();

        // when
        Mockito.when(beerService.listAll()).thenReturn(Collections.singletonList(beerDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(BEER_API_URL_PATH)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(beerDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(beerDTO.getBrand())))
                .andExpect(jsonPath("$[0].maxQuantity", is(beerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$[0].quantity", is(beerDTO.getQuantity())))
                .andExpect(jsonPath("$[0].type", is(beerDTO.getType().toString())));
    }

    @Test
    void whenDELETEIsCalledWithValidBeerIdThenReturnDeletedBeer() throws Exception {
        //given
        BeerDTO expectedDeletedBeerDTO = BeerUtils.createFakeDTO();

        //when
        Mockito.when(beerService.deleteById(expectedDeletedBeerDTO.getId())).thenReturn(expectedDeletedBeerDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + expectedDeletedBeerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedDeletedBeerDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedDeletedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(expectedDeletedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(expectedDeletedBeerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(expectedDeletedBeerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(expectedDeletedBeerDTO.getType().toString())));
    }

    @Test
    void whenDELETEIsCalledWithInvalidBeerIdThenReturnNotFoundStatus() throws Exception {
       //when
        Mockito.when(beerService.deleteById(INVALID_BEER_ID)).thenThrow(BeerNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.delete(BEER_API_URL_PATH + "/" + INVALID_BEER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPUTIsCalledWithValidBeerIdThenReturnUpdatedBeer() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();

        //when
        Mockito.when(beerService.updateBeer(expectedUpdatedBeerDTO.getId(), expectedUpdatedBeerDTO)).thenReturn(expectedUpdatedBeerDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.put(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(expectedUpdatedBeerDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUpdatedBeerDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedUpdatedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(expectedUpdatedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(expectedUpdatedBeerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(expectedUpdatedBeerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(expectedUpdatedBeerDTO.getType().toString())));
    }

    @Test
    void whenPUTIsCalledWithInvalidBeerIdThenReturnNotFoundStatus() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();

        //when
        Mockito.when(beerService.updateBeer(expectedUpdatedBeerDTO.getId(),expectedUpdatedBeerDTO))
                .thenThrow(BeerNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.put(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(expectedUpdatedBeerDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenIncrementIsCalledWithValidBeerIdAndValidQuantityThenReturnUpdatedBeer() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(10);

        //when
        Mockito.when(beerService.increment(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity())).thenReturn(expectedUpdatedBeerDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUpdatedBeerDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedUpdatedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(expectedUpdatedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(expectedUpdatedBeerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(expectedUpdatedBeerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(expectedUpdatedBeerDTO.getType().toString())));
    }

    @Test
    void whenIncrementIsCalledWithInvalidBeerIdAndValidQuantityThenReturnNotFoundStatus() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(10);

        //when
        Mockito.when(beerService.increment(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity()))
                .thenThrow(BeerNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenIncrementIsCalledWithValidBeerIdAndInvalidQuantityThenReturnBadRequestStatus() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(55);

        //when
        Mockito.when(beerService.increment(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity()))
                .thenThrow(BeerStockExceededException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_INCREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenDecrementIsCalledWithValidBeerIdAndValidQuantityThenReturnUpdatedBeer() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(10);

        //when
        Mockito.when(beerService.decrement(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity())).thenReturn(expectedUpdatedBeerDTO);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedUpdatedBeerDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedUpdatedBeerDTO.getName())))
                .andExpect(jsonPath("$.brand", is(expectedUpdatedBeerDTO.getBrand())))
                .andExpect(jsonPath("$.maxQuantity", is(expectedUpdatedBeerDTO.getMaxQuantity())))
                .andExpect(jsonPath("$.quantity", is(expectedUpdatedBeerDTO.getQuantity())))
                .andExpect(jsonPath("$.type", is(expectedUpdatedBeerDTO.getType().toString())));
    }

    @Test
    void whenDecrementIsCalledWithInvalidBeerIdAndValidQuantityThenReturnNotFoundStatus() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(10);

        //when
        Mockito.when(beerService.decrement(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity()))
                .thenThrow(BeerNotFoundException.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenDecrementIsCalledWithValidBeerIdAndInvalidQuantityThenReturnBadRequestStatus() throws Exception {
        //given
        BeerDTO expectedUpdatedBeerDTO = BeerUtils.createFakeDTO();
        QuantityDTO quantityDTO = new QuantityDTO(55);

        //when
        Mockito.when(beerService.decrement(expectedUpdatedBeerDTO.getId(), quantityDTO.getQuantity()))
                .thenThrow(BeerStockLessThanZero.class);

        //then
        mockMvc.perform(MockMvcRequestBuilders.patch(BEER_API_URL_PATH + "/" + expectedUpdatedBeerDTO.getId() + BEER_API_SUBPATH_DECREMENT_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonConvertionUtils.asJsonString(quantityDTO)))
                .andExpect(status().isBadRequest());
    }
}
