package com.worescloud.workdesk.storage.exception;

import com.worescloud.workdesk.common.exception.AbstractException;
import com.worescloud.workdesk.common.exception.code.ExceptionCategory;
import com.worescloud.workdesk.common.exception.code.ExceptionCode;

import static com.worescloud.workdesk.common.exception.code.ExceptionCategory.ILLEGAL;
import static com.worescloud.workdesk.common.exception.code.ExceptionCategory.INNER;
import static com.worescloud.workdesk.common.exception.code.ExceptionModule.MAIL;

public class StorageException extends AbstractException {

	public enum Error {

		UNKNOWN_EXCEPTION(INNER, 0, "unknown error"),
		BEAN_NOT_SET_ANNOTATED(INNER, 1, "this bean not annotated with @Set annotation."),
		BEAN_INVALID_KEY_ANNOTATIONS(INNER, 2, "in a storage bean, should only exists one filed annotated with @Key"),
		ENTITY_NOT_EXISTS(ILLEGAL, 0, "entity not exists");

		private final ExceptionCode code;

		Error(ExceptionCategory category, int code, String description) {
			this.code = MAIL.createCode(category, code, description);
		}

	}

	public StorageException(Error error, Object... parameters) {
		super(error.code, parameters);
	}

	public StorageException(ExceptionCode code, Object[] parameters, Throwable throwable) {
		super(code, parameters, throwable);
	}

	public StorageException(ExceptionCode code, Throwable throwable) {
		super(code, null, throwable);
	}

	public StorageException(Throwable throwable) {
		super(throwable);
	}

	public StorageException(Exception e) {
		super(e);
	}

	@Override
	protected ExceptionCode defaultCode() {
		return Error.UNKNOWN_EXCEPTION.code;
	}
}