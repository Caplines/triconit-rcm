package com.tricon.esdatareplication.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.tricon.esdatareplication.dto.PropFileDto;

public class CustomPropFileCache {

	final private static String PROPERTIES_FILE_PATH = "c:/es_rep/repconfig.properties";
	final static Properties properties = new Properties();

	public static Map<String, PropFileDto> cache = new HashMap<>(); // GLOBAL VARIABLE

	public static void readFileProperty() {

		PropFileDto f = new PropFileDto();
		try (InputStream inputStream = new FileInputStream(PROPERTIES_FILE_PATH)) {

			properties.load(inputStream);
			f.setEsDbuser(properties.getProperty("es.dbuser"));
			f.setEsDbPass(properties.getProperty("es.dbpassword"));
			f.setLogLocation(properties.getProperty("log.location"));
			cache.put("1", f);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
