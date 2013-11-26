package com.seeburger.research.securityframework.layers.data;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import me.prettyprint.cassandra.serializers.DoubleSerializer;
import me.prettyprint.cassandra.serializers.LongSerializer;
import me.prettyprint.cassandra.serializers.StringSerializer;
import me.prettyprint.cassandra.service.ThriftColumnDef;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.ddl.ColumnDefinition;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.ddl.ColumnType;
import me.prettyprint.hector.api.ddl.ComparatorType;

import org.apache.commons.lang.NotImplementedException;

import com.seeburger.research.securityframework.configuration.KeyspaceConfiguration;
import com.seeburger.research.securityframework.configuration.types.GranularityType;
import com.seeburger.research.securityframework.layers.crypto.EncryptionLayer;
import com.seeburger.research.securityframework.layers.data.matchables.Matchable;
import com.seeburger.research.securityframework.layers.data.matchables.MatchableString;
import com.seeburger.research.securityframework.layers.data.matchables.PatternMatchableString;
import com.seeburger.research.securityframework.layers.data.matchables.PrefixMatchableString;
import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;

/**
 * This layer serves two purposes:
 * <ol>
 * <li>Store the dataset that should be encrypted (and thus not passing data for
 * encryption if it is not in the dataset)
 * <li>There is no need to store the datatypes of the columns since internally
 * the values are retrieved as bytes and only upon request are they converted
 * to their code-level types.
 * </ol>
 * 
 * @author Eugen
 * 
 */
public class DataLayer {

	/**
	 * Looking up this data structure yields the dataset that is encrypted. If
	 * the data is not found then the requested dataset is not encrypted and is
	 * not passed to the encryption layer...
	 */
	private static Map<String, Map<String, Collection<Matchable>>> dataLayerConf = new HashMap<String, Map<String, Collection<Matchable>>>();
	private static Set<String> defaultEncryptedCFs = new HashSet<String>();
	private static final String byteValidationClass = ComparatorType.BYTESTYPE.getTypeName();

	public static void processKeyspaceConfiguration(
			KeyspaceConfiguration.DataLayerConf keyspaceConf) {
		String keyspaceName = keyspaceConf.getKeyspaceName();
		Map<String, Collection<Matchable>> cfMap = new HashMap<String, Collection<Matchable>>();
		for (String columnFamilyName : keyspaceConf.getColumnFamilyNames()) {
			/*
			 * The only difference between default encrypted and not default
			 * encrypted is that columns for which the default encryption policy
			 * is enabled are stored in <code>defaultEncryptedCFs</code>. Then
			 * the processing for whether the column name matches or not is done
			 * accordingly: - A match for a CF where default encryption is
			 * enabled means the column shouldn't be encrypted. No match - must
			 * encrypt. - A match for a CF where default encryption is not
			 * enabled means the column should be encrypted. No match - not
			 * encrypt.
			 */
			if (keyspaceConf.isDefaultEncrypted(columnFamilyName)) {
				defaultEncryptedCFs.add(keyspaceName + "%" + columnFamilyName);
			}
			List<String> exceptions = keyspaceConf
					.getEncryptionExceptions(columnFamilyName);
			if (exceptions != null) {
				Collection<Matchable> matchableExceptions = new ArrayList<Matchable>();
				for (String exception : exceptions) {
					switch (exceptionType(exception)) {
					case FIXED_COLUMN_NAME:
						matchableExceptions.add(new MatchableString(exception));
						break;
					case COLUMN_NAME_PREFIX:
						matchableExceptions
								.add(new PrefixMatchableString(exception
										.substring(0, exception.length() - 1)));// drop
																				// the
																				// *
																				// character
																				// at
																				// the
																				// end
						break;
					case COLUMN_NAME_PATTERN:
						matchableExceptions.add(new PatternMatchableString(
								exception));
						break;
					default:
						// should not happen, ever !
						throw new RuntimeException("Unknown Exception Type "
								+ exceptionType(exception) + " for: "
								+ exception);
					}
				}
				cfMap.put(columnFamilyName, matchableExceptions);
			}
		}
		dataLayerConf.put(keyspaceName, cfMap);
	}

	private static final int FIXED_COLUMN_NAME = 0;
	private static final int COLUMN_NAME_PREFIX = 1;
	private static final int COLUMN_NAME_PATTERN = 2;

	private static int exceptionType(String exception) {
		if (exception.matches("[a-zA-Z0-9_]+"))
			return FIXED_COLUMN_NAME;
		else if (exception.matches("[a-zA-Z0-9_]+\\*"))
			return COLUMN_NAME_PREFIX;
		else
			return COLUMN_NAME_PATTERN;
	}

	public static boolean isCrypted(String keyspace, String columnFamily,
			Object columnName) {
		if (checkIfDefaultEncrypted(keyspace, columnFamily) ^ found(keyspace, columnFamily, columnName))
			return true;
		else return false;
	}

	public static Object toDataType(String keyspace, String columnFamily,
			Object columnName, byte[] columnBytes) {
		throw new NotImplementedException();
	}

	/**
	 * Method to read and transform a column family definition that is about to
	 * be added to the cluster. Processing on the column family defintion is
	 * done only wrt to columnfamilies that should contain encrypted content and
	 * includes:
	 * <ul>
	 * <li>Adding the data type mapping information to the DataLayer and
	 * changing the validation class to byte[]
	 * <li>Reconfiguring encryption type on encrypted, indexed columns (should
	 * use deterministic encryption)
	 * </ul>
	 * 
	 * @param cfdef
	 * @return
	 * @throws Exception
	 */
	public static ColumnFamilyDefinition processCFAddition(
			ColumnFamilyDefinition cfdef) throws Exception {
		// determine if this cf has encrypted data
		if (cfdef.getColumnType() == ColumnType.SUPER) {
			System.out
					.println(">>>info: encryption of super columns not supported !");
			return cfdef;
		}
		boolean isDefaultEncrypted = checkIfDefaultEncrypted(cfdef);
		ComparatorType cmpType = cfdef.getComparatorType();
		String keyspace = cfdef.getKeyspaceName();
		String cfName = cfdef.getName();
		Serializer cnSer = getSerializer(cmpType);
		for (ColumnDefinition cfDef : cfdef.getColumnMetadata()) {
			Object column = cnSer.fromByteBuffer(cfDef.getName());
			if (found(keyspace, cfName, column) ^ isDefaultEncrypted) {
				String validationClass = cfDef.getValidationClass();
				if (!isByteDataType(validationClass)) {
					// TODO: remove definitively
					//DataConversion.addMapping(keyspace, cfName, column,
					//		validationClass);
					//System.out.println(">>> ColumnDefinition cfDef is actually:"+cfDef.getClass().getName());
					if (cfDef instanceof ThriftColumnDef){
						ThriftColumnDef thriftCfDef = (ThriftColumnDef) cfDef;
						Field validationClassField = ThriftColumnDef.class.getDeclaredField("validationClass");
						validationClassField.setAccessible(true);
						validationClassField.set(thriftCfDef, byteValidationClass);
					}
					else{
						throw new RuntimeException(cfDef+" is actually of type:"+cfDef.getClass().getName());
					}
				}
				// special processing for indexed columns
				if (cfDef.getIndexType() != null) {
					// has index defined -> change encryption to deterministic
					// only if sharing
					// is whole column family ! -> i.e. a single key is used.
					// Doesn't work with
					// finer grained encryption schemes because keys for each
					// row are different !!!
					if (KeyManagementLayer.getSharingGranularity(keyspace,
							cfName) == GranularityType.SELF)
						EncryptionLayer.setCryptoAlgorithmForIndexedColumn(
								keyspace, cfName, column);
					else
						throw new RuntimeException(
								"Impossible settings: Indexed encrypted column in a column family with sharing granularity :"
										+ KeyManagementLayer
												.getSharingGranularity(
														keyspace, cfName));
				}
			}
			// else do nothing
		}
		if (isDefaultEncrypted) {
			String validationClass = cfdef.getDefaultValidationClass();
			if (!isByteDataType(validationClass)) {
				DataConversion.addDefaultMapping(keyspace, cfName,
						validationClass);
				cfdef.setDefaultValidationClass(ComparatorType.BYTESTYPE
						.getTypeName());
			}// otherwise a no-op
		}
		return cfdef;
	}

	private static Serializer getSerializer(ComparatorType compType) {
		String typeName = compType.getTypeName();
		if (typeName.equals("UTF8Type")) {
			return StringSerializer.get();
		} else if (typeName.equals("DoubleType")) {
			return DoubleSerializer.get();
		} else if (typeName.equals("LongType")) {
			return LongSerializer.get();
		} else {
			throw new RuntimeException(
					"SerializerType mapping from Comparator Type: " + typeName
							+ " currently not supported.");
		}
	}

	private static boolean found(String keyspace, String cfName,
			Object columnName) {
		Map<String, Collection<Matchable>> cfs = dataLayerConf.get(keyspace);
		if (cfs == null)
			return false;
		Collection<Matchable> cfExceptions = cfs.get(cfName);
		if (cfExceptions == null)
			return false;
		for (Matchable m : cfExceptions) {
			if (m.matches(columnName))
				return true;
		}
		return false;
	}

	private static boolean checkIfDefaultEncrypted(ColumnFamilyDefinition cfdef) {
		return defaultEncryptedCFs.contains(cfdef.getKeyspaceName() + "%"
				+ cfdef.getName());
	}
	
	private static boolean checkIfDefaultEncrypted(String keyspace, String cf){
		return defaultEncryptedCFs.contains(keyspace + "%" + cf);
	}
	

	private static boolean isByteDataType(String validationClass) {
		return validationClass.endsWith("BytesType");
	}
}
