package com.apirestlibrary.libraryapi.model.repository;

import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class BookRepositoryTest {

    @Autowired
    TestEntityManager testEntityManager;

    @Autowired
    BookRepository repository;

    @Test
    @DisplayName("Deve retornar verdadeiro quando retornar um isbn ja registrado")
    public void returnTrueWhenIsbnExist(){

        //cenario
        Book book = getNewBook();
        testEntityManager.persist(book);

        //execução
        boolean exist = repository.existsByIsbn(book.getIsbn());

        //verificação
        assertThat(exist).isTrue();


    }

    @Test
    @DisplayName("Deve retornar false quando retornar um isbn não esta registrado")
    public void returnFalseWhenIsbnExist(){

        //cenario
        String isbn = "000";
        Book book = Book.builder()
                .author("123")
                .title("123")
                .isbn("123")
                .build();

        testEntityManager.persist(book);

        //execução
        boolean exist = repository.existsByIsbn(isbn);

        //verificação
        //validação
        assertThat(exist).isFalse();


    }

    private Book getNewBook() {
        return Book.builder().author("teste").title("teste").isbn("123").build();
    }
}

