package com.fressato.stockquotemanager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED)
public class NotAllowedToCreateException extends RuntimeException {

    public NotAllowedToCreateException() {
        super("It is necessary to have quotes to register stock quotes");
    }

}
