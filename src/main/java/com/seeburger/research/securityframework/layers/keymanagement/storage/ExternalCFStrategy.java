package com.seeburger.research.securityframework.layers.keymanagement.storage;

import java.security.Key;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.Id;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyExtractor;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyRequestor;

public class ExternalCFStrategy extends StorageStrategy{
	
	private final String keyspaceName;
	private final String cfName;
	private Key[] sharedCfKeys;

	public ExternalCFStrategy(String keyspace, String cfName, String[] algNames) {
		super(algNames);
		this.keyspaceName = keyspace;
		this.cfName = cfName;
	}

	@Override
	public Key[] getKeysFor(Object rowId, Object columnName, Key... keys) {
		if (sharedCfKeys == null){
			String requestId = RandomStringUtils.randomAscii(56);
			// ask policy to get you the keys
			ResourceCoordinates keyCoord = new ResourceCoordinates();
			keyCoord.setKeyspace(keyspaceName);
			keyCoord.setCfName(cfName);
			KeyExtractor extractor = new KeyExtractor();
			KeyRequestor.registerRequest(requestId, extractor, keyCoord);
			Map<Id, Key[]> receivedKeys = extractor.getKeysForStorage(null, algNames);
			for (Id id : receivedKeys.keySet()){
				sharedCfKeys = receivedKeys.get(id);
			}
		}
		return sharedCfKeys;
	}

}
