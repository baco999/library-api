package com.apirestlibrary.libraryapi.api.exception;

public class BusinessException extends RuntimeException {

    public BusinessException(String erro){
        super(erro);
    }
}
