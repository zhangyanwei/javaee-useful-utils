package com.worescloud.workdesk.storage.mongodb.filter;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

import static com.worescloud.workdesk.storage.Filter.Action.ALL;

@ConverterImpl(ALL)
@SuppressWarnings("unchecked")
public class AllFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		Object[] parameters = (Object[]) filter.getParameter();
		return (T) Filters.all(filter.getFieldName(), parameters);
	}
}
