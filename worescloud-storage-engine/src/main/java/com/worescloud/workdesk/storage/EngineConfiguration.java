package com.worescloud.workdesk.storage;

public interface EngineConfiguration {
	Class<? extends Engine> engineType();
	Configuration getConfiguration();
}
