package com.seeburger.research.securityframework.layers.crypto.datastructures;

import java.util.HashMap;
import java.util.Map;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

abstract public class CompositeCoordinate extends Coordinate {

	protected Map<Object, Coordinate> nestedCoordinates = new HashMap<Object, Coordinate>();
	protected UniversalCoordinate otherCoordinates;

	protected CompositeCoordinate(CryptoAlgorithm crypto) {
		super(crypto);
	}

	protected Map<Object, Coordinate> getNestedCoordinates() {
		return nestedCoordinates;
	}
	
	public void addCoordinate(Coordinate cc) {
		if (cc instanceof UniversalCoordinate)
			otherCoordinates = (UniversalCoordinate) cc;
		else
			nestedCoordinates.put(cc.getId(), cc);
	}
}
