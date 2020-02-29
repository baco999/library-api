package com.apirestlibrary.libraryapi.model.repository;

import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.repository.BookRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro pelo Id")
    public void findByIdTest(){
        //cenario
        Book book = Book.builder().isbn("123").build();
        testEntityManager.persist(book);

        //execução
        Optional<Book> findById = repository.findById(book.getId());

        //Verificação
        assertThat(findById.isPresent()).isTrue();

    }

    @Test
    @DisplayName("Deve salvar um livro na base")
    public void saveBook(){

        Book book = getNewBook();

        Book saveBook = repository.save(book);

        assertThat(saveBook.getId()).isNotNull();


    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deletedBook(){

        //cenario
        Book book = getNewBook();
        testEntityManager.persist(book);

        Book foundedBook = testEntityManager.find(Book.class, book.getId());

        repository.delete(foundedBook);

        Book deletedBook = testEntityManager.find(Book.class, book.getId());

        assertThat(deletedBook).isNull();


    }

    private Book getNewBook() {
        return Book.builder().author("teste").title("teste").isbn("123").build();
    }
}

