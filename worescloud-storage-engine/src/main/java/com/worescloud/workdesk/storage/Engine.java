package com.worescloud.workdesk.storage;

import com.worescloud.workdesk.storage.exception.StorageException;

public interface Engine {
	void initialize(Configuration configuration);
	<T> Storage<T> storage(Class<T> type) throws StorageException;
}
