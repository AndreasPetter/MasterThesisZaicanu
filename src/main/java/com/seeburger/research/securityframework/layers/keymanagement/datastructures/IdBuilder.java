package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import me.prettyprint.hector.api.Serializer;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.configuration.types.GranularityType;

public abstract class IdBuilder {
	
	protected static Map<String, Serializer> serializers = Collections.synchronizedMap(new HashMap<String, Serializer>());
	
	public abstract Id getId(Object rowId, Object columnName);
	public abstract ResourceCoordinates getResourceCoordinates(String keypace, String cfName, Object rowId, Object columnName);
	public abstract GranularityType getGranularity();
	
	public abstract Id getId(byte[] rowid, String rowSerializerClass, byte[] columnName,
			String columnNameSerializerClass);
	
	public static Serializer getSerializerFor(String className){
		Serializer ser = serializers.get(className);
		if (ser == null){
			try {
				ser = (Serializer) IdBuilder.class.forName(className).getMethod("get", null).invoke(null, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			} 
		}
		return ser;
	}
}
