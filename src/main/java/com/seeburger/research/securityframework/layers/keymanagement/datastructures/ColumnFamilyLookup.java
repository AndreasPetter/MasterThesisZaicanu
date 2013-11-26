package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import java.security.Key;

import com.seeburger.research.securityframework.configuration.types.GranularityType;

public abstract class ColumnFamilyLookup {
	abstract public Key[] lookupKeys(Object rowId, Object column);
	abstract public GranularityType getSharingGranularity();
}
