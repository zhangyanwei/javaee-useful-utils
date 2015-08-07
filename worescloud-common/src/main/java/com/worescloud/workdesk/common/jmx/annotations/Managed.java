package com.worescloud.workdesk.common.jmx.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({ TYPE, FIELD, METHOD })
@Documented
public @interface Managed {

    boolean enabled() default true;

    String type();

    String group();

    String name();

    String description();

    Attribute[] attributes() default {};

    Operation[] operations() default {};

}
