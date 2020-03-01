package com.apirestlibrary.libraryapi.service;

import com.apirestlibrary.libraryapi.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookService {
    Book save(Book book);

    Optional<Book> getById(Long id);

    void delete(Book book);

    Book update(Book book);

    Page<Book> find(Book filter, Pageable pageableRequest);

    Optional<Book> getBookByIsbn(String isbn);
}
