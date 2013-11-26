import java.security.Key;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.GCMBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import perf.ObjectSizeFetcher;

public class CryptoPerformance {

	private final String ALG_NAME = "AES/GCM/NoPadding";
	private final String fromProvider = "BC";

	@BeforeClass
	public static void addProvider() {
		Provider bc = new BouncyCastleProvider();
		Security.insertProviderAt(bc, 2);
	}

	@Test
	public void cipherCreationOverhead() throws Exception {
		Object obj = new Object();
		Key key = KeyGenerator.getInstance("AES").generateKey();
		// System.out.println("Key size="+key.getEncoded().length);
		key = new SecretKeySpec(Arrays.copyOf(key.getEncoded(), 16), ALG_NAME);
		byte[] ivb = Arrays.copyOf(key.getEncoded(), 8);
		IvParameterSpec ivspec = new IvParameterSpec(ivb);
		long objSpace = ObjectSizeFetcher.getObjectSize(obj);
		obj.hashCode();// ensure not GCed before measurement

		Map<Integer, long[]> runtimes = new TreeMap<Integer, long[]>();
		List<Cipher> cipherObjects = new ArrayList<Cipher>(1000);
		for (int i = 1; i <= 1000; i++) {
			long t1, t2, t3, t4;
			t1 = System.nanoTime();
			Cipher algorithm = Cipher.getInstance(ALG_NAME, fromProvider);
			t2 = System.nanoTime();
			algorithm.init(Cipher.ENCRYPT_MODE, key);
			t3 = System.nanoTime();
			algorithm.init(Cipher.DECRYPT_MODE, key, ivspec);
			t4 = System.nanoTime();
			long newInstanceTime = t2 - t1;
			long initEnc = t3 - t2;
			long initDec = t4 - t3;
			long cipherSpace = ObjectSizeFetcher.getObjectSize(algorithm);
			algorithm.hashCode();// ensure not GCed before measurement
			runtimes.put(i, new long[] { newInstanceTime / 1000,
					initEnc / 1000, initDec / 1000, cipherSpace });
			cipherObjects.add(algorithm);
		}

		System.out.println("Plain Java Object space overhead=" + objSpace);
		System.out.println("Cipher init time, initEnc, initDec, Cipher size");
		Cipher prev = null;
		for (Integer run : runtimes.keySet()) {
			System.out.print("Run #" + run + " : "
					+ Arrays.toString(runtimes.get(run)));
			System.out.println("--" + cipherObjects.get(run).hashCode()
					+ "equals prev=" + (prev == cipherObjects.get(run)));
			prev = cipherObjects.get(run);
		}
	}

	@Test
	public void testForThesis_usageOfAES() throws Exception {

		byte[] plainText = "SecretMessage".getBytes("UTF-8");
		byte[] IV = { 0x2a, 0x4a, 0x56, 0x6b, 0x37, 0x6a, 0x6e, 0x27, 0x00,
				0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
		Key aesKey = KeyGenerator.getInstance("AES").generateKey();
		Cipher aesCipher = Cipher.getInstance("AES/CTR/PKCS5Padding", "SunJCE");
		aesCipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(IV));
		byte[] cipherText = aesCipher.doFinal(plainText);
		aesCipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(IV));
		byte[] decryptedCipherText = aesCipher.doFinal(cipherText);
		Assert.assertArrayEquals(plainText, decryptedCipherText);
	}

	@Test
	public void testForThesis_determinismOfSHA2() throws Exception {
		// HMAC doesn't use IVs, i.e. it is deterministic
		byte[] plainText = "SecretMessage".getBytes("UTF-8");
		Key sha256Key = KeyGenerator.getInstance("HMACSHA256").generateKey();
		Mac sha = Mac.getInstance("HMACSHA256", "SunJCE");
		sha.init(sha256Key);
		byte[] tag = sha.doFinal(plainText);
		sha = Mac.getInstance("HMACSHA256", "SunJCE");
		sha.init(sha256Key);
		byte[] tag2 = sha.doFinal(plainText);
		Assert.assertArrayEquals(tag, tag2);
	}

	@Test
	public void testForThesis_compareTimes() throws Exception {
		final int _20MEGABYTE = 20 * 1024 * 1024;
		byte[] plainText = new byte[_20MEGABYTE];
		Key sha256Key = KeyGenerator.getInstance("HMACSHA256").generateKey();
		Mac sha = Mac.getInstance("HMACSHA256", "SunJCE");
		sha.init(sha256Key);
		long start = System.nanoTime();
		sha.doFinal(plainText);
		// for (int i = 0; i < _1MEGABYTE/256; i+= 256){
		// sha.update(plainText, i, i+256);
		// }
		long end = System.nanoTime();
		long shaTime = end - start;

		Cipher aes = Cipher.getInstance("AES/CTR/PKCS5Padding", "SunJCE");
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256);
		Key aes256Key = keyGen.generateKey();
		aes.init(Cipher.ENCRYPT_MODE, aes256Key);
		start = System.nanoTime();
		aes.doFinal(plainText);
		end = System.nanoTime();
		long aesTime = end - start;
		System.out.println("HMAC-SHA256 did the job in: " + shaTime + " ns");
		System.out.println("AES256 did the job in: " + aesTime + " ns");
		System.out.println("HMAC is " + (1.0 * aesTime / shaTime)
				+ " times faster");
	}

	@Test
	public void testInitializationPerformance() throws Exception {
		// plain text variant
		byte[] THEkey = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };// 256 bit
		Key singleKey = new SecretKeySpec(THEkey, "AES");
		Key[] manyKeys = new Key[1000000];

		for (int i = 0; i < manyKeys.length; i++) {
			byte[] k = new byte[32];
			k[0] = (byte) (i % 256);
			k[1] = (byte) ((i >> 8) % 256);
			k[2] = (byte) ((i >> 16) % 256);
			manyKeys[i] = new SecretKeySpec(k, "AES");
		}

		byte[] aDouble = { 0, 1, 2, 3, 4, 5, 6, 7 };

		long s, e;
		Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		IvParameterSpec iv = new IvParameterSpec(new byte[16]);
		
		byte[] ct;
		
		// "Warming" up the system a bit ...
		for (int i = 0; i < manyKeys.length; i++)
			c.init(Cipher.ENCRYPT_MODE, singleKey);

		c = Cipher.getInstance("AES/CBC/PKCS5Padding");
		s = System.nanoTime();
		for (int i = 0; i < manyKeys.length; i++){
			c.init(Cipher.ENCRYPT_MODE, singleKey);
			ct = c.doFinal(aDouble);
		}
		e = System.nanoTime();
		System.out.println("Using same key to initialize the cipher "
				+ manyKeys.length + " times = " + (e - s) / 1000000 + " ms");

		c = Cipher.getInstance("AES/CBC/PKCS5Padding");

		s = System.nanoTime();
		for (int i = 0; i < manyKeys.length; i++){
			c.init(Cipher.ENCRYPT_MODE, manyKeys[i]);
			ct = c.doFinal(aDouble);
		}
		e = System.nanoTime();
		System.out.println("Using different keys to initialize the cipher "
				+ manyKeys.length + " times = " + (e - s) / 1000000 + " ms");

		c = Cipher.getInstance("AES/CBC/PKCS5Padding");

		s = System.nanoTime();
		for (int i = 0; i < manyKeys.length; i++){
			c.init(Cipher.ENCRYPT_MODE, manyKeys[i], iv);
			ct = c.doFinal(aDouble);
		}
		e = System.nanoTime();
		System.out
				.println("Using same key with same IV to initialize the cipher "
						+ manyKeys.length
						+ " times = "
						+ (e - s)
						/ 1000000
						+ " ms");

		c = Cipher.getInstance("AES/CBC/PKCS5Padding");

		s = System.nanoTime();
		for (int i = 0; i < manyKeys.length; i++) {
			c.init(Cipher.ENCRYPT_MODE, manyKeys[i], iv);
			ct = c.doFinal(aDouble);
		}
		e = System.nanoTime();
		System.out
				.println("Using different keys with same IV to initialize the cipher "
						+ manyKeys.length
						+ " times = "
						+ (e - s)
						/ 1000000
						+ " ms");

		c = Cipher.getInstance("AES/CBC/PKCS5Padding");

		s = System.nanoTime();
		for (int i = 0; i < manyKeys.length; i++){
			c.init(Cipher.ENCRYPT_MODE, manyKeys[i], new IvParameterSpec(Arrays.copyOf(manyKeys[i].getEncoded(), 16)));
			ct = c.doFinal(aDouble);
		}
		e = System.nanoTime();
		System.out.println("Using different keys with different IVs to initialize the cipher "
				+ manyKeys.length + " times = " + (e - s) / 1000000 + " ms");
		
		// Conclusion: it doesn't matter much if same or different keys are used
		// There is a small performance hit when no IV is supplied (it seems to be generated internally anyway ...)

	}
}
