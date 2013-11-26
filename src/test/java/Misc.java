import static org.junit.Assert.*;

import java.net.InetAddress;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;


public class Misc {
	
	@Test
	public void testIp() throws Exception{
		System.out.println(InetAddress.getLocalHost().getHostAddress());
	}
	
	@Test
	public void testPatternMatching(){
		String a = "avoid_nu3_bers";
		String b = "avoid_nu3_bers*";
		String c = "[abc]*";
		assertTrue(a.matches("[0-9a-zA-Z_]+"));
		assertTrue(!b.matches("[0-9a-zA-Z_]+"));
		assertTrue(b.matches("[0-9a-zA-Z_]+\\*"));
		assertTrue(!c.matches("[0-9a-zA-Z_]+\\*"));
	}
	
	@Test
	public void testAes256() throws Exception {
		Cipher cipher = Cipher.getInstance("AES");
		byte keyb[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] pt = {1,2,3,4,5};
		System.out.println(keyb.length);
		System.out.println(Cipher.getMaxAllowedKeyLength("AES")/8);
		System.out.println(cipher.getIV());
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyb, "AES"));
		byte[] ct = cipher.doFinal(pt);
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(keyb, "AES"));
		byte[] ct2 = cipher.doFinal(pt);
		assertArrayEquals(ct, ct2);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(keyb, "AES"));
		byte[] decpt = cipher.doFinal(ct2);
		assertArrayEquals(pt, decpt);
	}
	
	@Test
	public void testDetHashing() throws Exception{
		Mac cipher = Mac.getInstance("HMACSHA256");
		byte keyb[] = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		byte[] pt = {1,2,3,4,5};
		System.out.println(keyb.length);
		System.out.println(Cipher.getMaxAllowedKeyLength("AES")/8);
		cipher.init(new SecretKeySpec(keyb,"HMACSHA256"));
		byte[] ct = cipher.doFinal(pt);
		cipher.init(new SecretKeySpec(keyb,"HMACSHA256"));
		byte[] ct2 = cipher.doFinal(pt);
		assertArrayEquals(ct, ct2);
		System.out.println("Mac length: "+cipher.getMacLength());
	}
	
	
}

