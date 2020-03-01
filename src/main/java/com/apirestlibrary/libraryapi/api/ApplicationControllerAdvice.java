package com.apirestlibrary.libraryapi.api;

import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.api.resource.ApiErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApplicationControllerAdvice {

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

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus
    public ResponseEntity handleResponseStatusException(ResponseStatusException e){
        return new ResponseEntity(new ApiErrors(e), e.getStatus());

    }
}
