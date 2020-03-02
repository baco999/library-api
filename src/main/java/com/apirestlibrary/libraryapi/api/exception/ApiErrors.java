package com.apirestlibrary.libraryapi.api.exception;

import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class ApiErrors {


    private List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();

        bindingResult.getAllErrors()
                .forEach(errors -> this.errors.add(errors.getDefaultMessage()));
    }

    public List<String> getErrors() {
        return errors;
    }

    public ApiErrors(BusinessException e) {
        this.errors = asList(e.getMessage());
    }

    public ApiErrors(ResponseStatusException e) {
        this.errors = asList(e.getReason());
    }

}