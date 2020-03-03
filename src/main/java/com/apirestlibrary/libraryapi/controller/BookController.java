package com.apirestlibrary.libraryapi.controller;

import com.apirestlibrary.libraryapi.api.dto.BookDTO;
import com.apirestlibrary.libraryapi.api.dto.LoanDTO;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.service.BookService;
import com.apirestlibrary.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
@Slf4j
public class BookController {

    private final BookService bookService;
    private final ModelMapper mapper;
    private final LoanService loanService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Metodo para criação de livros")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Livro Salvo com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel")
    })
    public BookDTO create(@RequestBody @Valid BookDTO bookDTO){

        log.info("Objeto de entrada para criação na base: {}", bookDTO);

        Book entity = mapper.map(bookDTO,Book.class);

        entity = bookService.save(entity);

        log.info("Objeto de saida com sucesso de criação: {}", entity);

        return mapper.map(entity,BookDTO.class);

    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Metodo para busca de livros atraves do id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Livro encontrado com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel"),
            @ApiResponse(code = 404, message = "Não foi encontrado")
    })
    public BookDTO getBook(@PathVariable @Valid Long id){
        log.info("ID de entrada para buscar um livro na base: {}", id);

        return bookService.getById(id)
                .map( book -> mapper.map(book,BookDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Metodo para deletar algum livro atraves do id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Livro deletado com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel"),
            @ApiResponse(code = 404, message = "Não foi encontrado")
    })
    public void deleteBook(@PathVariable @Valid Long id){
        log.info("ID de entrada para deletar um livro na base: {}", id);
        Book book = bookService.getById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        bookService.delete(book);

    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation("Metodo para atualizar as info de algum livro atraves do id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Livro atualizado com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel"),
            @ApiResponse(code = 404, message = "Não foi encontrado")
    })
    public BookDTO updateBook(@PathVariable @Valid Long id ,BookDTO bookDTO){
        log.info("Objetos de entrada para atualização do livro: {}, {}",id,bookDTO);
        return bookService.getById(id).map( book -> {
                book.setAuthor(bookDTO.getAuthor() );
                book.setTitle(bookDTO.getTitle() );
                book = bookService.update(book);
                return mapper.map(book,BookDTO.class);
                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @GetMapping
    @ApiOperation("Metodo para buscar livros atraves dos parametros")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Lista de livros atraves de parametros encontrados com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel"),
            @ApiResponse(code = 404, message = "Não foi encontrado")
    })
    public Page<BookDTO> find(BookDTO bookDTO, Pageable pageable){
        log.info("Campos de entrada para buscar livros com parametros: {}", bookDTO);
        Book filter = mapper.map(bookDTO, Book.class);

        Page<Book> response = bookService.find(filter, pageable);

        List<BookDTO> listResponse = response.getContent()
                .stream()
                .map(entity -> mapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());

        return new PageImpl<BookDTO>(listResponse,pageable, response.getTotalElements());
    }

    @GetMapping("{id}/loans")
    @ApiOperation("Metodo para buscar livros com emprestimos atraves do id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Lista de livros emprestados encontrado com sucesso"),
            @ApiResponse(code = 500, message = "Serviço indisponivel"),
            @ApiResponse(code = 404, message = "Não foi encontrado")
    })
    public Page<LoanDTO> bookFetchByLoans(@PathVariable Long id, Pageable pageable){
        log.info("ID de entrada para buscar livros com emprestimos: {}", id);

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
