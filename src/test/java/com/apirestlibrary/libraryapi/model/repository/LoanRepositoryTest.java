package com.apirestlibrary.libraryapi.model.repository;

import com.apirestlibrary.libraryapi.model.entity.Book;
import com.apirestlibrary.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static com.apirestlibrary.libraryapi.model.repository.BookRepositoryTest.getNewBook;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("deve verificar se existe emprestimo nao devolvido para o livro solicitado")
    public void existsBookAndNotReturnedTest(){

        Loan loan = createdAndPersistLoan(LocalDate.now());
        Book book = loan.getBook();

        //execução
        boolean exist = loanRepository.existsBookAndNotReturned(book);

        //validação
        assertThat(exist).isTrue();

    }

    @Test
    @DisplayName("Deve buscar um emprestimo pelo isbn ou pelo customer do livro")
    public void findByBookIsbnOrCustomer(){

        createdAndPersistLoan(LocalDate.now());

        Page<Loan> response = loanRepository.findByBookIsbnOrCustomer("123", "silva", PageRequest.of(0, 10));

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getPageable().getPageSize()).isEqualTo(10);
        assertThat(response.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(response.getTotalElements()).isEqualTo(1);

    }

    @Test
    @DisplayName("Deve obter emprestimos cuja a data emprestimo for menor ou igual a 3 dias atras e nao retornados")
    public void findByLoansDateLessThanAndNotReturned(){

        Loan loan = createdAndPersistLoan(LocalDate.now().minusDays(5));

        List<Loan> response = loanRepository.findByLoansDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(response).hasSize(1).contains(loan);


    }

    @Test
    @DisplayName("Deve retornar Vazio quando não houver empréstimos atrasados")
    public void notFindByLoansDateLessThanAndNotReturned(){

        Loan loan = createdAndPersistLoan(LocalDate.now());

        List<Loan> response = loanRepository.findByLoansDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(response).isEmpty();


    }

    public Loan createdAndPersistLoan(LocalDate loanDate){

        //cenario
        Book book = getNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
                .book(book)
                .customer("silva")
                .loanDate(loanDate)
                .build();
        entityManager.persist(loan);
        return loan;

    }
}
