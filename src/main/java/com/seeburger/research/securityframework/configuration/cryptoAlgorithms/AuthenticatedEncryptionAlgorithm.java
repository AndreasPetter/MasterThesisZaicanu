package com.seeburger.research.securityframework.configuration.cryptoAlgorithms;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;

/**
 * Allows thread-safe parallel encryption and decryption. Some synchronization overhead
 * is incurred to ensure thread safety.
 * 
 * @author Eugen
 *
 */
public class AuthenticatedEncryptionAlgorithm extends CryptoAlgorithm {

	private final Cipher enc_algorithm;
	private final Cipher dec_algorithm;
	private final Object encLock = new Object();
	private final Object decLock = new Object();
	private Key cachedDecKey = new SecretKeySpec(new byte[16], "AES");
	private final byte[] fixedIV = new byte[16];

	public AuthenticatedEncryptionAlgorithm(String ALG_NAME)
			throws NoSuchAlgorithmException, NoSuchPaddingException {
		super(ALG_NAME);
		enc_algorithm = Cipher.getInstance(ALG_NAME);
		dec_algorithm = Cipher.getInstance(ALG_NAME);
	}

	public AuthenticatedEncryptionAlgorithm(String ALG_NAME, String fromProvider)
			throws NoSuchAlgorithmException, NoSuchProviderException,
			NoSuchPaddingException {
		super(ALG_NAME);
		enc_algorithm = Cipher.getInstance(ALG_NAME, fromProvider);
		dec_algorithm = Cipher.getInstance(ALG_NAME, fromProvider);
	}

	@Override
	public byte[] process(byte[] plainTextb, Key... keys) throws Exception {
		byte[] ivb = null;
		byte[] cipherTextb = null;
		byte[] c = null;
		synchronized (encLock) {
			enc_algorithm.init(Cipher.ENCRYPT_MODE, keys[0], new IvParameterSpec(fixedIV));
			ivb = enc_algorithm.getIV();
			c = enc_algorithm.doFinal(plainTextb);
			cipherTextb = new byte[ivb.length + c.length];
		}
		System.arraycopy(c, 0, cipherTextb, ivb.length, c.length);
		System.arraycopy(ivb, 0, cipherTextb, 0, ivb.length);
		return cipherTextb;
	}

	@Override
	public byte[] recover(byte[] cipherTextb, Key... keys) throws Exception {
		int keyLen = keys[0].getEncoded().length;
		synchronized (decLock) {
			if (!cachedDecKey.equals(keys[0])){
			dec_algorithm.init(
					Cipher.DECRYPT_MODE,
					keys[0],
					new IvParameterSpec(Arrays.copyOfRange(cipherTextb, 0,
							keyLen / 2)));
			cachedDecKey = keys[0];
			}
/*			else{
				System.out.println("Cache hit");
			}*/
			return dec_algorithm.doFinal(Arrays.copyOfRange(cipherTextb,
					keyLen / 2, cipherTextb.length));
		}
	}

	@Override
	public CryptoAlgorithm clone() {
		String provider = enc_algorithm.getProvider().getName();
		try {
			return new AuthenticatedEncryptionAlgorithm(algNames[0], provider);
		} catch (Exception e) {
			// Can't happen since previous instance would not have been created
			e.printStackTrace();
			System.exit(-3);
		}
		return null;
	}
}
