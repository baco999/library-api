package com.apirestlibrary.libraryapi.bookControllerTest;


import com.apirestlibrary.libraryapi.api.dto.BookDTO;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @Test
    @DisplayName("Deve criar um livro com sucesso")
    public void createBookTest() throws Exception {

        BookDTO dto = createNewBook();

        Book book = Book.builder()
                .id(1L)
                .author("Gabriel Nogueira")
                .title("Meu livro Teste")
                .isbn("123456")
                .build();


        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(book);

        //Metodo para preencher minha chama com um objeto Json generico ou que eu criei
        String json = new ObjectMapper().writeValueAsString(dto);

        //Metodo para realizar a chamada mockada no endpoint
         MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

         mvc
                 .perform(request)
                 .andExpect(status().isCreated())
                 .andExpect(jsonPath("id").isNotEmpty())
                 .andExpect(jsonPath("title").value(dto.getTitle()))
                 .andExpect(jsonPath("author").value(dto.getAuthor()))
                 .andExpect(jsonPath("isbn").value(dto.getIsbn()));


    }



    @Test
    @DisplayName("Deve lançar erro na criação caso não houver dados suficientes na requisição")
    public void invalidBookTest() throws Exception {


        //Metodo para preencher minha chama com um objeto Json generico ou que eu criei
        String json = new ObjectMapper().writeValueAsString(new BookDTO());

        //Metodo para realizar a chamada mockada no endpoint
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve retornar erro caso o livro cadastrado ja exista com o mesmo isbn")
    public void createBookWithDuplicatedIsbn() throws Exception {

        BookDTO dto = createNewBook();

        //Metodo para preencher minha chama com um objeto Json generico ou que eu criei
        String json = new ObjectMapper().writeValueAsString(dto);
        String error = "Isbn já cadastrada";
        BDDMockito.given(service.save(Mockito.any(Book.class))).willThrow(new BusinessException(error));

        //Metodo para realizar a chamada mockada no endpoint
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(error));
    }

    private BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Gabriel Nogueira")
                .title("Meu livro Teste")
                .isbn("123456")
                .build();
    }

}
