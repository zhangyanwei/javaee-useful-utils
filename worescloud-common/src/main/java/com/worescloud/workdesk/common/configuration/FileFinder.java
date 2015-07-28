package com.worescloud.workdesk.common.configuration;

import com.worescloud.workdesk.common.exception.CommonException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileFinder {

	public static File findFile(String filename, String dirRegex) throws CommonException {

		dirRegex = processPath(dirRegex);

		File file = new File(dirRegex, filename);
		if (!file.exists()) {
			try {
				ClassLoader loader = Thread.currentThread()
						.getContextClassLoader();
				if (loader == null) {
					loader = CoreProperties.class.getClassLoader();
				}
				if (loader != null) {
					URL url = file.isAbsolute() ? loader.getResource(filename)
							: loader.getResource(file.getPath());
					if (url != null) {
						String newFilename = URLDecoder.decode(url.getFile(),
								"ISO-8859-1");
						file = new File(newFilename);
					}
				}
				if (!file.exists()) {
					file = new File(filename);
				}
			} catch (SecurityException | IOException e) {
				// log.error(e);
				throw new CommonException(e);
			}
		}

		return file;
	}

	private static String processPath(String defaultDir) {
		Pattern pattern = Pattern.compile("\\$\\{(\\p{Alpha}+[\\w\\.]*)\\}");
		Matcher matcher = pattern.matcher(defaultDir);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			String variable = matcher.group(1);
			String value = System.getProperty(variable);
			if (value != null) {
				value = value.replaceAll("\\\\", "\\\\\\\\");
				matcher.appendReplacement(sb, value);
			}
		}

		matcher.appendTail(sb);
		return sb.toString();
	}

}
