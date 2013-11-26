package com.seeburger.research.securityframework.configuration.cryptoAlgorithms;

import java.lang.reflect.Array;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

public class CustomAuthEncryption extends CryptoAlgorithm {
	
	private final Cipher algorithm;
	private final boolean isDeterministic;
	private final int macSize;
	private final Mac mac;
	private final Object encLock = new Object();
	private final Object decLock = new Object();

	public CustomAuthEncryption(String encryptionAlgorithm,
			String integrityAlgorithm) throws Exception{
		super(encryptionAlgorithm, integrityAlgorithm);
		algorithm = Cipher.getInstance(encryptionAlgorithm);
		isDeterministic = checkIfDeterministic(algorithm);
		mac = Mac.getInstance(integrityAlgorithm);
		macSize = mac.getMacLength();
	}

	public CustomAuthEncryption(String providerID, String encryptionAlgorithm,
			String integrityAlgorithm) throws Exception{
		super(encryptionAlgorithm, integrityAlgorithm);
		algorithm = Cipher.getInstance(encryptionAlgorithm, providerID);
		isDeterministic = checkIfDeterministic(algorithm);
		mac = Mac.getInstance(integrityAlgorithm, providerID);
		macSize = mac.getMacLength();
	}
	
	private boolean checkIfDeterministic(Cipher cipher){
		try {
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(new byte[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,}, cipher.getAlgorithm()));
			return cipher.getParameters() == null;
		} catch (InvalidKeyException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public byte[] process(byte[] plainTextb, Key... keys) throws Exception {
		byte[] ivb = null;
		byte[] c = null;
		byte[] hashb = null;
		synchronized (encLock) {
			algorithm.init(Cipher.ENCRYPT_MODE, keys[0]);
			if (!isDeterministic)
				ivb = algorithm.getIV();
			c = algorithm.doFinal(plainTextb);
			mac.init(keys[1]);
			hashb = mac.doFinal(c);
		}
		byte[] cipherTextb = new byte[(ivb == null ? 0  : ivb.length)
			   			        + macSize
			   					+ c.length];
		if (!isDeterministic) {
			System.arraycopy(c, 0, cipherTextb, ivb.length+macSize, c.length);
			System.arraycopy(hashb, 0, cipherTextb, ivb.length, macSize);
			System.arraycopy(ivb, 0, cipherTextb, 0, ivb.length);
		}
		else{
			System.arraycopy(hashb, 0, cipherTextb, 0, macSize);
			System.arraycopy(c, 0, cipherTextb, macSize, c.length);
		}
		return cipherTextb;
	}

	@Override
	public byte[] recover(byte[] cipherText, Key... keys) throws BadPaddingException, Exception {
		if (isDeterministic){
			byte[] tag = Arrays.copyOfRange(cipherText, 0, macSize);
			byte[] ct = Arrays.copyOfRange(cipherText, macSize, cipherText.length);
			synchronized (decLock) {
				mac.init(keys[1]);
				byte[] computedTag = mac.doFinal(ct);
				for (int i = 0; i < computedTag.length; i++){
					if (tag[i] != computedTag[i])
						throw new BadPaddingException("Mac check failed.");
				}
				// mac verification passed. Decrypt
				algorithm.init(Cipher.DECRYPT_MODE, keys[0]);
				byte pt[] = algorithm.doFinal(ct);
				return pt;
			}
		}else{
			byte[] ivb = Arrays.copyOfRange(cipherText, 0, 16);
			byte[] tag = Arrays.copyOfRange(cipherText, ivb.length, ivb.length+macSize);
			byte[] ct = Arrays.copyOfRange(cipherText, ivb.length+macSize, cipherText.length);
			synchronized (decLock) {
				mac.init(keys[1]);
				byte[] computedTag = mac.doFinal(ct);
				for (int i = 0; i < computedTag.length; i++){
					if (tag[i] != computedTag[i])
						throw new BadPaddingException("Mac check failed.");
				}
				// mac verification passed. Decrypt
				algorithm.init(Cipher.DECRYPT_MODE, keys[0], new IvParameterSpec(ivb));
				byte pt[] = algorithm.doFinal(ct);
				return pt;
			}
		}
	}

	@Override
	public CryptoAlgorithm clone() {
		String encProvider = algorithm.getProvider().getName();
		try {
			return new CustomAuthEncryption(algNames[0], algNames[1], encProvider);
		} catch (Exception e) {
			// Can't happen since previous instance would not have been created
			e.printStackTrace();
			System.exit(-3);
		}
		return null;
	}
}
