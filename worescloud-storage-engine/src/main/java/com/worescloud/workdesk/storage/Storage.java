package com.worescloud.workdesk.storage;

import com.worescloud.workdesk.storage.StorageQueryBuilder.StorageQuery;
import com.worescloud.workdesk.storage.StorageStreamBuilder.StorageStream;
import com.worescloud.workdesk.storage.exception.StorageException;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Storage<E> {

	void initialize(Engine engine, Class<E> type) throws StorageException;

	void save(E entity) throws StorageException;

	void saveOrUpdate(E entity) throws StorageException;

	void saveOrUpdate(E entity, Set<String> attributes) throws StorageException;

	void batchSaveOrUpdate(List<E> entities) throws StorageException;

	void batchSaveOrUpdate(List<E> entities, Set<String> attributes) throws StorageException;

	void update(E entity, Set<String> attributes) throws StorageException;

	boolean delete(Object key);

	boolean delete(Object... keys);

	boolean delete(Filter filter);

	boolean exists(Object key);

	Optional<E> find(Object key) throws StorageException;

	Optional<E> find(Object key, String... retrieveAttributes) throws StorageException;

	E findOrSave(Object key, E entity) throws StorageException;

	StorageQuery<E> query();

	StorageStream<E> stream();
}
