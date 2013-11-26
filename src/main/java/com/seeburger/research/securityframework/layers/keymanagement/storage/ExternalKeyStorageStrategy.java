package com.seeburger.research.securityframework.layers.keymanagement.storage;

import java.security.Key;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.Id;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyExtractor;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyRequestor;

public class ExternalKeyStorageStrategy extends StorageStrategy {

	// TODO : [ideally] must be backed up by persistent store
	private final Map<Id, Key[]> permanentStorage;
	private final static String CHARSET = "UTF-8";
	private final String keyspace;
	private final String columnFamilyName;

	// upon initialization reads any stored keys from previous sessions..
	// not implemented for now.
	public ExternalKeyStorageStrategy(String keyspace, String cfName, String algNames[],
			String locationOfPreviouslyStoredKeys) {
		super(algNames);
		this.keyspace = keyspace;
		this.columnFamilyName = cfName;
		permanentStorage = Collections
				.synchronizedMap(new HashMap<Id, Key[]>());

	}

	@Override
	public Key[] getKeysFor(Object rowId, Object columnName, Key... k) {
		// note in this case the parameter "k" is simply discarded
		Id id = idBuilder.getId(rowId, columnName);
		Key[] keys = permanentStorage.get(id);
		if (keys == null) {// must ask now
			String requestId = RandomStringUtils.randomAscii(56);
			// ask policy to get you the keys
			ResourceCoordinates keyCoord = idBuilder.getResourceCoordinates(
					keyspace, columnFamilyName, rowId, columnName);
			KeyExtractor extractor = new KeyExtractor();
			KeyRequestor.registerRequest(requestId, extractor, keyCoord);
			Map<Id, Key[]> receivedKeys = extractor.getKeysForStorage(idBuilder, algNames);
			for (Id receivedId : receivedKeys.keySet()){
				if (id.equals(receivedId)){
					keys = receivedKeys.get(receivedId);
					break;
				}
			}
			permanentStorage.putAll(receivedKeys);
		}
		return keys;
	}
}
