package com.worescloud.workdesk.storage.impl.dsl;

import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery;
import com.worescloud.workdesk.storage.exception.StorageException;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface StorageTestDefinition {

	interface GivenType<T> {
		Type<T> givenType(Class<T> type) throws StorageException;
	}

	interface DoAction<T> {
		ExpectedSucceed<T> save(T entity, Supplier keySupplier);
		ExpectedSucceed<T> saveOrUpdate(T entity, Supplier keySupplier);
		ExpectedSucceed<T> saveOrUpdate(T entity, Set<String> attributes, Supplier keySupplier);
		ExpectedSucceed<T> update(T entity, Set<String> attributes);
		ExpectedSucceed<T> delete(Object key);
		ExpectedMatch<Optional<T>> find(Object key);
		ExpectedMatch<T> query(Function<StorageQuery<T>, ?> function);
	}

	interface Type<T> extends DoAction<T> {
	}

	interface ExpectedSucceed<T> extends DoAction<T> {
		void success() throws StorageException;
	}

	interface ExpectedMatch<T> {
		<R> void match(Predicate<R> r) throws StorageException;
	}
}
