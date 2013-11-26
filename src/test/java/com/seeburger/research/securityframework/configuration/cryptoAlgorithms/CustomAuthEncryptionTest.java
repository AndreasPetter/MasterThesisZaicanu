package com.seeburger.research.securityframework.configuration.cryptoAlgorithms;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.security.Key;
import java.security.Provider;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.BeforeClass;
import org.junit.Test;

public class CustomAuthEncryptionTest {
	
	@BeforeClass
	public static void addBouncyCastle(){
		Provider p = new BouncyCastleProvider();
		Security.insertProviderAt(p, 2);
	}

	@Test
	public void testDeterministicEncryption() throws Exception{
		CustomAuthEncryption crypto = new CustomAuthEncryption("BC", "AES", "HMACSHA256");
		byte pt[] = {1,2,3,4,5};
		byte[] enckey = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] hashkey = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		Key[] keys = new Key[]{new SecretKeySpec(enckey, "AES"), new SecretKeySpec(hashkey, "HMACSHA256")};
		byte[] ct = crypto.process(pt, keys);
		byte[] newpt = crypto.recover(ct, keys);
		assertArrayEquals(pt, newpt);
		ct[3] = (byte) (ct[3]^0xFF);
		try{
			crypto.recover(ct, keys);
		}catch(BadPaddingException ex){
			return;
		}
		fail("Mac check failed !");
	}
	
	@Test
	public void testRandomizedEncryption() throws Exception{
		CustomAuthEncryption crypto = new CustomAuthEncryption("BC", "AES/CTR/PKCS5PADDING", "HMACSHA256");
		byte pt[] = {1,2,3,4,5};
		byte[] enckey = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] hashkey = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
		Key[] keys = new Key[]{new SecretKeySpec(enckey, "AES/CTR/PKCS5PADDING"), new SecretKeySpec(hashkey, "HMACSHA256")};
		byte[] ct = crypto.process(pt, keys);
		byte[] newpt = crypto.recover(ct, keys);
		assertArrayEquals(pt, newpt);
		ct[3] = (byte) (ct[3]^0xFF);
		try{
			crypto.recover(ct, keys);
		}catch(BadPaddingException ex){
			return;
		}
		fail("Mac check failed !");
	}
}
