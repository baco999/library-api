package com.apirestlibrary.libraryapi.controller;

import com.apirestlibrary.libraryapi.api.dto.BookDTO;
import com.apirestlibrary.libraryapi.api.dto.LoanDTO;
import com.apirestlibrary.libraryapi.api.resource.ApiErrors;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.service.BookService;
import com.apirestlibrary.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private BookService bookService;
    private ModelMapper mapper;
    private LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){

        Book entity = mapper.map(bookDTO,Book.class);

        entity = bookService.save(entity);

        return mapper.map(entity,BookDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO getBook(@PathVariable @Valid Long id){

        return bookService.getById(id)
                .map( book -> mapper.map(book,BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable @Valid Long id){
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        bookService.delete(book);

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBook(@PathVariable @Valid Long id ,BookDTO bookDTO){
        return bookService.getById(id).map( book -> {
                book.setAuthor(bookDTO.getAuthor() );
                book.setTitle(bookDTO.getTitle() );
                book = bookService.update(book);
                return mapper.map(book,BookDTO.class);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable){
            Book filter = mapper.map(bookDTO, Book.class);

        Page<Book> response = bookService.find(filter, pageable);

        List<BookDTO> listResponse = response.getContent()
                .stream()
                .map(entity -> mapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(listResponse,pageable, response.getTotalElements());
    }

    @GetMapping("{id}/loans")
    public Page<LoanDTO> bookFetchByLoans(@PathVariable Long id, Pageable pageable){

        Book book = bookService.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id nao encontrado na base de dados"));

        Page<Loan> response = loanService.getLoansByBook(book, pageable);

        List<LoanDTO> loanDTOList = response.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookDTO bookDTO = mapper.map(loanBook, BookDTO.class);
                    LoanDTO loanDTO = mapper.map(loan, LoanDTO.class);
                    loanDTO.setBookDTO(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());
        return new PageImpl<LoanDTO>(loanDTOList, pageable, response.getTotalElements());
    }



}
