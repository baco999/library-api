package com.apirestlibrary.libraryapi.model.repository;

import com.apirestlibrary.libraryapi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository <Book, Long> {
    boolean existsByIsbn(String isbn);
}
