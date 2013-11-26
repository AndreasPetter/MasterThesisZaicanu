package com.seeburger.research.securityframework.layers.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Seems that this class is not needed at all.
 * All info for conversion is available during decryption process.
 * @author Eugen
 *
 */
public class DataConversion {
	/**
	 * Note the original byte mappings are not stored, i.e. if no mapping detected
	 * assume original data type was bytes.
	 */
	private static Map<String, CfMappings> dataConversion = new HashMap<String, CfMappings>();
	
	public static void addMapping(String keyspace, String columnFamilyName, Object column, String validationClass){
		String key = keyspace+"%"+columnFamilyName;
		CfMappings cfmaps = dataConversion.get(key);
		if (cfmaps == null){
			cfmaps = new CfMappings();
		}
		cfmaps.addMapping(column, validationClass);
		dataConversion.put(key, cfmaps);
	}

	public static void addDefaultMapping(String keyspace, String cfName,
			String validationClass) {
		String key = keyspace+"%"+cfName;
		CfMappings cfmaps = dataConversion.get(key);
		if (cfmaps == null){
			cfmaps = new CfMappings();
		}
		cfmaps.addDefaultMapping(validationClass);
	}
	
}
