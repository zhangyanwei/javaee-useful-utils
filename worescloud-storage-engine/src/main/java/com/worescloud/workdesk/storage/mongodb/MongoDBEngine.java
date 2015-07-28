package com.worescloud.workdesk.storage.mongodb;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import com.worescloud.workdesk.storage.Configuration;
import com.worescloud.workdesk.storage.Engine;
import com.worescloud.workdesk.storage.Storage;
import com.worescloud.workdesk.storage.exception.StorageException;

public class MongoDBEngine implements Engine {

	private MongoDBConfiguration configuration;
	private MongoDatabase database;

	@Override
	public void initialize(Configuration configuration) {
		this.configuration = (MongoDBConfiguration) configuration;
	}

	@Override
	public <T> Storage<T> storage(Class<T> type) throws StorageException {

		MongoClientURI connectionString = new MongoClientURI(this.configuration.getUri());
		MongoClient mongoClient = new MongoClient(connectionString);
		database = mongoClient.getDatabase(this.configuration.getDatabase());

		MongoDBStorage<T> storage = new MongoDBStorage<>();
		storage.initialize(this, type);
		return storage;
	}

	public MongoDBConfiguration getConfiguration() {
		return configuration;
	}

	public MongoDatabase getDatabase() {
		return database;
	}
}
