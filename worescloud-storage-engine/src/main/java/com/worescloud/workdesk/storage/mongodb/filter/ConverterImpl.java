package com.worescloud.workdesk.storage.mongodb.filter;

import com.worescloud.workdesk.storage.Filter.Action;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface ConverterImpl {

	Action value();

}
