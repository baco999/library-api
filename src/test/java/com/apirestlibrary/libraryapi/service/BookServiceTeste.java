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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

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
        verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro pelo ID")
    public void getBookByIdTest(){

        //cenario
        Long id = 1l;
        Book book = getNewBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        //execução
        Optional<Book> findBook = service.getById(id);

        //validação
        assertThat(findBook.isPresent()).isTrue();
        assertThat(findBook.get().getId()).isEqualTo(id);
        assertThat(findBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(findBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(findBook.get().getTitle()).isEqualTo(book.getTitle());

    }

    @Test
    @DisplayName("Deve retornar vazio quando nao existir o livro na base")
    public void getBookByIdNotFoundTest(){

        //cenario
        Long id = 1l;
        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execução
        Optional<Book> findBook = service.getById(id);

        //validação
        assertThat(findBook.isPresent()).isFalse();


    }

    @Test
    @DisplayName("Deve deletar um livro quando encontrado pelo id")
    public void deleteBookFindById(){

        //cenario
        Book book = getNewBook();
        book.setId(1L);
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                        .id(1L)
                        .isbn("123")
                        .title("teste")
                        .author("teste")
                        .build());
        //execução
        assertDoesNotThrow(() -> service.delete(book));

        //verificação
        verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve retornar erro quando tentar deletar um livro com id nulo ou inexistente")
    public void returnErrorWithDeletedBookInIdNull(){

        //cenario
        Book book = getNewBook();

        //execução
        //service.delete(book);
        Throwable e = Assertions.catchThrowable(() -> service.delete(book));


        //verificação
        assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("Id nao pode ser nulo");
        verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro quando encontrado na base")
    public void updatedBookFounded(){

        //cenario
        Book book = getNewBook();
        book.setId(1L);
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                        .id(1L)
                        .isbn("123")
                        .title("teste")
                        .author("teste")
                        .build());

        //execução
        Book updateBook = service.update(book);

        //verificação
        assertThat(updateBook.getId()).isNotNull();
        assertThat(updateBook.getAuthor()).isEqualTo("teste");
        assertThat(updateBook.getIsbn()).isEqualTo("123");
        assertThat(updateBook.getTitle()).isEqualTo("teste");

    }

    @Test
    @DisplayName("Deve retornar erro quando tentar atualizar um livro com id nulo ou inexistente")
    public void updatedErrorBook(){
        //cenario
        Book book = getNewBook();

        //execução
        Throwable e = Assertions.catchThrowable(() -> service.update(book));


        //verificação
        assertThat(e).isInstanceOf(IllegalArgumentException.class).hasMessage("Id nao pode ser nulo");
       verify(repository, Mockito.never()).save(book);

    }

    @Test
    @DisplayName("Deve buscar livros atraves de filtros")
    public void findBookWithFilters(){

        Book book = getNewBook();
        PageRequest pageRequest =  PageRequest.of(0,10);

        List<Book> list = Arrays.asList(book);

        PageImpl<Book> page = new PageImpl<Book>(Arrays.asList(book), pageRequest, 1);

        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);

        Page<Book> response = service.find(book, pageRequest);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).isEqualTo(list);
        assertThat(response.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(response.getPageable().getPageSize()).isEqualTo(10);

    }

    @Test
    @DisplayName("Deve obter um livro pelo Isbn")
    public void getBookByIsbn(){
        String isbn = "123";

        Book book = Book.builder().isbn("123").title("teste").author("teste").id(1l).build();

        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        Optional<Book> book1 = service.getBookByIsbn(isbn);

        assertThat(book1.isPresent()).isTrue();
        assertThat(book1.get().getId()).isEqualTo(1l);
        assertThat(book1.get().getIsbn()).isEqualTo(isbn);

        verify(repository, times(1)).findByIsbn(isbn);
    }

    private Book getNewBook() {
        return Book.builder().author("teste").title("teste").isbn("123").build();
    }
}
