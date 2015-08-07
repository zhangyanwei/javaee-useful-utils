package com.worescloud.workdesk.common.jmx.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Operation {

    String operation();

    String description();

    Parameter[] parameters() default {};

}
