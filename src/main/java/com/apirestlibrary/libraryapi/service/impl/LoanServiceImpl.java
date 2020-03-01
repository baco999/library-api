package com.apirestlibrary.libraryapi.service.impl;

import com.apirestlibrary.libraryapi.api.dto.LoanFilterDTO;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.model.repository.LoanRepository;
import com.apirestlibrary.libraryapi.service.LoanService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public class LoanServiceImpl implements LoanService {

    private LoanRepository loanRepository;

    public LoanServiceImpl(LoanRepository repository) {

        this.loanRepository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if (loanRepository.existsBookAndNotReturned(loan.getBook())){
            throw new BusinessException("Livro ja emprestado");
        }
        return this.loanRepository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return this.loanRepository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return this.loanRepository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO loanFilterDTO, Pageable pageable) {
        return this.loanRepository.findByBookIsbnOrCustomer(
                loanFilterDTO.getIsbn(),
                loanFilterDTO.getCustomer(),
                pageable);
    }
}
