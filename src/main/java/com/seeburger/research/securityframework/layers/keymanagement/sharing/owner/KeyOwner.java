package com.seeburger.research.securityframework.layers.keymanagement.sharing.owner;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.Key;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.jws.WebService;

import me.prettyprint.hector.api.Serializer;

import org.apache.cxf.jaxws.JaxWsServerFactoryBean;

import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.IKeyRequestor;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.IKeyRequestorService;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyCoordinate;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeySharingPackage;

@WebService(name = "IKeySharingOwnerServiceService")
public class KeyOwner implements IKeySharingOwnerService {

	private final String CERT_TYPE = "X.509";
	private final String PUBLIC_KEY_ALG = "RSA";
	private ExecutorService packageSendingPool = Executors
			.newFixedThreadPool(8);

	public static final String endpoint = initializeEndpoint();

	private static Map<String, Serializer> serializerCache = Collections
			.synchronizedMap(new HashMap<String, Serializer>(4));

	private static String initializeEndpoint() {
		String hostIP = null;
		try {
			hostIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		int port = 11001;

		// try consecutive ports until one is available

		while (true) {
			try {
				ServerSocket sock = new ServerSocket();
				SocketAddress addr = new InetSocketAddress(hostIP, port);
				// sock.setReuseAddress(true);
				sock.bind(addr);
				sock.close();
				break;
			} catch (IOException ex) {
				port++;
			}
		}

		JaxWsServerFactoryBean svrFactory = new JaxWsServerFactoryBean();
		svrFactory.setServiceClass(IKeySharingOwnerService.class);
		String address = "http://" + hostIP + ":" + port + "/keyOwnerPort";
		svrFactory.setAddress(address);
		svrFactory.setServiceBean(new KeyOwner());
		svrFactory.create();
		System.out.println("\n\nKey Owner running at: " + address);
		return address;
	}

	public void shareKeys(byte[] correlationId,
			final ResourceCoordinates resourceCoordinates, byte[] receiverCertificate) {
		byte[] rowIdBytes = resourceCoordinates.getRowid();
		Object rowIdObject = null;
		if (rowIdBytes != null) {
			String serializerClassName = resourceCoordinates
					.getRowidSerializerClass();
			Serializer rowIdSer = serializerCache.get(serializerClassName);
			if (rowIdSer == null) {
				try {
					Class serializerClass = getClass().forName(
							serializerClassName);
					Serializer ser = (Serializer) serializerClass.getMethod(
							"get", null).invoke(null, null);
					rowIdSer = ser;
					serializerCache.put(serializerClassName, rowIdSer);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			rowIdObject = rowIdSer.fromBytes(rowIdBytes);
		}
		byte[] columnNameBytes = resourceCoordinates.getColumnName();
		Object columnNameObject = null;
		if (columnNameBytes != null) {
			String serializerClassName = resourceCoordinates
					.getColumnNameSerializerClass();
			Serializer columnNameSer = serializerCache.get(serializerClassName);
			if (columnNameSer == null) {
				try {
					Class serializerClass = getClass().forName(
							serializerClassName);
					Serializer ser = (Serializer) serializerClass.getMethod(
							"get", null).invoke(null, null);
					columnNameSer = ser;
					serializerCache.put(serializerClassName, columnNameSer);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
			columnNameObject = columnNameSer.fromBytes(columnNameBytes);
		}
		Key[] keys = KeyManagementLayer.getKeys(
				resourceCoordinates.getKeyspace(),
				resourceCoordinates.getCfName(), columnNameObject, rowIdObject);
		final KeySharingPackage keyPackage = new KeySharingPackage();
		keyPackage.setKeyspaceName(resourceCoordinates.keyspace);
		keyPackage.setColumnFamilyName(resourceCoordinates.cfName);
		keyPackage.setCorrelationId(correlationId);
		KeyCoordinate keyWithCoords = new KeyCoordinate();
		keyWithCoords.setRowid(resourceCoordinates.getRowid());
		keyWithCoords.setRowSerializerClass(resourceCoordinates
				.getRowidSerializerClass());
		keyWithCoords.setColumnName(resourceCoordinates.getColumnName());
		keyWithCoords.setColumnNameSerializerClass(resourceCoordinates
				.getColumnNameSerializerClass());
		Certificate recCert = null;
		try {
			recCert = CertificateFactory.getInstance(CERT_TYPE)
					.generateCertificate(
							new ByteArrayInputStream(receiverCertificate));
		} catch (CertificateException e) {
			throw new RuntimeException(e);
		}
		if (keys.length == 1) {
			keyWithCoords.setKey1(encryptKey(keys[0], recCert));
		} else if (keys.length == 2) {
			keyWithCoords.setKey1(encryptKey(keys[0], recCert));
			keyWithCoords.setKey2(encryptKey(keys[1], recCert));
		}
		List<KeyCoordinate> _keys = keyPackage.getKeys();
		_keys.add(keyWithCoords);
		packageSendingPool.submit(new Runnable() {
			public void run() {
				sendKeySharingPackage(keyPackage,
						resourceCoordinates.getRequestorEndpoint());
			}
		});
	}

	private void sendKeySharingPackage(KeySharingPackage preparedPackage,
			String receiverEndpoint) {
		try {
			IKeyRequestorService service = new IKeyRequestorService(new URL(
					receiverEndpoint + "?wsdl"));
			IKeyRequestor port = service.getPort(IKeyRequestor.class);
			port.storeKeyPackage(preparedPackage);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	private byte[] encryptKey(Key key, Certificate receiverCertificate) {
		try {
			Cipher rsa = Cipher.getInstance(PUBLIC_KEY_ALG);
			rsa.init(Cipher.ENCRYPT_MODE, receiverCertificate);
			return rsa.doFinal(key.getEncoded());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
