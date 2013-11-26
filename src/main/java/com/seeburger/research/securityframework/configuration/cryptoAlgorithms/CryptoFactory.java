package com.seeburger.research.securityframework.configuration.cryptoAlgorithms;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.Mac;

import org.apache.commons.lang.NotImplementedException;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class CryptoFactory {

	public static CryptoAlgorithm newAuthEncCryptoAlgorithm(String providerID,
			String authenticatedEncryptionAlgorithm) throws Exception {
		if (providerID == null || providerID.equals(""))
			return new AuthenticatedEncryptionAlgorithm(authenticatedEncryptionAlgorithm);
		else
			return new AuthenticatedEncryptionAlgorithm(authenticatedEncryptionAlgorithm, providerID);
	}

	public static CryptoAlgorithm newIntegrityAlgorithm(String providerID,
			String integrityAlgorithm) {
		throw new NotImplementedException();
	}

	public static CryptoAlgorithm newCustomAuthEncAlgorithm(String providerID,
			String encryptionAlgorithm, String integrityAlgorithm) throws Exception{
		if (providerID == null || providerID.equals(""))
			return new CustomAuthEncryption(encryptionAlgorithm, integrityAlgorithm);
		else{
			return new CustomAuthEncryption(providerID, encryptionAlgorithm, integrityAlgorithm);
		}
	}
}
