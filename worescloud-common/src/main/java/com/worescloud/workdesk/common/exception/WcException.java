package com.worescloud.workdesk.common.exception;

import java.io.Serializable;

import com.worescloud.workdesk.common.exception.code.ExceptionCode;

public interface WcException {

    ExceptionCode getCode();

    Serializable[] getParameters();

    Throwable getThrowable();
}
