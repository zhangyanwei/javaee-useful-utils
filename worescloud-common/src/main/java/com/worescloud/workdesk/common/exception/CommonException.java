package com.worescloud.workdesk.common.exception;

import com.worescloud.workdesk.common.exception.code.ExceptionCategory;
import com.worescloud.workdesk.common.exception.code.ExceptionCode;

import static com.worescloud.workdesk.common.exception.code.ExceptionCategory.ILLEGAL;
import static com.worescloud.workdesk.common.exception.code.ExceptionCategory.INNER;
import static com.worescloud.workdesk.common.exception.code.ExceptionModule.COMMON;

public class CommonException extends AbstractException {

	private static final long serialVersionUID = -3595143143148007167L;

    public enum Error {
        UNKNOWN(INNER, 0, "unknown error"),
        NOT_ANNOTATED_WITH_EXPECTED_ANNOTATION(ILLEGAL, 0, "class not annotated with expected annotation"),
        PROPERTY_METHOD_MISSING_PARAMETER(ILLEGAL, 1, "property set method missing parameters");

        private final ExceptionCode code;

        Error(ExceptionCategory category, int code, String description) {
            this.code = COMMON.createCode(category, code, description);
        }
    }

    public CommonException(Error error, Object... parameters) {
        super(error.code, parameters);
    }

    public CommonException(Throwable throwable) {
        super(throwable);
    }

    @Override
    protected ExceptionCode defaultCode() {
        return Error.UNKNOWN.code;
    }
}
