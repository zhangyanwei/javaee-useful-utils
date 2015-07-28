package com.worescloud.workdesk.storage.mongodb;

import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.DeleteResult;
import com.worescloud.workdesk.storage.Filter;
import com.worescloud.workdesk.storage.StorageStreamBuilder.StorageFilter;
import com.worescloud.workdesk.storage.StorageStreamBuilder.StorageStream;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.worescloud.workdesk.storage.mongodb.filter.MongoDBFilterConverter.convert;

public class MongoDBStream<E> implements StorageStream<E> {

	private final Class<E> type;
	private final MongoCollection<Document> collection;
	private final Gson gson;

	private List<Bson> filters;

	public MongoDBStream(Class<E> type, MongoCollection<Document> collection, Gson gson) {
		this.type = type;
		this.collection = collection;
		this.gson = gson;
		this.filters = new ArrayList<>();
	}

	@Override
	public StorageFilter<E> filter(Filter filter) {
		this.filters.add(convert(filter));
		return this;
	}

	@Override
	public boolean delete() {
		DeleteResult deleteResult = this.collection.deleteMany(and(filters));
		return deleteResult.getDeletedCount() > 0;
	}
}
