package com.worescloud.workdesk.storage;

import com.worescloud.workdesk.storage.exception.StorageException;

public final class EngineFactory {

	public static Engine create(EngineConfiguration configuration) throws StorageException {
		try {
			Engine engine = configuration.engineType().newInstance();
			engine.initialize(configuration.getConfiguration());
			return engine;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new StorageException(e);
		}
	}

}
