package com.seeburger.research.securityframework.layers.keymanagement.storage;

import java.security.Key;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.crypto.Mac;

import com.seeburger.research.securityframework.layers.keymanagement.datastructures.Id;

public class CachingStorageStrategy extends KeyOwnerStorageStrategy {

	private final Map<Id, Key[]> lruCacheSync;

	public CachingStorageStrategy(String[] algNames, Mac mac, int cacheSize) {
		super(algNames, mac);
		lruCacheSync = Collections.synchronizedMap(new LRUCache<Id, Key[]>(cacheSize));
	}

	@Override
	public Key[] getKeysFor(Object rowId, Object columnName, Key... keys) {
		Id id = idBuilder.getId(rowId, columnName);
		Key[] keyArray = lruCacheSync.get(id);
		if (keyArray == null) {
			keyArray = generateKeys(id, rowId, columnName, keys);
			lruCacheSync.put(id, keyArray);
		}
		return keyArray;
	}
}

class LRUCache<K, V> extends LinkedHashMap<K, V> {

	private final int maxEntries;

	public LRUCache(final int maxEntries) {
		super(maxEntries + 1, 1.0f, true);
		this.maxEntries = maxEntries;
	}

	@Override
	protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
		return super.size() > maxEntries;
	}
}
