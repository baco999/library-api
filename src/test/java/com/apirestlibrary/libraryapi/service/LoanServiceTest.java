package com.apirestlibrary.libraryapi.service;

import com.apirestlibrary.libraryapi.api.dto.LoanFilterDTO;
import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import com.apirestlibrary.libraryapi.model.repository.BookRepository;
import com.apirestlibrary.libraryapi.model.repository.LoanRepository;
import com.apirestlibrary.libraryapi.service.impl.BookServiceImpl;
import com.apirestlibrary.libraryapi.service.impl.LoanServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LoanServiceTest {

    LoanService service;

    @MockBean
    LoanRepository repository;

    @BeforeEach
    public void setUp(){
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um empréstimo")
    public void saveLoanTest(){

        Book book = Book.builder()
                .isbn("001")
                .id(1l)
                .author("costinha")
                .title("teste")
                .build();

        Loan savingLoan = Loan.builder()
                .id(1l)
                .customer("silva")
                .loanDate(LocalDate.now())
                .book(book)
                .build();

        Loan savedLoan = Loan.builder()
                .id(1l)
                .loanDate(LocalDate.now())
                .customer("silva")
                .book(book)
                .build();

        when(repository.save(savingLoan)).thenReturn(savedLoan);

        Loan loan = service.save(savingLoan);

        assertThat(loan.getId()).isEqualTo(savedLoan.getId());
        assertThat(loan.getBook().getId()).isEqualTo(savedLoan.getBook().getId());
        assertThat(loan.getCustomer()).isEqualTo(savedLoan.getCustomer());
        assertThat(loan.getLoanDate()).isEqualTo(savedLoan.getLoanDate());

    }

    @Test
    @DisplayName("Deve lançar erro caso tente realizar um emprestimo de um livro ja emprestado")
    public void saveErrorLoanTest(){

        Book book = Book.builder()
                .isbn("001")
                .id(1l)
                .author("costinha")
                .title("teste")
                .build();

        Loan savingLoan = Loan.builder()
                .id(1l)
                .customer("silva")
                .loanDate(LocalDate.now())
                .book(book)
                .build();

        when(repository.existsBookAndNotReturned(book)).thenReturn(true);

        Throwable e = catchThrowable(() -> service.save(savingLoan));

        assertThat(e).isInstanceOf(BusinessException.class).hasMessage("Livro ja emprestado");

        verify(repository, Mockito.never()).save(savingLoan);

    }

    @Test
    @DisplayName("Deve obter as informaçoes de um emprestimo pelo id")
    public void getInfoLoanById(){

        Long id = 1l;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        //exe
        Optional<Loan> loanById = service.getById(id);

        //verificação
        assertThat(loanById.isPresent()).isTrue();
        assertThat(loanById.get().getId()).isEqualTo(id);
        assertThat(loanById.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(loanById.get().getBook()).isEqualTo(loan.getBook());
        assertThat(loanById.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        verify(repository, times(1)).findById(id);


    }

    @Test
    @DisplayName("Deve atualizar as info de um emprestimo")
    public void updateLoanTest(){

        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);

        when(repository.save(loan)).thenReturn(loan);

        Loan update = service.update(loan);

        assertThat(update.getReturned()).isTrue();
        verify(repository, times(1)).save(loan);
    }

    @Test
    @DisplayName("Deve buscar emprestimos atraves de filtros")
    public void findFiltersWithFilters(){

        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().isbn("001").customer("silva").build();
        Loan loan = createLoan();
        PageRequest pageRequest =  PageRequest.of(0,10);

        List<Loan> list = Arrays.asList(loan);

        PageImpl<Loan> page = new PageImpl<Loan>(Arrays.asList(loan), pageRequest, 1);

        Mockito.when(repository.findByBookIsbnOrCustomer(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.any(Pageable.class)))
                .thenReturn(page);

        Page<Loan> response = service.find(loanFilterDTO, pageRequest);

        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent()).isEqualTo(list);
        assertThat(response.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(response.getPageable().getPageSize()).isEqualTo(10);

    }

    public static Loan createLoan(){
        Book book = Book.builder()
                .isbn("001")
                .id(1l)
                .author("costinha")
                .title("teste")
                .build();

        return Loan.builder()
                .id(1l)
                .customer("silva")
                .loanDate(LocalDate.now())
                .book(book)
                .build();
    }
}
