package com.worescloud.workdesk.storage.mongodb.filter;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

import static com.worescloud.workdesk.storage.Filter.Action.IN;

@ConverterImpl(IN)
@SuppressWarnings("unchecked")
public class InFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		Object parameters = filter.getParameter();
		if (parameters instanceof Iterable) {
			return (T) Filters.in(filter.getFieldName(), (Iterable) parameters);
		}

		return (T) Filters.in(filter.getFieldName(), (Object[]) parameters);
	}
}
