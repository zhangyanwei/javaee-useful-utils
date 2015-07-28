package com.worescloud.workdesk.storage;

public interface StorageStreamBuilder {

	interface StorageStream<E> extends StorageFilter<E> {
	}

	interface StorageFilter<E> extends StorageAction<E> {
		StorageFilter<E> filter(Filter filter);
	}

	interface StorageAction<E> {
		boolean delete();
	}

}
