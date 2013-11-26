package com.seeburger.research.securityframework.layers.crypto.datastructures;

import java.util.Map;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class UniversalCoordinate extends Coordinate{
	
	private Chameleon matchesAny = Chameleon.singleton;

	public UniversalCoordinate(CryptoAlgorithm crypto) {
		super(crypto);
	}

	@Override
	protected Map<Object, Coordinate> getNestedCoordinates() {
		return null;
	}

	@Override
	public CryptoAlgorithm lookup(String keyspace, String columnFamily,
			Object column) {
		return cryptoAlg;
	}

	@Override
	protected Object getId() {
		return matchesAny;
	}
	
	private static class Chameleon {
		@Override
		public boolean equals(Object obj){
			if (obj == null)
				return false;
			else
				return true;
		}
		
		private static Chameleon singleton = new Chameleon();
	}

}
