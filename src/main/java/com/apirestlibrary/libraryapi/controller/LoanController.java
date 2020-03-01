package com.apirestlibrary.libraryapi.controller;

import com.apirestlibrary.libraryapi.api.dto.BookDTO;
import com.apirestlibrary.libraryapi.api.dto.LoanDTO;
import com.apirestlibrary.libraryapi.api.dto.LoanFilterDTO;
import com.apirestlibrary.libraryapi.api.dto.ReturnedLoanDTO;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.service.BookService;
import com.apirestlibrary.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long createdLoan(@RequestBody LoanDTO loanDTO){

        Book book = bookService.getBookByIsbn(loanDTO.getIsbn())
                .orElseThrow(()->
                        new ResponseStatusException( HttpStatus.BAD_REQUEST, "Livro não encontrado com esse isbn fornecido"));

        Loan entity = Loan.builder()
                .book(book)
                .customer(loanDTO.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = loanService.save(entity);

        return entity.getId();
    }

    @PutMapping("{id}")
    public void returnedLoan(@PathVariable Long id, @RequestBody ReturnedLoanDTO returnedLoanDTO){

        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro nao encontrado"));
        loan.setReturned(returnedLoanDTO.getReturned());

        loanService.update(loan);

    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO returnedLoanDTO, Pageable pageable){

        Page<Loan> response = loanService.find(returnedLoanDTO, pageable);

        List<LoanDTO> listResponse = response.getContent()
                .stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = mapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = mapper.map(entity, LoanDTO.class);
                    loanDTO.setBookDTO(bookDTO);
                    return loanDTO;
                }).collect(Collectors.toList());

        return new PageImpl<LoanDTO>(listResponse,pageable, response.getTotalElements());


    }
}
