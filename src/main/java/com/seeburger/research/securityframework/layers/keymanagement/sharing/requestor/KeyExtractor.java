package com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import com.seeburger.research.securityframework.layers.keymanagement.datastructures.Id;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.IdBuilder;

public class KeyExtractor {
	private KeySharingPackage keyPackage;
	private volatile Object lock;

	public void addWaitLock(Object lock) {
		this.lock = lock;
	}

	public void setWaitLock(Object lock) {
		this.lock = lock;
	}

	public synchronized void setKeyPackage(KeySharingPackage keyPackage) {
		this.keyPackage = keyPackage;
	}

	public Map<Id, Key[]> getKeysForStorage(IdBuilder idBuilder,
			String[] algNames) {
		synchronized (lock) {
			while (keyPackage == null)
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		Map<Id, Key[]> keyMap = new HashMap<Id, Key[]>();
		if (idBuilder != null)
			for (KeyCoordinate keyCoord : keyPackage.getKeys()) {
				Id id = idBuilder.getId(keyCoord.getRowid(),
						keyCoord.getRowSerializerClass(),
						keyCoord.getColumnName(),
						keyCoord.getColumnNameSerializerClass());
				byte[] key1 = keyCoord.getKey1();
				byte[] key2 = keyCoord.getKey2();
				Key[] keys = new Key[2];
				if (key1 != null)
					keys[0] = new SecretKeySpec(key1, algNames[0]);
				if (key2 != null)
					keys[1] = new SecretKeySpec(key2, algNames[1]);
				keyMap.put(id, keys);
			}
		else {
			KeyCoordinate keyCoord = keyPackage.getKeys().get(0);
			byte[] key1 = keyCoord.getKey1();
			byte[] key2 = keyCoord.getKey2();
			Key[] keys = new Key[algNames.length];
			if (algNames.length >= 1)
				keys[0] = new SecretKeySpec(key1, algNames[0]);
			if (algNames.length == 2)
				keys[1] = new SecretKeySpec(key2, algNames[1]);
			keyMap.put(new Id() {

				@Override
				public boolean equals(Object obj) {
					// TODO Auto-generated method stub
					return true;
				}

				@Override
				public int hashCode() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public byte[] getIdBytes() {
					// TODO Auto-generated method stub
					return null;
				}

			}, keys);
		}
		return keyMap;
	}

}
