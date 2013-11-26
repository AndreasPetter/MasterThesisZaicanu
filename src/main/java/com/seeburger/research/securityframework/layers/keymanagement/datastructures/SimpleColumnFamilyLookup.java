package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import java.security.Key;

import com.seeburger.research.securityframework.configuration.types.GranularityType;

/**
 * This class is used when the sharing granularity of a column family is a 
 * the column family itself.
 * 
 * @author Eugen
 *
 */
public class SimpleColumnFamilyLookup extends ColumnFamilyLookup{
	
	private Key[] cfKeys;
	
	public SimpleColumnFamilyLookup(Key cfKeyEnc, Key cfKeyInt) {
		cfKeys = new Key[2];
		cfKeys[0] = cfKeyEnc;
		cfKeys[1] = cfKeyInt;
	}

	@Override
	public Key[] lookupKeys(Object rowId, Object column) {
		return cfKeys;
	}

	@Override
	public GranularityType getSharingGranularity() {
		return GranularityType.SELF;
	}
}
