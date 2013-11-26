package com.seeburger.research.securityframework.layers.crypto;

import java.security.Key;

abstract public class CryptoAlgorithm implements Cloneable{
	
	protected final String[] algNames;
	
	public CryptoAlgorithm(String... algNames){
		this.algNames = algNames;
	}
	
	abstract public byte[] process(byte[] plainText, Key... keys) throws Exception;
	abstract public byte[] recover(byte[] cipherText, Key... keys) throws Exception;
	
	abstract public CryptoAlgorithm clone();
	
	public String[] getAlgorithmNames(){
		return algNames;
	}
}
