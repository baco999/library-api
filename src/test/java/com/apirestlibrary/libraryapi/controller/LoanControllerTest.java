package com.apirestlibrary.libraryapi.controller;

import com.apirestlibrary.libraryapi.api.dto.LoanDTO;
import com.apirestlibrary.libraryapi.api.dto.LoanFilterDTO;
import com.apirestlibrary.libraryapi.api.dto.ReturnedLoanDTO;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.service.BookService;
import com.apirestlibrary.libraryapi.service.LoanService;
import com.apirestlibrary.libraryapi.service.LoanServiceTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = LoanController.class)
public class LoanControllerTest {

    static final String LOAN_URL = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService bookService;

    @MockBean
    LoanService loanService;

    @Test
    @DisplayName("Deve realizar um empréstimo")
    public void creatLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("123").customer("silva").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));

        Loan loan = Loan.builder()
                .id(1l)
                .customer("silva")
                .book(book)
                .loanDate(LocalDate.now())
                .build();

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));

    }

    @Test
    @DisplayName("Deve retornar erro caso tente fazer um empréstimo de um livro inexistente")
    public void returnedErrorWithLoanBook() throws Exception {

        LoanDTO dto = LoanDTO.builder().isbn("001").customer("silva").build();

        String json = new ObjectMapper().writeValueAsString(dto);

        BDDMockito.given(bookService.getBookByIsbn("002")).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Livro não encontrado com esse isbn fornecido"));
    }

    @Test
    @DisplayName("Deve retornar erro caso tente fazer um empréstimo de um livro que ja foi emprestado")
    public void loanedBookErrorOnCreatedLoanTest() throws Exception {

        LoanDTO dto = LoanDTO.builder()
                .isbn("123")
                .customer("silva")
                .build();

        String json = new ObjectMapper().writeValueAsString(dto);

        Book book = Book.builder()
                .id(1l)
                .isbn("123")
                .build();

        BDDMockito.given(bookService.getBookByIsbn("123"))
                .willReturn(Optional.of(book));

        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willThrow(new BusinessException("Livro ja emprestado"));


        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(LOAN_URL)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Livro ja emprestado"));
    }

    @Test
    @DisplayName("Deve realizar a devoluçao de um livro emprestado")
    public void returnedLoanTest() throws Exception {

        //cenario {return : true}
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();

        Loan loan = Loan.builder().id(1l).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.of(loan));

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(LOAN_URL.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isOk());

        Mockito.verify(loanService, Mockito.times(1)).update(loan);


    }

    @Test
    @DisplayName("Deve retornar erro caso a devoluçao de um livro nao exista")
    public void returnedErrorLoanTest() throws Exception {

        //cenario {return : false}
        ReturnedLoanDTO dto = ReturnedLoanDTO.builder().returned(true).build();
        BDDMockito.given(loanService.getById(Mockito.anyLong())).willReturn(Optional.empty());

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put(LOAN_URL.concat("/1"))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isNotFound());

        Mockito.verify(loanService, Mockito.never()).update(Loan.builder().build());

    }

    @Test
    @DisplayName("Deve realizar uma pesquisa de livros")
    public void findLoanFilter() throws Exception {

        Long id = 1l;

        Loan loan = LoanServiceTest.createLoan();
        loan.setId(id);
        Book book = Book.builder().isbn("001").id(1l).build();
        loan.setBook(book);

        BDDMockito.given(loanService.find(Mockito.any(LoanFilterDTO.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,10), 1));

        String queryParams = String.format("?isbn=%s&costumer=%s&page=0&size=10",
                book.getIsbn(), loan.getCustomer());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(LOAN_URL.concat(queryParams))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(10))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }
}
