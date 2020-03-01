package com.apirestlibrary.libraryapi.controller;


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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
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

    @Test
    @DisplayName("Deve encontrar um livro atravez do id")
    public void getBookDetailsOnTest() throws Exception {

        //cenario
        Long id = 1l;

        Book book = Book.builder()
                .isbn(createNewBook().getIsbn())
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .id(id)
                .build();

        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        //execução
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        //verificaçao

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(book.getTitle()))
                .andExpect(jsonPath("author").value(book.getAuthor()))
                .andExpect(jsonPath("isbn").value(book.getIsbn()));


    }

    @Test
    @DisplayName("Deve retornar erro quando nao encontrar o livro solicitado")
    public void getErrorBookWithNotFounded() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve deletar um livro quando for encontrado atraves do id")
    public void deleteBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1l).build()));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNoContent());

    }

    @Test
    @DisplayName("Deve retonar erro quando nao encontrar o ID do livro para ser deletado")
    public void deleteBookTestNotFound() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/" + 1))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve atualizar as infos de um livro")
    public void updateBookTest() throws Exception {

        Long id = 1l;
        String json = new ObjectMapper().writeValueAsString(createNewBook());


        Book updatingNewBook = Book.builder()
                .id(1l)
                .author("novo autor")
                .title("novo titulo")
                .isbn("novo isbn")
                .build();

        Book updatedBook = Book.builder()
                .id(1l)
                .author("Gabriel Nogueira")
                .title("Meu livro Teste")
                .isbn("123456")
                .build();

        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(updatingNewBook));
        BDDMockito.given(service.update(updatingNewBook)).willReturn(updatedBook);

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);


        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBook().getTitle()))
                .andExpect(jsonPath("author").value(createNewBook().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBook().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar um erro quando tentar atualizar um livro inexistente")
    public void updateBookReturnErrorTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewBook());

        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);


        mvc.perform(request)
                .andExpect(status().isNotFound());

    }

    @Test
    @DisplayName("Deve buscar livros atraves de filtros")
    public void findBookFilter() throws Exception {

        Long id = 1l;

        Book book = Book.builder()
                .isbn(createNewBook().getIsbn())
                .title(createNewBook().getTitle())
                .author(createNewBook().getAuthor())
                .id(id)
                .build();

        BDDMockito.given(service.find(Mockito.any(Book.class), Mockito.any(Pageable.class)))
                .willReturn(new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,199), 1));

        String queryParams = String.format("?title=%s&author=%s&page=0&size=100",
                book.getTitle(), book.getAuthor());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryParams))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));

    }



    private BookDTO createNewBook() {
        return BookDTO.builder()
                .author("Gabriel Nogueira")
                .title("Meu livro Teste")
                .isbn("123456")
                .build();
    }

}
