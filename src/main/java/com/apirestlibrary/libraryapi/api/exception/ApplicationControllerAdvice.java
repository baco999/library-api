package com.apirestlibrary.libraryapi.api.exception;

import com.apirestlibrary.libraryapi.api.exception.BusinessException;
import com.apirestlibrary.libraryapi.api.exception.ApiErrors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
@Slf4j
public class ApplicationControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(MethodArgumentNotValidException e){

        log.error("Informações sugeridas invalidas", e);
        BindingResult bindingResult = e.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationException(BusinessException e){
        log.error("Informações sugeridas invalidas", e);

        return new ApiErrors(e);
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus
    public ResponseEntity handleResponseStatusException(ResponseStatusException e){
        log.error("Status da resposta retornando erro", e);

        return new ResponseEntity(new ApiErrors(e), e.getStatus());

    }
}
