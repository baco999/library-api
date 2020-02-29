package com.apirestlibrary.libraryapi.controller;

import com.apirestlibrary.libraryapi.api.dto.BookDTO;
import com.apirestlibrary.libraryapi.api.resource.ApiErrors;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.service.BookService;
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

@RestController
@RequestMapping("/api/books")
public class BooksController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ModelMapper mapper;

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
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException e){

        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(BusinessException e){

        return new ApiErrors(e);
    }

}
