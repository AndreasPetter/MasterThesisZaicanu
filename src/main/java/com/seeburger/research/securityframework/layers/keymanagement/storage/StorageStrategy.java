package com.seeburger.research.securityframework.layers.keymanagement.storage;

import java.security.Key;

import javax.crypto.Mac;

import com.seeburger.research.securityframework.layers.keymanagement.datastructures.IdBuilder;

public abstract class StorageStrategy {
	
	protected IdBuilder idBuilder;
	protected final String[] algNames;
	
	public StorageStrategy(String[] algNames){
		this.algNames = algNames;
	}
	
	public void setIdBuilder(IdBuilder idBuilder) {
		this.idBuilder = idBuilder;
	}
	
	public IdBuilder getIdBuilder(){
		return idBuilder;
	}
	
	abstract public Key[] getKeysFor(Object rowId, Object columnName, Key... keys);

}
