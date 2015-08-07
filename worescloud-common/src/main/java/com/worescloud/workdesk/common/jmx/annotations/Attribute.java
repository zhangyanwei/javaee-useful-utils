package com.worescloud.workdesk.common.jmx.annotations;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Attribute {

    boolean readonly() default false;

    String attribute();

    String description();

}
