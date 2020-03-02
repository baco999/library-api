package com.apirestlibrary.libraryapi.api.dto;

import com.apirestlibrary.libraryapi.model.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO {

    private Long id;

    private String isbn;

    private String customer;

    private BookDTO bookDTO;

    private String emailCustomer;
}
