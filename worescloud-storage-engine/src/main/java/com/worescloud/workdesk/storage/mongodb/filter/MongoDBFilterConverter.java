package com.worescloud.workdesk.storage.mongodb.filter;

import com.google.common.collect.ImmutableMap;
import com.worescloud.workdesk.storage.Filter;
import com.worescloud.workdesk.storage.Filter.Action;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;

public final class MongoDBFilterConverter {

	private static final Map<Action, Converter> FILTER_CONVERTER_MAP;

	static {
		List<Class<? extends Converter>> filterTypes = of(
				InFilter.class,
				AllFilter.class,
				EqFilter.class,
				NeFilter.class,
				NotFilter.class,
				ExistsFilter.class
		);

		ImmutableMap.Builder<Action, Converter> mapBuilder = ImmutableMap.builder();
		filterTypes.stream()
				.filter(filterType -> filterType.isAnnotationPresent(ConverterImpl.class))
				.forEach(filterType -> {
					ConverterImpl converterImpl = filterType.getAnnotation(ConverterImpl.class);
					Action key = converterImpl.value();
					try {
						mapBuilder.put(key, filterType.newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
				});

		FILTER_CONVERTER_MAP = mapBuilder.build();
	}

	public static Bson convert(Filter filter) {
		return FILTER_CONVERTER_MAP.get(filter.getAction()).convert(filter);
	}

}
