package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import java.security.Key;

import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.layers.keymanagement.storage.StorageStrategy;


public class ColumnFamilyLookupExt extends ColumnFamilyLookup {
	private final StorageStrategy storage;
	private Key cfKeyEnc;
	private Key cfKeyInt;
	
	public ColumnFamilyLookupExt(StorageStrategy storage){
		this.storage = storage;
	}
	
	public ColumnFamilyLookupExt(Key encKey, Key intKey, StorageStrategy storage){
		cfKeyEnc = encKey;
		cfKeyInt = intKey;
		this.storage = storage;
	}

	@Override
	public Key[] lookupKeys(Object rowId, Object column) {
		return storage.getKeysFor(rowId, column, cfKeyEnc, cfKeyInt);
	}

	@Override
	public GranularityType getSharingGranularity() {
		return storage.getIdBuilder().getGranularity();
	}
}
