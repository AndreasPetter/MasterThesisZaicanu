package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

public abstract class Id {
	abstract public boolean equals(Object obj);
	abstract public int hashCode();
	abstract public byte[] getIdBytes();
}
