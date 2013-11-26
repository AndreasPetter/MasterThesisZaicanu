package com.seeburger.research.securityframework.layers.crypto.datastructures;

import java.util.Map;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class ColumnCoordinate extends Coordinate{
	
	private Object thisColumnName;

	public ColumnCoordinate(Object columnName, CryptoAlgorithm crypto) {
		super(crypto);
		thisColumnName = columnName;
	}

	@Override
	protected Map<Object, Coordinate> getNestedCoordinates() {
		return null;
	}

	@Override
	public CryptoAlgorithm lookup(String keyspace, String columnFamily,
			Object column) {
		if (thisColumnName.equals(column))
			return cryptoAlg;
		else
			return null;
	}

	@Override
	protected Object getId() {
		return thisColumnName;
	}

}
