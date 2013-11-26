package com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.jws.WebMethod;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.JaxWsClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import com.seeburger.research.policyengine.ws.Exception_Exception;
import com.seeburger.research.policyengine.ws.IAccessManagement;
import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.owner.KeyOwner;
//import com.seeburger.research.policyengine.ws.AccessControlWS;
//import com.seeburger.research.policyengine.ws.Exception_Exception;
//import com.seeburger.research.policyengine.ws.IAccessManagement;
//import com.seeburger.research.policyengine.ws.ResourceCoordinates;

public class KeyRequestor implements IKeyRequestor {

	private static String AUTH_TOKEN;
	private static String POLICY_ENDPOINT_ADDRESS;
	private static Key publicKeyPolicyEngine;

	public static PrivateKey sk;
	public static PublicKey pk;
	public static PublicKey policyEnginePK;

	private static final String PUBLIC_KEY_ALG = "RSA";
	private static final String CHARSET = "UTF-8";

	private static String endpoint;

	private static IAccessManagement policyEngineClient;

	public static void setAuthToken(String value) {
		AUTH_TOKEN = value;
		sendActorInfo(endpoint);
	}

	static {
		String hostIP = null;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		int port = 11000;

		// try consecutive ports until one is available

		while (true) {
			try {
				ServerSocket sock = new ServerSocket();
				SocketAddress addr = new InetSocketAddress(hostIP, port);
				// sock.setReuseAddress(true);// can bind different processes to
				// same port !!!
				sock.bind(addr);
				sock.close();
				break;
			} catch (IOException ex) {
				port++;
			}
		}

		JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
		svrFactory.setServiceClass(IKeyRequestor.class);
		endpoint = "http://" + hostIP + ":" + port + "/keyRequestorPort";
		svrFactory.setAddress(endpoint);
		svrFactory.setServiceBean(new KeyRequestor());
		svrFactory.create();
		System.out.println("\n\nKey Requestor running at: " + endpoint);
	}

	public static void setPolicyEndpoint(String address) {
		POLICY_ENDPOINT_ADDRESS = address;
		initializePolicyEngineClient();
	}

	private static void initializePolicyEngineClient() {
		/* try { */
		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(IAccessManagement.class);
		factory.setAddress(POLICY_ENDPOINT_ADDRESS);
		policyEngineClient = (IAccessManagement) factory.create();
		Client c = JaxWsClientProxy.getClient(policyEngineClient);
		HTTPConduit conduit = (HTTPConduit) c.getConduit();
		conduit.getClient().setReceiveTimeout(0);
		/*
		 * IAccessManagementService service = new IAccessManagementService( new
		 * URL(POLICY_ENDPOINT_ADDRESS + "?wsdl"));
		 * 
		 * policyEngineClient = service.getPort(IAccessManagement.class);
		 */
		/*
		 * } catch (MalformedURLException e) { throw new RuntimeException(e); }
		 */
	}

	private static void sendActorInfo(String address) {
		// read the PK of Policy Engine from "policyEngine.cer"
		try {
			CertificateFactory certFactory = CertificateFactory
					.getInstance(KeyManagementLayer.CERT_TYPE);
			InputStream certInputStream = KeyRequestor.class
					.getResourceAsStream("/policyEngine.cer");
			Certificate peCert = certFactory
					.generateCertificate(certInputStream);
			policyEnginePK = peCert.getPublicKey();
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
		byte[] enc_authToken = pkeEncrypt(AUTH_TOKEN);
		byte[] enc_endpoint = pkeEncrypt(endpoint + "#" + KeyOwner.endpoint);
		policyEngineClient.storeActorAddress(enc_authToken, enc_endpoint);
	}

	private static byte[] pkeEncrypt(String data) {
		try {
			Cipher rsa = Cipher.getInstance(PUBLIC_KEY_ALG);
			rsa.init(Cipher.ENCRYPT_MODE, policyEnginePK);
			return rsa.doFinal(data.getBytes(CHARSET));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@WebMethod
	public void storeKeyPackage(KeySharingPackage keyPackage) {
		String requestId = new String(decrypt(keyPackage.getCorrelationId()));
		KeyExtractor extractor = listenersForRequest.remove(requestId);
		if (extractor == null) {// hm, a response to a non-existent request
			// TODO: [ideally] must escalate this as a business/security error.
			// Probably a hacking attempt.
			System.err.println("WARNING: Unknown requestID: " + requestId);
			return;
		} else {
			List<KeyCoordinate> keyCoordinates = keyPackage.getKeys();
			for (KeyCoordinate kCoord : keyCoordinates){
				kCoord.key1 = kCoord.key1 != null ? decrypt(kCoord.key1) : null;
				kCoord.key2 = kCoord.key2 != null ? decrypt(kCoord.key2) : null;
			}
			extractor.setKeyPackage(keyPackage);
			Object lock = extractorLocks.remove(requestId);
			synchronized (lock) {
				lock.notifyAll();
			}
			return;
		}
	}

	// "internal API"

	private static Map<String, KeyExtractor> listenersForRequest = Collections
			.synchronizedMap(new HashMap<String, KeyExtractor>());
	private static Map<String, Object> extractorLocks = Collections
			.synchronizedMap(new HashMap<String, Object>());

	public synchronized static void registerRequest(String requestId,
			KeyExtractor extractor, ResourceCoordinates resourceCoordinates) {
		Object lock = new Object();
		extractorLocks.put(requestId, lock);
		extractor.setWaitLock(lock);
		listenersForRequest.put(requestId, extractor);
		try {
			policyEngineClient.requestKeys(pkeEncrypt(AUTH_TOKEN),
					pkeEncrypt(requestId), resourceCoordinates);
		} catch (Exception_Exception e) {
			// Authentication Error is not supposed to happen
			throw new RuntimeException(e);
		}
	}

	private byte[] decrypt(byte[] ct) {
		try {
			Cipher rsa = Cipher.getInstance(PUBLIC_KEY_ALG);
			rsa.init(Cipher.DECRYPT_MODE, sk);
			return rsa.doFinal(ct);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
