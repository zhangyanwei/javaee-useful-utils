package com.worescloud.workdesk.common.exception;

import com.worescloud.workdesk.common.exception.code.ExceptionCode;

import java.io.Serializable;

public interface WcException {

    ExceptionCode getCode();

    Serializable[] getParameters();

    Throwable getThrowable();
}
