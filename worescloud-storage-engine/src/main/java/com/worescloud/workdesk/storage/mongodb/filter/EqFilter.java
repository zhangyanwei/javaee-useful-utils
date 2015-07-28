package com.worescloud.workdesk.storage.mongodb.filter;

import static com.worescloud.workdesk.storage.Filter.Action.EQ;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

@ConverterImpl(EQ)
@SuppressWarnings("unchecked")
public class EqFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		return (T) Filters.eq(filter.getFieldName(), filter.getParameter());
	}
}
