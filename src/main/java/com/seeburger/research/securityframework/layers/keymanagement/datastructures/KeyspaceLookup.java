package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.WSAddress;

public class KeyspaceLookup {
	
	private Key ksEncKey;
	private Key ksIntKey;
	
	private Map<String, ColumnFamilyLookup> cfLookups = new HashMap<String, ColumnFamilyLookup>();

	public void setGenerationKeys(Key encKey, Key intKey) {
		ksEncKey = encKey;
		ksIntKey = intKey;
	}
	
	public void addColumnFamilyLookup(String cfName, ColumnFamilyLookup cflookUp){
		cfLookups.put(cfName, cflookUp);
	}
	
	public Key[] lookupKeys(String cfName, Object rowId, Object column){
		ColumnFamilyLookup cfl = cfLookups.get(cfName);
		if (cfl == null){
			throw new RuntimeException("No such CF: "+cfName);
		}
		return cfl.lookupKeys(rowId, column);
	}

	public GranularityType getGranularityFor(String cfName) {
		ColumnFamilyLookup cfl = cfLookups.get(cfName);
		if (cfl == null)
			throw new RuntimeException("Framework misconfiguration !\n" +
					"Column Family lookup was null when an encrypted column family name was supplied: "+cfName);
		return cfl.getSharingGranularity();
	}

}
