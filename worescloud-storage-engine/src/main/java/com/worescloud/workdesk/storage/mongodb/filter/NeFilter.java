package com.worescloud.workdesk.storage.mongodb.filter;

import static com.worescloud.workdesk.storage.Filter.Action.NE;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

@ConverterImpl(NE)
@SuppressWarnings("unchecked")
public class NeFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		return (T) Filters.ne(filter.getFieldName(), filter.getParameter());
	}
}
