package com.worescloud.workdesk.storage.mongodb.filter;

import com.mongodb.client.model.Filters;
import com.worescloud.workdesk.storage.Filter;

import static com.worescloud.workdesk.storage.Filter.Action.NOT;

@ConverterImpl(NOT)
@SuppressWarnings("unchecked")
public class NotFilter implements Converter {

	@Override
	public <T> T convert(Filter filter) {
		return (T) Filters.not(MongoDBFilterConverter.convert((Filter) filter.getParameter()));
	}
}
