package certificatestuff;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.bouncycastle.util.Arrays;
import org.junit.Test;

import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;

public class SignedCertificateCheck {
	@Test
	public void testPKs() throws Exception{
		InputStream certStream = SignedCertificateCheck.class.getResourceAsStream("/certs/policyEngine.cer");
		InputStream dsoKeystore = SignedCertificateCheck.class.getResourceAsStream("/certs/smkKeystore");
		
		Certificate peCert = CertificateFactory.getInstance(KeyManagementLayer.CERT_TYPE).generateCertificate(certStream);		
		byte[] pk_peb = peCert.getPublicKey().getEncoded();
		KeyStore ks = KeyStore.getInstance(KeyManagementLayer.KEYSTORE_TYPE);
		ks.load(dsoKeystore, new char[]{});
		X509Certificate dsoCert = (X509Certificate) ks.getCertificate(KeyManagementLayer.KS_ALIAS_CERT);
		byte[] pk_dsob = dsoCert.getPublicKey().getEncoded();
		try{
			assertFalse(Arrays.areEqual(pk_peb, pk_dsob));
			dsoCert.verify(peCert.getPublicKey());
		}
		catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
}
