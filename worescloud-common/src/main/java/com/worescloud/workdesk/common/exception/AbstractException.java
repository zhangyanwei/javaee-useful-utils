package com.worescloud.workdesk.common.exception;

import java.io.Serializable;

import com.worescloud.workdesk.common.exception.code.ExceptionCode;

public abstract class AbstractException extends Exception implements WcException {

    private static final long serialVersionUID = -3329192751975660034L;

    private ExceptionCode code;
    private Serializable[] parameters;
    private Throwable throwable;

    public AbstractException(ExceptionCode code, Serializable... parameters) {
        super(code.getDescription());
        this.code = code;
        this.parameters = parameters;
    }

    public AbstractException(ExceptionCode code, Serializable[] parameters, Throwable throwable) {
        super(code.getDescription(), throwable);
        this.code = code;
        this.parameters = parameters;
        this.throwable = throwable;
    }

    public AbstractException(Throwable throwable) {
        super(throwable);
        digestException(throwable);
    }

    @Override
    public ExceptionCode getCode() {
        return code;
    }

    @Override
    public Serializable[] getParameters() {
        return parameters;
    }

    @Override
    public Throwable getThrowable() {
        return throwable;
    }

    protected abstract ExceptionCode defaultCode();

    private void digestException(Throwable throwable) {
        this.throwable = throwable;
        if (throwable instanceof WcException) {
            WcException exception = (WcException) throwable;
            this.code = exception.getCode();
            this.parameters = exception.getParameters();
        } else {
            this.code = defaultCode();
        }
    }
}
