package com.worescloud.workdesk.common.jmx.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Parameter {

    Class<?> type();

    String name();

    String description();

}
