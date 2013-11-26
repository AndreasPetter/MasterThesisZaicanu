package com.seeburger.research.securityframework.layers.crypto.datastructures;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class ColumnFamilyCoordinate extends CompositeCoordinate {

	private String thisColumnFamilyName;

	public ColumnFamilyCoordinate(String columnFamilyName,
			CryptoAlgorithm crypto) {
		super(crypto);
		thisColumnFamilyName = columnFamilyName;
	}

	@Override
	protected Object getId() {
		return thisColumnFamilyName;
	}

	@Override
	public CryptoAlgorithm lookup(String keyspace, String columnFamily,
			Object column) {
		Coordinate nestedCoordinate = nestedCoordinates.get(column);
		if (nestedCoordinate != null) {
			return nestedCoordinate.lookup(keyspace, columnFamily, column);
		} else {
			if (otherCoordinates != null) {
				return otherCoordinates.lookup(keyspace, columnFamily, column);
			} else {
				return cryptoAlg;// return default algorithm for CF.
			}
		}
	}

}
