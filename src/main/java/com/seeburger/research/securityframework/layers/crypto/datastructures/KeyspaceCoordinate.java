package com.seeburger.research.securityframework.layers.crypto.datastructures;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class KeyspaceCoordinate extends CompositeCoordinate {

	private String thisKeyspace;

	public KeyspaceCoordinate(String keyspace, CryptoAlgorithm crypto) {
		super(crypto);
		thisKeyspace = keyspace;
	}

	@Override
	protected Object getId() {
		return thisKeyspace;
	}
	
	public ColumnFamilyCoordinate getColumnFamilyCoordinate(String cfName){
		return (ColumnFamilyCoordinate) nestedCoordinates.get(cfName);
	}

	@Override
	public CryptoAlgorithm lookup(String keyspace, String columnFamily,
			Object column) {
		Coordinate cfCoordinate = nestedCoordinates.get(columnFamily);
		if (cfCoordinate != null) {
			return cfCoordinate.lookup(keyspace, columnFamily, column);
		} else {
			if (otherCoordinates != null) {
				return otherCoordinates.lookup(keyspace, columnFamily, column);
			} else {
				return cryptoAlg;// return default algorithm for KS
			}
		}
	}
	
	public CryptoAlgorithm getCryptoAlg(){
		return cryptoAlg;
	}

}
