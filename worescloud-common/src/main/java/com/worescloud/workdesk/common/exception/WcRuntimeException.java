package com.worescloud.workdesk.common.exception;

import com.worescloud.workdesk.common.exception.code.ExceptionCode;

import java.io.Serializable;

import static com.worescloud.workdesk.common.exception.code.ExceptionCategory.INNER;
import static com.worescloud.workdesk.common.exception.code.ExceptionModule.COMMON;

public class WcRuntimeException extends RuntimeException implements WcException {

	private static final long serialVersionUID = -5827572639548084945L;

	public static ExceptionCode UNKNOWN = COMMON.createCode(INNER, 0);

	private ExceptionCode code;
	private Serializable[] parameters;
	private Throwable throwable;

	public WcRuntimeException(ExceptionCode code, Serializable... parameters) {
		this.code = code;
		this.parameters = parameters;
	}

	public WcRuntimeException(String message) {
		digestException(null);
	}

	public WcRuntimeException(Throwable throwable) {
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

	protected ExceptionCode defaultCode() {
		return UNKNOWN;
	}

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
