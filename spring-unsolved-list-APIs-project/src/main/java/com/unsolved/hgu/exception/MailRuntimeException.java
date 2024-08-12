package com.unsolved.hgu.exception;

import java.io.Serial;

public class MailRuntimeException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;

    public MailRuntimeException(String message) {
        super(message);
    }
}
