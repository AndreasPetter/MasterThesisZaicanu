package com.seeburger.research.securityframework.layers.crypto.datastructures;

import java.util.Map;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

abstract public class Coordinate {
	protected CryptoAlgorithm cryptoAlg;

	protected Coordinate(CryptoAlgorithm crypto) {
		cryptoAlg = crypto;
	}
	
	protected abstract Map<Object, Coordinate> getNestedCoordinates();

	public abstract CryptoAlgorithm lookup(String keyspace,
			String columnFamily, Object column);
	
	protected abstract Object getId();

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Coordinate))
			return false;
		Coordinate other = (Coordinate) obj;
		return other.getId().equals(other.getId());
	}
	
	@Override
	public int hashCode(){
		return getId().hashCode();
	}
}
