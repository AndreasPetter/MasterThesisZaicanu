import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.Cipher;

import static org.junit.Assert.*;

import org.junit.Test;

public class PKETest {
	@Test
	public void testPKE() throws Exception{
		byte[] pt = "dso".getBytes();
		KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
		//kpGen.initialize();
		KeyPair kp = kpGen.generateKeyPair();
		Cipher rsaCipher = Cipher.getInstance("RSA");
		PublicKey publicKey = kp.getPublic();
		System.out.println("Public Key Info:\n" +
				"size="+publicKey.getEncoded().length+"\n" +
						"bytes="+Arrays.toString(publicKey.getEncoded()));
		PrivateKey privateKey = kp.getPrivate();
		System.out.println("Private Key Info:\n" +
				"size="+privateKey.getEncoded().length+"\n" +
						"bytes="+Arrays.toString(privateKey.getEncoded()));
		rsaCipher.init(Cipher.ENCRYPT_MODE, publicKey);
		byte ct[] = rsaCipher.doFinal(pt);
		rsaCipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] pt1 = rsaCipher.doFinal(ct);
		assertArrayEquals(pt, pt1);
	}
}
