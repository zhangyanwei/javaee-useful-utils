package com.worescloud.workdesk.common.configuration;

import java.io.IOException;

public interface ConfigurationReader {

	java.util.Properties readProperties(String propertiesName) throws IOException;

}
