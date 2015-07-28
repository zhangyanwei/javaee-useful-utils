package com.worescloud.workdesk.storage;

import com.worescloud.workdesk.storage.exception.StorageException;

import java.util.List;

public interface StorageQueryBuilder {

	interface StorageQuery<E> extends StorageFilter<E> {
		StorageFilter<E> full();
		StorageFilter<E> select(String... attributes);
	}

	interface StorageFilter<E> extends StorageAction<E> {
		StorageFilter<E> filter(Filter filter);
		StorageFilter<E> sort(Sort sort);
		StorageFilter<E> skip(int skip);
		StorageFilter<E> limit(int limit);
	}

	interface StorageAction<E> {
		E one() throws StorageException;
		List<E> collect() throws StorageException;
		E index(int index) throws StorageException;
		long count() throws StorageException;
		boolean exist() throws StorageException;
	}

}