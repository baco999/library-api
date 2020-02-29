package com.apirestlibrary.libraryapi.service;

import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.repository.BookRepository;
import com.apirestlibrary.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTeste {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void saveBookTest(){

        //cenario
        Book book = getNewBook();
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                .id(1L)
                .isbn("123")
                .title("teste")
                .author("teste")
                .build());

        //execução
        Book save = service.save(book);

        //validaçao
        assertThat(save.getId()).isNotNull();
        assertThat(save.getAuthor()).isEqualTo("teste");
        assertThat(save.getIsbn()).isEqualTo("123");
        assertThat(save.getTitle()).isEqualTo("teste");

    }

    @Test
    @DisplayName("Deve lançar erro ao cadastrar livro com isbn duplicada")
    public void shouldNotWithBookIsnbDuplicated(){

        //cenario
        Book book = getNewBook();

        Mockito.when(repository.existsByIsbn(Mockito.any())).thenReturn(true);

        //execução
        Throwable e = Assertions.catchThrowable(() -> service.save(book));

        //verificação
        assertThat(e).isInstanceOf(BusinessException.class).hasMessage("Isbn ja cadastrada");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    private Book getNewBook() {
        return Book.builder().author("teste").title("teste").isbn("123").build();
    }
}
