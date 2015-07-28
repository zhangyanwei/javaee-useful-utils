package com.worescloud.workdesk.storage.mongodb;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.worescloud.workdesk.common.cdi.annotations.Ignore;
import com.worescloud.workdesk.common.exception.WcRuntimeException;
import com.worescloud.workdesk.storage.Engine;
import com.worescloud.workdesk.storage.Filter;
import com.worescloud.workdesk.storage.Storage;
import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery;
import com.worescloud.workdesk.storage.StorageStreamBuilder;
import com.worescloud.workdesk.storage.annotation.FieldAlias;
import com.worescloud.workdesk.storage.annotation.Key;
import com.worescloud.workdesk.storage.annotation.Transient;
import com.worescloud.workdesk.storage.exception.StorageException;
import com.worescloud.workdesk.storage.mongodb.adaptor.DateTimeTypeConverter;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.joda.time.DateTime;

import java.util.*;
import java.util.function.Function;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.mongodb.client.model.ReturnDocument.AFTER;
import static com.mongodb.util.JSON.parse;
import static com.worescloud.workdesk.storage.mongodb.filter.MongoDBFilterConverter.convert;
import static com.worescloud.workdesk.storage.util.Reflections.*;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Ignore
public class MongoDBStorage<E> implements Storage<E> {

	private static final String FIELD_ID = "_id";

	private Class<E> type;

	private MongoDatabase database;

	private Gson gson;
	private MongoCollection<Document> collection;

	@Override
	public void initialize(Engine engine, Class<E> type) throws StorageException {
		this.type = type;

		MongoDBEngine mongoDBEngine = (MongoDBEngine) engine;
		this.database = mongoDBEngine.getDatabase();
		this.collection = database.getCollection(beanSetName(type));
		this.gson = new GsonBuilder()
				.setFieldNamingStrategy(field -> {
					if (field.isAnnotationPresent(Key.class)) {
						return FIELD_ID;
					}

					FieldAlias fieldAlias = field.getAnnotation(FieldAlias.class);
					return fieldAlias != null ? fieldAlias.value() : field.getName();
				})
				.addSerializationExclusionStrategy(new ExclusionStrategy() {
					@Override
					public boolean shouldSkipField(FieldAttributes fieldAttributes) {
						return fieldAttributes.getAnnotation(Transient.class) != null;
					}

					@Override
					public boolean shouldSkipClass(Class<?> aClass) {
						return false;
					}
				})
				.registerTypeAdapter(DateTime.class, new DateTimeTypeConverter())
				.create();
	}

	@Override
	public void save(E entity) throws StorageException {
		collection.insertOne(Document.parse(gson.toJson(entity)));
	}

	@Override
	public void saveOrUpdate(E entity) throws StorageException {
		update(entity, BeanInfo::getAttributes, true);
	}

	@Override
	public void saveOrUpdate(E entity, Set<String> attributes) throws StorageException {
		update(entity, beanInfo -> availableAttributes(attributes, beanInfo), true);
	}

	@Override
	public void update(E entity, Set<String> attributes) throws StorageException {
		update(entity, beanInfo -> availableAttributes(attributes, beanInfo), false);
	}

	@Override
	public void batchSaveOrUpdate(List<E> entities) throws StorageException {
		batchUpdate(entities, BeanInfo::getAttributes);
	}

	@Override
	public void batchSaveOrUpdate(List<E> entities, Set<String> attributes) throws StorageException {
		batchUpdate(entities, beanInfo -> availableAttributes(attributes, beanInfo));
	}

	@Override
	public boolean delete(Object key) {
		DeleteResult deleteResult = collection.deleteOne(eqKey(key));
		return deleteResult.getDeletedCount() > 0;
	}

	@Override
	public boolean delete(Object... keys) {
		DeleteResult deleteResult = collection.deleteMany(Filters.in(FIELD_ID, keys));
		return deleteResult.getDeletedCount() > 0;
	}

	@Override
	public boolean delete(Filter filter) {
		DeleteResult deleteResult = collection.deleteMany(convert(filter));
		return deleteResult.getDeletedCount() > 0;
	}

	@Override
	public boolean exists(Object key) {
		return collection.count(eqKey(key)) > 0;
	}

	@Override
	public Optional<E> find(Object key) throws StorageException {
		Document document = collection.find(eqKey(key)).first();
		if (document != null) {
			return Optional.of(gson.fromJson(document.toJson(), type));
		}

		return Optional.empty();
	}

	@Override
	public Optional<E> find(Object key, String... retrieveAttributes) throws StorageException {
		Document document = collection.find(eqKey(key))
				.projection(Projections.include(retrieveAttributes))
				.first();
		if (document != null) {
			return Optional.of(gson.fromJson(document.toJson(), type));
		}

		return Optional.empty();
	}

	@Override
	public E findOrSave(Object key, E entity) throws StorageException {
		Document document = collection.findOneAndUpdate(
				eqKey(key),
				new BasicDBObject("$setOnInsert", Document.parse(gson.toJson(entity))),
				new FindOneAndUpdateOptions().returnDocument(AFTER).upsert(true)
		);

		return gson.fromJson(document.toJson(), type);
	}

	@Override
	public StorageQuery<E> query() {
		return new MongoDBQuery<>(type, collection, gson);
	}

	@Override
	public StorageStreamBuilder.StorageStream<E> stream() {
		return new MongoDBStream<>(type, collection, gson);
	}

	private void update(E entity, Function<BeanInfo, Map<String, Object>> attributes, boolean upsert) throws StorageException {
		BeanInfo beanInfo = analyse(entity);
		Object key = beanInfo.getKey();
		Map<String, Object> attrs = attributes.apply(beanInfo);

		updateFields(key, attrs, upsert);
	}

	private void batchUpdate(List<E> entities, Function<BeanInfo, Map<String, Object>> attributes) {

		Function<E,  UpdateOneModel<Document>> toModel = entity -> {
			try {
				BeanInfo beanInfo = analyse(entity);
				return toUpdateOneModel(beanInfo.getKey(), attributes.apply(beanInfo), true);
			} catch (StorageException e) {
				throw new WcRuntimeException(e);
			}
		};

		collection.bulkWrite(
				entities.stream().map(toModel).collect(toList()),
				new BulkWriteOptions().ordered(false)
		);
	}

	private void updateFields(Object key, Map<String, Object> attributes, boolean upsert) {
		if (!attributes.isEmpty()) {
			collection.updateOne(eqKey(key), createUpdateDBObject(attributes), new UpdateOptions().upsert(upsert));
		}
	}

	private UpdateOneModel<Document> toUpdateOneModel(Object key, Map<String, Object> attributes, boolean upsert) throws StorageException {
		return new UpdateOneModel<>(eqKey(key), createUpdateDBObject(attributes), new UpdateOptions().upsert(upsert));
	}

	private Bson eqKey(Object key) {
		return Filters.eq(FIELD_ID, key);
	}

	private Map<String, Object> availableAttributes(Set<String> attributes, BeanInfo beanInfo) {
		Map<String, Object> filtered = new HashMap<>();
		beanInfo.getAttributes()
				.entrySet()
				.stream()
				.filter(entry -> attributes.contains(entry.getKey()))
				.forEach(entry -> filtered.put(entry.getKey(), entry.getValue()));

		return filtered;
	}

	private BasicDBObject createUpdateDBObject(Map<String, Object> attributes) {

		BasicDBObject basicDBObject = new BasicDBObject();
		Map<String, Object> addingAttributes = transformAttributes(addingAttribute(attributes));
		if (!addingAttributes.isEmpty()) {
			basicDBObject.append("$set", new BasicDBObject(addingAttributes));
		}

		Map<String, Object> removingAttributes = transformAttributes(removingAttribute(attributes));
		if (!removingAttributes.isEmpty()) {
			basicDBObject.append("$unset", new BasicDBObject(removingAttributes));
		}

		return basicDBObject;
	}

	private Map<String, Object> transformAttributes(Map<String, Object> attributes) {
		List<String> storables = Arrays.asList(storableAttributes(type));
		Map<String, Object> updateMap = new HashMap<>();
		attributes.keySet()
				.stream()
				.filter(storables::contains)
				.forEach(value -> updateMap.put(value, parse(gson.toJson(attributes.get(value)))));
		return updateMap;
	}

	private Map<String, Object> addingAttribute(Map<String, Object> attributes) {
		Map<String, Object> filtered = new HashMap<>();
		attributes.keySet()
				.stream()
				.filter(attr -> !isRemovable(attributes.get(attr)))
				.forEach(attr -> filtered.put(attr, attributes.get(attr)));

		return filtered;
	}

	private Map<String, Object> removingAttribute(Map<String, Object> attributes) {
		Map<String, Object> filtered = new HashMap<>();
		attributes.keySet()
				.stream()
				.filter(attr -> isRemovable(attributes.get(attr)))
				.forEach(attr -> filtered.put(attr, attributes.get(attr)));

		return filtered;
	}

	private boolean isRemovable(Object attributeValue) {
		if (attributeValue instanceof String) {
			return isNullOrEmpty((String) attributeValue);
		}

		return isNull(attributeValue);
	}
}
