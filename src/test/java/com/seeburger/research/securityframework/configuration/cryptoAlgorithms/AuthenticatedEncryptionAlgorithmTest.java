package com.seeburger.research.securityframework.configuration.cryptoAlgorithms;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;
import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class AuthenticatedEncryptionAlgorithmTest {

	@BeforeClass
	public static void addBouncyCastle() {
		Provider p = new BouncyCastleProvider();
		Security.insertProviderAt(p, 2);
	}

	@Test
	public void testEncryption() throws Exception {
		AuthenticatedEncryptionAlgorithm crypto = new AuthenticatedEncryptionAlgorithm(
				"AES/GCM/NoPadding", "BC");
		byte pt[] = { 1, 2, 3, 4, 5 };
		byte[] enckey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		Key[] keys = new Key[] { new SecretKeySpec(enckey, "AES/GCM/NoPadding") };
		byte[] ct = crypto.process(pt, keys);
		byte[] newpt = crypto.recover(ct, keys);
		assertArrayEquals(pt, newpt);
		ct[18] = (byte) (ct[18] ^ 0xFF);// if index < 15 then the IV is modified
										// however if the key is the same it is
										// reused !
		try {
			crypto.recover(ct, keys);
		} catch (BadPaddingException ex) {
			return;
		} catch (Exception ex) {
			ex.printStackTrace();
			return;
		}
		fail("Mac check failed !");
	}

	@Test
	public void testEncryptionOfADouble() throws Exception {
		AuthenticatedEncryptionAlgorithm crypto = new AuthenticatedEncryptionAlgorithm(
				"AES/GCM/NoPadding", "BC");
		byte pt[] = new byte[8];
		ByteBuffer.wrap(pt).putDouble(13.13);
		byte[] enckey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit key
		Key[] keys = new Key[] { new SecretKeySpec(enckey, "AES/GCM/NoPadding") };
		byte[] ct = crypto.process(pt, keys);
		byte[] iv = Arrays.copyOfRange(ct, 0, enckey.length / 2);
		System.out.println(Arrays.toString(iv));
		System.out.println("Size of ciphertext=" + ct.length);
		byte[] newpt = crypto.recover(ct, keys);
		// test if caching works correctly
		newpt = crypto.recover(ct, keys);
		assertArrayEquals(pt, newpt);
		System.out.println(ByteBuffer.wrap(newpt).getDouble());
	}

	@Test
	public void testAESSymmetry() throws Exception {
		Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");
		byte[] enckey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit key
		byte[] ivb = Arrays.copyOfRange(enckey, 0, enckey.length / 2);
		Key aesKey = new SecretKeySpec(enckey, "AES");
		byte[] pt = new byte[1024 * 1024 * 10];// 10 MB of pt
		long s = System.nanoTime();
		c.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(ivb));
		byte[] ct = c.doFinal(pt);
		long e = System.nanoTime();
		System.out.println("Encrypt " + pt.length / 1024 / 1024 + " MB: "
				+ (e - s) + " ns");
		s = System.nanoTime();
		c.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivb));
		byte[] newpt = c.doFinal(ct);
		e = System.nanoTime();
		System.out.println("Decrypt " + pt.length / 1024 / 1024 + " MB: "
				+ (e - s) + " ns");
		// assertArrayEquals(pt, newpt);
	}

	@Ignore
	@Test
	public void testAESReuse() throws Exception {
		Cipher c = Cipher.getInstance("AES/GCM/NoPadding", "BC");
		byte[] enckey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit key
		byte[] ivb = Arrays.copyOfRange(enckey, 0, enckey.length / 2);
		Key aesKey = new SecretKeySpec(enckey, "AES");
		byte[] pt = new byte[1024 * 1024 * 10];// 10 MB of pt
		c.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(ivb));
		byte[] ct = c.doFinal(pt);
		final int iters = 100;
		System.out.println("Press a key and then enter:");
		System.in.read();
		long s, e;
		/*
		 * // Test no reuse s = System.nanoTime(); for (int i = 1; i <= iters;
		 * i++){ pt[4] = (byte)i; ct = c.doFinal(pt); } e = System.nanoTime();
		 * System
		 * .out.println("Encrypt "+pt.length/1024/1024+" MB: "+(e-s)/1000000
		 * +" ms -- "+iters+" times with reuse");
		 */

		// Decrypt to see if idea is correct
		/*
		 * s = System.nanoTime(); c.init(Cipher.DECRYPT_MODE, aesKey, new
		 * IvParameterSpec(ivb)); byte[] newpt = c.doFinal(ct); e =
		 * System.nanoTime();
		 * System.out.println("Decrypt "+pt.length/1024/1024+" MB: "
		 * +(e-s)/1000000+" ms -- once"); assertEquals(iters, newpt[4]);
		 */

		// Generate new keys
		c.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(ivb));
		byte[] ct2 = c.doFinal(pt);
		Key aesKeys[] = new Key[iters];
		for (int i = 0; i < iters; i++) {
			enckey[0] = (byte) i;
			aesKeys[i] = new SecretKeySpec(enckey, "AES");
		}

		// Try encryption with re-initialization and different keys
		s = System.nanoTime();
		for (int i = 1; i <= iters; i++) {
			pt[4] = (byte) i;
			c.init(Cipher.ENCRYPT_MODE, aesKeys[i - 1],
					new IvParameterSpec(ivb));
			ct = c.doFinal(pt);
		}
		e = System.nanoTime();
		System.out.println("Encrypt " + pt.length / 1024 / 1024 + " MB: "
				+ (e - s) / 1000000 + " ms -- " + iters + " times no reuse");
		// assertArrayEquals(pt, newpt);
	}

	@Ignore
	@Test
	public void testDecryptionOverhead() throws Exception {
		byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit
		Key encKey = new SecretKeySpec(key, "AES");
		Key[] algKeys = new Key[] { encKey };

		AuthenticatedEncryptionAlgorithm alg = new AuthenticatedEncryptionAlgorithm(
				"AES/GCM/NoPadding", "BC");
		measureOverhead(alg, algKeys);

		algKeys = new Key[] { encKey, new SecretKeySpec(key, "SHA256") };
		CustomAuthEncryption alg2 = new CustomAuthEncryption("BC",
				"AES/CTR/PKCS5PADDING", "HMACSHA256");
		measureOverhead(alg2, algKeys);

	}

	private void measureOverhead(CryptoAlgorithm alg, Key[] algKeys)
			throws Exception {

		final int n_MB = 100;
		byte[] pt = new byte[1024 * 1024 * n_MB];
		long s, e;

		byte[] ct = alg.process(pt, algKeys);
		System.out.println("Decrypting...");
		byte[] charbuff = new byte[3];
		System.in.read(charbuff);
		s = System.nanoTime();
		byte[] newpt = alg.recover(ct, algKeys);
		e = System.nanoTime();
		System.out.println("Decrypting " + n_MB + " MB took: " + (e - s)
				/ 1000000.0 + " ms");

		// Encrypt by parts, decrypt by parts - same key.
		AuthenticatedEncryptionAlgorithm alg2 = new AuthenticatedEncryptionAlgorithm(
				"AES/GCM/NoPadding", "BC");
		byte[][] ptFragments = new byte[pt.length / 64][];
		for (int i = 0; i < ptFragments.length; i++) {
			ptFragments[i] = new byte[64];
		}

		byte[][] ctFragments = new byte[pt.length / 64][];
		for (int i = 0; i < ptFragments.length; i++) {
			ctFragments[i] = alg2.process(ptFragments[i], algKeys);
		}
		System.out.println("Decrypting by fragments...");
		System.in.read(charbuff);
		s = System.nanoTime();
		for (int i = 0; i < ctFragments.length; i++) {
			ptFragments[i] = alg2.recover(ctFragments[i], algKeys);
		}
		e = System.nanoTime();
		System.out.println("Decrypting " + n_MB
				+ " MB in 64 B fragments took: " + (e - s) / 1000000.0 + " ms");
	}

	@Ignore
	@Test
	public void testTheoryPerformance() throws Exception {
		// plain text variant
		byte[] key = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit
		byte[] aDouble = { 0, 1, 2, 3, 4, 5, 6, 7 };
		final int nConsumers = 10;
		final int nCols = 89280;
		Key[] keys = new Key[nConsumers];

		for (int i = 0; i < nConsumers; i++) {
			key[1] = (byte) i;
			keys[i] = new SecretKeySpec(Arrays.copyOf(key, key.length), "AES");
		}

		byte[][][] dataset = new byte[nConsumers][][];

		for (int i = 0; i < nConsumers; i++) {
			dataset[i] = new byte[nCols][];
			for (int j = 0; j < nCols; j++) {
				dataset[i][j] = Arrays.copyOf(aDouble, 8);
			}
		}

		byte[][][] ct = new byte[nConsumers][][];
		for (int i = 0; i < nConsumers; i++) {
			ct[i] = new byte[nCols][];
		}

		System.out.println("Starting the encryption part .. ");
		AuthenticatedEncryptionAlgorithm alg = new AuthenticatedEncryptionAlgorithm(
				"AES/GCM/NoPadding", "BC");
		for (int i = 0; i < nConsumers; i++) {
			for (int j = 0; j < nCols; j++) {
				ct[i][j] = alg.process(dataset[i][j], keys[i]);
			}
		}

		long s, e;
		System.out.println("Reached the decryption stage. Press a key...");
		System.in.read();
		s = System.nanoTime();
		for (int i = 0; i < nConsumers; i++) {
			for (int j = 0; j < nCols; j++) {
				dataset[i][j] = alg.recover(ct[i][j], keys[i]);
			}
		}
		e = System.nanoTime();
		System.out.println("Decryption took: " + (e - s) / 1000000000.0
				+ " seconds");
		for (int i = 0; i < nConsumers; i++) {
			for (int j = 0; j < nCols; j++) {
				assertArrayEquals(aDouble, dataset[i][j]);
			}
		}
	}

	@Ignore
	@Test
	public void testAESKeyExpansionProblem() throws Exception {
		Cipher c = Cipher.getInstance("AES", "SunJCE");
		byte[] theKEY = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
				16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31 };
		Key aesKey = new SecretKeySpec(theKEY, "AES");

		byte[] threechars = new byte[3];
		byte[] aDouble = { 2, 4, 6, 8, 10, 12, 14, 16 };

		c.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] ct = c.doFinal(aDouble);
		byte[] pt = null;
		System.out.println("Decryption phase. Press a key..");
		System.in.read(threechars);
		long s, e;
		s = System.nanoTime();
		for (int i = 1; i <= 15000000; i++) {
			c.init(Cipher.DECRYPT_MODE, aesKey);
			pt = c.doFinal(ct);
		}
		e = System.nanoTime();
		System.out.println("Without Reusing Initialized Cipher : " + (e - s)
				/ 1000000000.0 + " sec");
		assertArrayEquals(aDouble, pt);
		/*
		 * s = System.nanoTime(); c.init(Cipher.DECRYPT_MODE, aesKey); for (int
		 * i = 1; i <= 50000000; i++){ pt = c.doFinal(ct); } e =
		 * System.nanoTime();
		 * System.out.println("With Reusing Initialized Cipher : "
		 * +(e-s)/1000000000.0+" sec"); assertArrayEquals(aDouble, pt);
		 */
	}

}
