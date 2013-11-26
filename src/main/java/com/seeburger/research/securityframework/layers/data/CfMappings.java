package com.seeburger.research.securityframework.layers.data;

import java.util.HashMap;
import java.util.Map;

public class CfMappings {
	private String defaultValidationClass;
	private Map<Object, String> fixedColumnDataMappings;
	
	public CfMappings(){
		
	}
	
	public void addMapping(Object columnName, String validationClass){
		if (fixedColumnDataMappings == null){
			fixedColumnDataMappings = new HashMap<Object, String>();
		}
		fixedColumnDataMappings.put(columnName, validationClass);
	}
	
	public void removeMapping(Object columnName){
		if (fixedColumnDataMappings != null)
			fixedColumnDataMappings.remove(columnName);
	}

	public void addDefaultMapping(String defaultValidationClass){
		this.defaultValidationClass = defaultValidationClass;
	}
	
	@Override
	public String toString(){
		String result = fixedColumnDataMappings.toString()+"\n";
		result += defaultValidationClass == null ? "NO Default Mapping" : defaultValidationClass + "<-> bytes";
		return result;
	}
}
