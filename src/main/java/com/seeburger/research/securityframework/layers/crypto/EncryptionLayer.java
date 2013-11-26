package com.seeburger.research.securityframework.layers.crypto;

import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.seeburger.research.securityframework.configuration.KeyspaceConfiguration;
import com.seeburger.research.securityframework.configuration.cryptoAlgorithms.CryptoFactory;
import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.layers.crypto.datastructures.ColumnFamilyCoordinate;
import com.seeburger.research.securityframework.layers.crypto.datastructures.KeyspaceCoordinate;
import com.seeburger.research.securityframework.layers.crypto.datastructures.UniversalCoordinate;

public class EncryptionLayer {

	private static Map<String, KeyspaceCoordinate> keyspaceConfiguration = new HashMap<String, KeyspaceCoordinate>();

	public static void addProvider(String id, String className, int prefOrder)
			throws Exception {
		Class<Provider> providerClass = (Class<Provider>) Class
				.forName(className);
		Provider provider = providerClass.newInstance();
		// set as the preferred provider. Positions start from '1'.
		Security.insertProviderAt(provider, prefOrder);
	}

	public static void processKeyspaceConfiguration(
			KeyspaceConfiguration.EncryptionLayerConf keyspaceConfiguration)
			throws Exception {
		String keyspace = keyspaceConfiguration.getKeyspaceName();
		CryptoAlgorithm cryptoAlg = keyspaceConfiguration.getCryptoAlgorithm();
		KeyspaceCoordinate keyspaceCoord = new KeyspaceCoordinate(keyspace,
				cryptoAlg);
		for (String columnFamily : keyspaceConfiguration.getColumnFamilyNames()) {
			CryptoAlgorithm crypto = keyspaceConfiguration
					.getCryptoAlgorithmForCF(columnFamily, cryptoAlg);
			ColumnFamilyCoordinate cfCoord = new ColumnFamilyCoordinate(
					columnFamily, crypto);
			// columns as separate coordinates are not yet supported.
			cfCoord.addCoordinate(new UniversalCoordinate(crypto.clone()));
			keyspaceCoord.addCoordinate(cfCoord);
		}
		EncryptionLayer.keyspaceConfiguration.put(keyspace, keyspaceCoord);
	}

	public static CryptoAlgorithm extractCryptoAlgorithm(String keyspace, String columnFamily, Object column){
		KeyspaceCoordinate keyspaceCoordinate = keyspaceConfiguration.get(keyspace);
		// guaranteed to be non-null due to DataLayer filtering non-encrypted requests
		return keyspaceCoordinate.lookup(keyspace, columnFamily, column);
	}

	/**
	 * This method is not likely to be ever used. Encrypted indexed columns make sense only when
	 * the column family is fully shared (i.e. a single key is used for encryption). To index,
	 * we use a deterministic encryption algorithm like AES with no random IV. 
	 * @param keyspace
	 * @param cfName
	 * @param column
	 * @throws Exception 
	 */
	public static void setCryptoAlgorithmForIndexedColumn(String keyspace,
			String cfName, Object column) throws Exception {
		KeyspaceCoordinate ksCoord = keyspaceConfiguration.get(keyspace);
		CryptoAlgorithm detCryptoAlg = CryptoFactory.newCustomAuthEncAlgorithm(null, "AES", "HMACSHA256");
		ColumnFamilyCoordinate 	cfCoordinate = new ColumnFamilyCoordinate(cfName, detCryptoAlg);
		// overwrite previous coordinate if any.
		ksCoord.addCoordinate(cfCoordinate);

	}
}
