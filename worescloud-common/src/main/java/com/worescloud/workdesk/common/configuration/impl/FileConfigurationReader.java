package com.worescloud.workdesk.common.configuration.impl;

import com.google.common.cache.Cache;
import com.google.common.io.CharSource;
import com.worescloud.workdesk.common.configuration.ConfigurationReader;
import com.worescloud.workdesk.common.configuration.NullableProperties;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static com.google.common.base.Charsets.UTF_8;
import static com.google.common.cache.CacheBuilder.newBuilder;
import static com.google.common.io.Resources.asCharSource;
import static com.google.common.io.Resources.getResource;
import static java.util.concurrent.TimeUnit.HOURS;

public class FileConfigurationReader implements ConfigurationReader {

	private Cache<String, Properties> propertiesCache = newBuilder()
			.expireAfterWrite(1L, HOURS)
			.build();

	@Override
	public Properties readProperties(String propertiesName) throws IOException {

		try {
			return propertiesCache.get(propertiesName, () -> {
				CharSource charSource = asCharSource(getResource(propertiesName), UTF_8);
				Properties prop = new NullableProperties();
				prop.load(charSource.openBufferedStream());
				return prop;
			});
		} catch (ExecutionException e) {
			return new NullableProperties();
		}
	}

}
