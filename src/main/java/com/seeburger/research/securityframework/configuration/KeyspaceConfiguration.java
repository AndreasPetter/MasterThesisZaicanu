package com.seeburger.research.securityframework.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import com.seeburger.research.securityframework.configuration.cryptoAlgorithms.CryptoFactory;
import com.seeburger.research.securityframework.configuration.types.AllOrNothingType;
import com.seeburger.research.securityframework.configuration.types.ColumnFamilyAsset;
import com.seeburger.research.securityframework.configuration.types.ColumnSetExceptionType;
import com.seeburger.research.securityframework.configuration.types.EncryptedAsset;
import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.configuration.types.KeyspaceAsset;
import com.seeburger.research.securityframework.configuration.types.WsEndpoint;
import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;
import com.seeburger.research.securityframework.layers.keymanagement.sharing.requestor.WSAddress;

public class KeyspaceConfiguration {
	private KeyspaceAsset keyspaceAsset;
	private Map<String, Integer> cfMapping = new HashMap<String, Integer>();

	public KeyspaceConfiguration(KeyspaceAsset ka) {
		keyspaceAsset = ka;
		List<ColumnFamilyAsset> cfAssets = keyspaceAsset.getColumnFamily();
		int i = 0;
		for (ColumnFamilyAsset cfa : cfAssets) {
			cfMapping.put(cfa.getName(), i++);
		}
	}
	
	public ColumnFamilyAsset getColumnFamilyAsset(String cfName){
		return keyspaceAsset.getColumnFamily().get(cfMapping.get(cfName));
	}

	public class CommonLayerFunctions {

		public String getKeyspaceName() {
			return keyspaceAsset.getName();
		}

		public String[] getColumnFamilyNames() {
			return cfMapping.keySet().toArray(new String[0]);
		}
	}
	
	public class KeyManagementLayerConf extends CommonLayerFunctions{

		public boolean isOwner() {
			return keyspaceAsset.isOwner();
		}
		
		public boolean isOwner(String cfName) {
			ColumnFamilyAsset cfa = getColumnFamilyAsset(cfName);
			Boolean b = cfa.isOwner();
			if (b != null)
				return b;
			else return keyspaceAsset.isOwner();
		}

		public GranularityType getSharingGranularity(String cfName) {
			ColumnFamilyAsset cfa = getColumnFamilyAsset(cfName);
			return cfa.getSharingGranularity();
		}

		public int getCacheSize(String cfName) {
			ColumnFamilyAsset cfa = getColumnFamilyAsset(cfName);
			return cfa.getCacheSize();
		}
	}

	public class EncryptionLayerConf extends CommonLayerFunctions {

		public CryptoAlgorithm getCryptoAlgorithm() throws Exception {
			CryptoAlgorithm crypto = getEncryptedAssetCryptoAlgorithm(keyspaceAsset);
			if (crypto == null)
				throw new RuntimeException(
						"Configuration error. For encrypted keyspace: "
								+ keyspaceAsset.getName()
								+ " no standard authenticated encryption, nor custom authenticated encryption nor integrity checks where specified. Only Encryption algorithms are not safe to use!");
			else
				return crypto;
		}

		private CryptoAlgorithm getEncryptedAssetCryptoAlgorithm(
				EncryptedAsset asset) throws Exception {
			String providerID = asset.getProvider();
			String authenticatedEncryptionAlgorithm = asset
					.getAuthenticatedEncryption();
			if (authenticatedEncryptionAlgorithm == null) {
				String integrityAlgorithm = asset.getIntegrityAlgorithm();
				if (integrityAlgorithm == null) {
					return null;
				} else {
					String encryptionAlgorithm = keyspaceAsset
							.getEncryptionAlgorithm();
					if (encryptionAlgorithm == null) {
						return CryptoFactory.newIntegrityAlgorithm(
								providerID, integrityAlgorithm);
					} else {
						return CryptoFactory
								.newCustomAuthEncAlgorithm(providerID,
										encryptionAlgorithm, integrityAlgorithm);
					}
				}
			} else
				return CryptoFactory.newAuthEncCryptoAlgorithm(
						providerID, authenticatedEncryptionAlgorithm);
		}

		public CryptoAlgorithm getCryptoAlgorithmForCF(String columnFamily,
				CryptoAlgorithm keyspaceAlgorithm) throws Exception {
			ColumnFamilyAsset cfa = getColumnFamilyAsset(columnFamily);
			CryptoAlgorithm crypto = getEncryptedAssetCryptoAlgorithm(cfa);
			if (crypto == null)
				return keyspaceAlgorithm.clone();
			else
				return crypto;
		}
	}

	public class DataLayerConf extends CommonLayerFunctions {
		public boolean isDefaultEncrypted(String cfName) {
			AllOrNothingType policyType = getColumnFamilyAsset(cfName).getDefaultEncryptionPolicy();
			switch (policyType) {
			case NONE:
				return false;
			case ALL:
				return true;
			}
			return false;
		}

		public List<String> getEncryptionExceptions(String cfName) {
			ColumnFamilyAsset cfa = getColumnFamilyAsset(cfName);
			List<String> exceptions = new ArrayList<String>();
			if (cfa.getColumnExceptionSet() != null) {
				List<JAXBElement<ColumnSetExceptionType>> exs = cfa
						.getColumnExceptionSet()
						.getSpecificColumnSetExceptionOrPrefixedColumnSetExceptionOrRegexFilteredColumnSetException();
				for (JAXBElement<ColumnSetExceptionType> exception : exs) {
					String exceptionType = exception.getName().getLocalPart();
					if (exceptionType.equals("prefixedColumnSetException")) {
						exceptions.add(exception.getValue().getFilter() + "*");
					} else
						exceptions.add(exception.getValue().getFilter());
				}
				if (exceptions.size() > 0)
					return exceptions;
				else
					return null;
			}
			return null;
		}
	}
}
