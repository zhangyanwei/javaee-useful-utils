package com.worescloud.workdesk.storage.mongodb;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.worescloud.workdesk.storage.Filter;
import com.worescloud.workdesk.storage.Sort;
import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageFilter;
import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery;
import com.worescloud.workdesk.storage.exception.StorageException;
import com.worescloud.workdesk.storage.mongodb.sort.MongoDBSortConverter;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.worescloud.workdesk.storage.mongodb.filter.MongoDBFilterConverter.convert;
import static com.worescloud.workdesk.storage.util.Reflections.isKeyAttribute;
import static com.worescloud.workdesk.storage.util.Reflections.storableAttributes;

public class MongoDBQuery<E> implements StorageQuery<E> {

	private static final String ID = "_id";

	private Class<E> type;
	private MongoCollection<Document> collection;
	private Gson gson;

	private String[] fieldNames;
	private List<Bson> filters;
	private List<Sort> sorts;
	private int skip;
	private int limit;

	public MongoDBQuery(Class<E> type, MongoCollection<Document> collection, Gson gson) {
		this.type = type;
		this.collection = collection;
		this.gson = gson;
		this.fieldNames = new String[] {ID};
		this.filters = new ArrayList<>();
		this.sorts = new ArrayList<>();
	}

	@Override
	public StorageFilter<E> full() {
		this.fieldNames = storableAttributes(type);
		return this;
	}

	@Override
	public StorageFilter<E> select(String... attributes) {
		this.fieldNames = attributes;
		return this;
	}

	@Override
	public StorageFilter<E> filter(Filter filter) {
		filters.add(convert(
				isKeyAttribute(type, filter.getFieldName()) ?
						new Filter(filter.getAction(), ID, filter.getParameter()) :
						filter
		));
		return this;
	}

	@Override
	public StorageFilter<E> sort(Sort sort) {
		this.sorts.add(sort);
		return this;
	}

	@Override
	public StorageFilter<E> skip(int skip) {
		this.skip = skip;
		return this;
	}

	@Override
	public StorageFilter<E> limit(int limit) {
		this.limit = limit;
		return this;
	}

	@Override
	public E one() throws StorageException {
		return iterable().first();
	}

	@Override
	public List<E> collect() throws StorageException {
		return iterable().into(new ArrayList<>());
	}

	@Override
	public E index(int index) throws StorageException {
		return collection.find(Filters.and(filters))
				.projection(Projections.include(fieldNames))
				.sort(MongoDBSortConverter.convert(sorts))
				.skip(index)
				.limit(1)
				.map(document -> gson.fromJson(document.toJson(), type))
				.first();
	}

	@Override
	public long count() throws StorageException {
		return collection.count(Filters.and(filters), new CountOptions().skip(skip).limit(limit));
	}

	@Override
	public boolean exist() throws StorageException {
		return count() > 0;
	}

	private MongoIterable<E> iterable() {
		return collection.find(Filters.and(filters))
				.projection(Projections.include(fieldNames))
				.sort(MongoDBSortConverter.convert(sorts))
				.skip(skip)
				.limit(limit)
				.map(document -> gson.fromJson(document.toJson(), type));
	}
}
