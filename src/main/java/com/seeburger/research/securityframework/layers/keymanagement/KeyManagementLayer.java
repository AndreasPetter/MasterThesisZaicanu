package com.seeburger.research.securityframework.layers.keymanagement;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.seeburger.research.securityframework.configuration.KeyspaceConfiguration;
import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.layers.crypto.EncryptionLayer;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.CellIdBuilder;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.ColumnFamilyLookup;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.ColumnFamilyLookupExt;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.ColumnIdBuilder;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.IdBuilder;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.KeyspaceLookup;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.RowIdBuilder;
import com.seeburger.research.securityframework.layers.keymanagement.datastructures.SimpleColumnFamilyLookup;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.KeyRequestor;
import com.seeburger.research.securityframework.layers.keymanagement.storage.CachingStorageStrategy;
import com.seeburger.research.securityframework.layers.keymanagement.storage.ExternalCFStrategy;
import com.seeburger.research.securityframework.layers.keymanagement.storage.ExternalKeyStorageStrategy;
import com.seeburger.research.securityframework.layers.keymanagement.storage.NoCachingStorageStrategy;
import com.seeburger.research.securityframework.layers.keymanagement.storage.StorageStrategy;

public class KeyManagementLayer {

	public static final String KEYSTORE_TYPE = "jceks";
	public static final String KS_ALIAS_CERT = "pke_cert";
	private static final String KS_ALIAS_SK = "privateKey";
	private static final String KS_ALIAS_ENC_SMK_PREFIX = "e_smk";
	private static final String KS_ALIAS_INT_SMK_PREFIX = "i_smk";
	private static final char[] keystore_pass = {};

	private static final String HASH_ALG = "HMACSHA256";
	public static final String CERT_TYPE = "X.509";
	public static final Mac hashingAlgorithm = getMacInstance();
	public static final Charset defaultEncoding = Charset.forName("UTF-8");

	private static Key[] e_smk;
	private static Key[] i_smk;

	private static Map<String, KeyspaceLookup> keylookup = new HashMap<String, KeyspaceLookup>(
			2);

	private static Mac getMacInstance() {
		try {
			return Mac.getInstance(HASH_ALG);
		} catch (NoSuchAlgorithmException e) {
			System.exit(-2);
		}
		return null;
	}

	public static void extractMasterData(InputStream keyStoreStream)
			throws Exception {
		KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
		ks.load(keyStoreStream, keystore_pass);
		KeyRequestor.pk = ks.getCertificate(KS_ALIAS_CERT).getPublicKey();
		KeyRequestor.sk = (PrivateKey) ks.getKey(KS_ALIAS_SK, keystore_pass);
		List<Key> encryptionKeySuperMasterSet = new ArrayList<Key>();
		List<Key> integrityKeySuperMasterSet = new ArrayList<Key>();
		int i = 1;
		while (true) {
			Key int_smk = ks.getKey(KS_ALIAS_INT_SMK_PREFIX + i, keystore_pass);
			if (int_smk == null) {
				if (integrityKeySuperMasterSet.size() > 0)
					i_smk = integrityKeySuperMasterSet.toArray(new Key[0]);
				else {
					throw new RuntimeException(
							"No SMK for integrity algorithm were found in keyspace !\n"
									+ "Name your integrity keys as: i_smk<consecutive_number>");
				}
				break;
			} else {
				integrityKeySuperMasterSet.add(int_smk);
				i++;
			}
		}
		i = 1;
		while (true) {
			Key enc_smk = ks.getKey(KS_ALIAS_ENC_SMK_PREFIX + i, keystore_pass);
			if (enc_smk == null) {
				if (encryptionKeySuperMasterSet.size() > 0) {
					e_smk = encryptionKeySuperMasterSet.toArray(new Key[0]);
				} else {
					throw new RuntimeException(
							"No SMK for encryption algorithm were found in keyspace !\n"
									+ "Name your encryption keys as: e_smk<consecutive_number>");
				}

				break;
			} else {
				encryptionKeySuperMasterSet.add(enc_smk);
				i++;
			}
		}

	}

	public static void processKeyspaceConfiguration(
			com.seeburger.research.securityframework.configuration.KeyspaceConfiguration.KeyManagementLayerConf keyspaceConfiguration)
			throws Exception {
		String keyspace = keyspaceConfiguration.getKeyspaceName();
		KeyspaceLookup ksLookup = new KeyspaceLookup();
		// Note: for keyspace we generate the keys anyway
		// note how we generate keys regardless of the algorithm used. This is
		// to accommodate
		// all possible customizations of the algorithm (e.g. integrity only at
		// keyspace level, and for some column - auth. enc.).
		// A slight design defect of the framework ..
		Key keyspaceEncKey = generateKey(keyspace.getBytes(defaultEncoding),
				e_smk);
		Key keyspaceIntKey = generateKey(keyspace.getBytes(defaultEncoding),
				i_smk);
		ksLookup.setGenerationKeys(keyspaceEncKey, keyspaceIntKey);
		for (String cfName : keyspaceConfiguration.getColumnFamilyNames()) {
			// now configure the columnfamilylookup object:
			ksLookup.addColumnFamilyLookup(
					cfName,
					configureColumnFamilyLookup(keyspace, cfName,
							keyspaceConfiguration));
		}
		keylookup.put(keyspace, ksLookup);
	}

	private static ColumnFamilyLookup configureColumnFamilyLookup(
			String keyspace, String cfName,
			KeyspaceConfiguration.KeyManagementLayerConf keyspaceConfiguration)
			throws Exception {
		final boolean isColumnFamilyOwner = keyspaceConfiguration
				.isOwner(cfName);
		// get the sharingGranularity now and configure the storage strategy !
		GranularityType sharingGranularity = keyspaceConfiguration
				.getSharingGranularity(cfName);
		StorageStrategy strategy;
		IdBuilder idBuilder;
		switch (sharingGranularity) {
		case ROW:
			idBuilder = new RowIdBuilder();
			break;
		case COLUMN:
			idBuilder = new ColumnIdBuilder();
			break;
		case CELL:
			idBuilder = new CellIdBuilder();
			break;
		default:
			idBuilder = null;
			break;
		}
		if (!isColumnFamilyOwner) {
			String algNames[] = EncryptionLayer.extractCryptoAlgorithm(
					keyspace, cfName, null).getAlgorithmNames();
			if (idBuilder != null){
				strategy = new ExternalKeyStorageStrategy(keyspace, cfName, algNames,
					"/externalKeys/.extKeys");
				strategy.setIdBuilder(idBuilder);
			}
			else {// full CF sharing
				strategy = new ExternalCFStrategy(keyspace, cfName, algNames);
			}
			return new ColumnFamilyLookupExt(strategy);
		} else {
			// The keys for encryption and hashing are generated in any case,
			// regardless of
			// default algorithm for CF. Some columns might be configured with
			// different algorithms.
			// Note: TODO - per column algorithm customization is not supported
			// at the moment
			byte[] cfNameb = cfName.getBytes(defaultEncoding);
			Key cfKeyEnc = generateKey(cfNameb, e_smk);
			Key cfKeyInt = generateKey(cfNameb, i_smk);
			if (sharingGranularity == GranularityType.SELF) {
				return new SimpleColumnFamilyLookup(cfKeyEnc, cfKeyInt);
			}
			int cacheSize = keyspaceConfiguration.getCacheSize(cfName);
			String algNames[] = EncryptionLayer.extractCryptoAlgorithm(
					keyspace, cfName, null).getAlgorithmNames();
			if (cacheSize < 1)// no caching
			{
				strategy = new NoCachingStorageStrategy(algNames,
						Mac.getInstance(HASH_ALG));
			} else {
				strategy = new CachingStorageStrategy(algNames,
						Mac.getInstance(HASH_ALG), cacheSize);
			}
			strategy.setIdBuilder(idBuilder);
			return new ColumnFamilyLookupExt(cfKeyEnc, cfKeyInt, strategy);
		}
	}

	private static Key generateKey(byte[] pt, Key... keys) throws Exception {
		for (Key key : keys) {
			hashingAlgorithm.init(key);
			byte[] ct = hashingAlgorithm.doFinal(pt);
			pt = ct;
		}
		return new SecretKeySpec(pt, "");
	}

	public static GranularityType getSharingGranularity(String keyspace,
			String cfName) {
		KeyspaceLookup keyspaceLookup = keylookup.get(keyspace);
		return keyspaceLookup.getGranularityFor(cfName);
	}

	public static Key[] getKeys(String keyspaceName, String columnFamilyName,
			Object column, Object rowId) {
		return keylookup.get(keyspaceName).lookupKeys(columnFamilyName, rowId,
				column);
	}

}
