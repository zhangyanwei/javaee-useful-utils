package com.worescloud.workdesk.storage.mongodb;

import com.worescloud.workdesk.storage.Configuration;

public class MongoDBConfiguration implements Configuration {

	private String uri;
	private String database;

	public String getUri() {
		return uri;
	}

	public String getDatabase() {
		return database;
	}

	public MongoDBConfiguration uri(String uri) {
		this.uri = uri;
		return this;
	}

	public  MongoDBConfiguration database(String database) {
		this.database = database;
		return this;
	}
}
