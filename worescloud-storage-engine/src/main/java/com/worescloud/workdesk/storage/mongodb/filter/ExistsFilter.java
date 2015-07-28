package com.worescloud.workdesk.storage.mongodb.filter;

import static com.worescloud.workdesk.storage.Filter.Action.EXISTS;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

@ConverterImpl(EXISTS)
@SuppressWarnings("unchecked")
public class ExistsFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		Boolean exists = (Boolean) filter.getParameter();
		return (T) Filters.exists(filter.getFieldName(), exists != null && exists);
	}
}
