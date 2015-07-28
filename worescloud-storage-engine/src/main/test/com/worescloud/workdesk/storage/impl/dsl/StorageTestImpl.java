package com.worescloud.workdesk.storage.impl.dsl;

import com.worescloud.workdesk.storage.*;
import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery;
import com.worescloud.workdesk.storage.exception.StorageException;
import com.worescloud.workdesk.storage.impl.dsl.StorageTestDefinition.ExpectedMatch;
import com.worescloud.workdesk.storage.impl.dsl.StorageTestDefinition.ExpectedSucceed;
import com.worescloud.workdesk.storage.impl.dsl.StorageTestDefinition.GivenType;
import com.worescloud.workdesk.storage.impl.dsl.StorageTestDefinition.Type;
import com.worescloud.workdesk.storage.mongodb.MongoDBConfiguration;
import com.worescloud.workdesk.storage.mongodb.MongoDBEngine;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class StorageTestImpl<T> implements GivenType<T>, Type<T>, ExpectedSucceed<T>, ExpectedMatch<T> {

	public static <T> Type<T> type(Class<T> type) throws StorageException {
		return new StorageTestImpl<T>().givenType(type);
	}

	private Storage<T> storage;
	private DelayEventChain eventChain;

	@Override
	public Type<T> givenType(Class<T> type) throws StorageException {
		initialize(type);
		return this;
	}

	@Override
	public ExpectedSucceed<T> save(T entity, Supplier keySupplier) {
		delayExecute(v -> {
			storage.save(entity);
			return null;
		}, v -> storage.delete(keySupplier.get()));
		return this;
	}

	@Override
	public ExpectedSucceed<T> saveOrUpdate(T entity, Supplier keySupplier) {
		delayExecute(v -> {
			storage.saveOrUpdate(entity);
			return null;
		}, v -> storage.delete(keySupplier.get()));
		return this;
	}

	@Override
	public ExpectedSucceed<T> saveOrUpdate(T entity, Set<String> attributes, Supplier keySupplier) {
		delayExecute(v -> {
			storage.saveOrUpdate(entity, attributes);
			return null;
		}, v -> storage.delete(keySupplier.get()));
		return this;
	}

	@Override
	public ExpectedSucceed<T> update(T entity, Set<String> attributes) {
		delayExecute(v -> { storage.update(entity, attributes); return null; });
		return this;
	}

	@Override
	public ExpectedSucceed<T> delete(Object key) {
		delayExecute(v -> storage.delete(key));
		return this;
	}

	@Override
	public ExpectedMatch<Optional<T>> find(Object key) {
		delayExecute(v -> storage.find(key));
		return (ExpectedMatch<Optional<T>>) this;
	}

	@Override
	public ExpectedMatch<T> query(Function<StorageQuery<T>, ?> function) {
		delayExecute(v -> function.apply(storage.query()));
		return this;
	}

	@Override
	public void success() throws StorageException {
		delayExecute(v -> {
			assert true;
			return null;
		});
		eventChain.execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <R> void match(Predicate<R> predicate) throws StorageException {
		delayExecute(v -> predicate.test((R) v));
		eventChain.execute();
	}

	private void initialize(Class<T> type) throws StorageException {
		EngineConfiguration engine = new EngineConfiguration() {

			@Override
			public Class<? extends Engine> engineType() {
				return MongoDBEngine.class;
			}

			@Override
			public Configuration getConfiguration() {
				return new MongoDBConfiguration()
						.uri("mongodb://localhost:27017")
						.database("worescloud&test");
			}
		};

		storage = EngineFactory.create(engine).storage(type);
		eventChain = new DelayEventChain();
	}

	private void delayExecute(Execute before) {
		delayExecute(before, null);
	}

	private void delayExecute(Execute before, Execute after) {
		eventChain.add(new DelayEvent() {
			@Override
			public Object before(Object previousValue) throws StorageException {
				return before != null ? before.execute(previousValue) : null;
			}

			@Override
			public Object after(Object previousValue) throws StorageException {
				return after != null ? after.execute(previousValue) : null;
			}
		});
	}

	public interface Execute {
		Object execute(Object value) throws StorageException;
	}

	public interface DelayEvent {
		Object before(Object previousValue) throws StorageException;
		Object after(Object previousValue) throws StorageException;
	}

	public static class DelayEventChain {

		private List<DelayEvent> events;

		public DelayEventChain() {
			events = new ArrayList<>();
		}

		public Object execute() throws StorageException {
			return execute(events.iterator(), null);
		}

		private Object execute(Iterator<DelayEvent> iterator, Object value) throws StorageException {
			if (iterator.hasNext()) {
				DelayEvent event = iterator.next();
				Object after = null;
				try {
					Object before = event.before(value);
					after = execute(iterator, before);
				} finally {
					after = event.after(after);
				}

				return after;
			}

			return null;
		}

		public void add(DelayEvent delayEvent) {
			events.add(delayEvent);
		}

	}
}
