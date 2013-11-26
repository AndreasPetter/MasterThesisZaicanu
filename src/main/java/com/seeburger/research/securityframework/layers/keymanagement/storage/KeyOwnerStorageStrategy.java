package com.seeburger.research.securityframework.layers.keymanagement.storage;

import java.security.InvalidKeyException;
import java.security.Key;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.seeburger.research.securityframework.layers.keymanagement.datastructures.Id;

public class KeyOwnerStorageStrategy extends StorageStrategy{
	
	private final Mac macAlgorithm;
	
	public KeyOwnerStorageStrategy(String[] algNames, Mac mc) {
		super(algNames);
		macAlgorithm = mc;
	}

	@Override
	public Key[] getKeysFor(Object rowId, Object columnName, Key... columnFamilyKeys) {
		Id id = idBuilder.getId(rowId, columnName);
		return generateKeys(id, rowId, columnName, columnFamilyKeys);
	}
	
	protected Key[] generateKeys(Id id, Object rowId, Object columnName,
			Key... keys) {
		List<Key> newKeys = new ArrayList<Key>(2);
		int i = 0;
		synchronized (macAlgorithm) {
			for (String algName : algNames) {
				if (algName != null) {
					try {
						macAlgorithm.init(keys[i]);
						byte[] newKey = macAlgorithm.doFinal(id.getIdBytes());
						newKeys.add(new SecretKeySpec(newKey, algName));
					} catch (InvalidKeyException e) {
						throw new RuntimeException(e);
					}
				}
				i++;
			}
		}
		return newKeys.toArray(new Key[0]);
	}

}
