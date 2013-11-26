package com.seeburger.research.securityframework.layers.keymanagement.storage;

import javax.crypto.Mac;


public class NoCachingStorageStrategy extends KeyOwnerStorageStrategy {

	public NoCachingStorageStrategy(String[] algNames, Mac mc) {
		super(algNames, mc);
	}

}
