package com.seeburger.research.securityframework.interceptor;

import java.nio.ByteBuffer;
import java.util.List;

import javax.crypto.BadPaddingException;

import me.prettyprint.cassandra.model.HColumnImpl;
import me.prettyprint.cassandra.model.MutatorImpl;
import me.prettyprint.cassandra.model.thrift.ThriftColumnQuery;
import me.prettyprint.cassandra.model.thrift.ThriftRangeSlicesQuery;
import me.prettyprint.cassandra.model.thrift.ThriftSliceQuery;
import me.prettyprint.cassandra.serializers.BytesArraySerializer;
import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.Serializer;
import me.prettyprint.hector.api.beans.ColumnSlice;
import me.prettyprint.hector.api.beans.HColumn;
import me.prettyprint.hector.api.beans.OrderedRows;
import me.prettyprint.hector.api.beans.Row;
import me.prettyprint.hector.api.ddl.ColumnFamilyDefinition;
import me.prettyprint.hector.api.query.QueryResult;
import me.prettyprint.hector.api.query.RangeSlicesQuery;

import com.seeburger.research.securityframework.configuration.ConfigurationReader;
import com.seeburger.research.securityframework.layers.keymanagement.KeyManagementLayer;
import com.seeburger.research.securityframework.layers.crypto.CryptoAlgorithm;
import com.seeburger.research.securityframework.layers.crypto.EncryptionLayer;
import com.seeburger.research.securityframework.layers.data.DataLayer;


privileged public aspect SecurityAspectDataDefinition {

	/**
	 * Adding 3 public fields to each HColumnImpl: rowId, cfname and keyspace
	 * name
	 */

	public String HColumnImpl.ksName = null;
	public String HColumnImpl.cfName = null;
	public Object HColumnImpl.rowId = null;

	private static BytesArraySerializer byteArraySerializer = BytesArraySerializer
			.get();

	/**
	 * Interceptor methods and interaction with Encryption Layers
	 */

	static {
		System.out
				.println(">>>Security Framework: Data definition aspect is loaded!");
	}

	/**
	 * Query interceptors
	 */

	pointcut queryExecute(): 
		execution (public * me.prettyprint.cassandra.model.*.execute() );

	pointcut getColumnValue():
		execution (public * me.prettyprint.cassandra.model.HColumnImpl.getValue());

	pointcut addEqualsOperandForIndex():
		execution (public RangeSlicesQuery me.prettyprint.hector.api.query.RangeSlicesQuery.addEqualsExpression(Object, Object));

	pointcut addOtherOperandForIndex():
		!addEqualsOperandForIndex() && 
		execution (public RangeSlicesQuery me.prettyprint.hector.api.query.RangeSlicesQuery.add*Expression(Object, Object));

	/**
	 * ------------------------------------------------------------------------
	 */

	before() : execution (public static void *.main(String[])){
		System.out
				.println(">>>Security Framework: Configuring the framework...");
		// validate the conf. file against xsd. If all ok, continue with
		// processing.
		try {
			long start = System.nanoTime();
			ConfigurationReader.configureFramework("/META-INF");
			long end = System.nanoTime();
			System.out.println(">>>Framework configured in: " + (end - start)
					/ 1000000000.0 + " seconds");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(-1);
		}
	}

	// intercept column family creation: retrieve data type mappings, change
	// validation class to BytesType for encrypted columns
	// When CF is shared fully, and indexed columns are encrypted, change the
	// encryption to deterministic
	// (works only when the whole CF is encrypted with a single key)
	String around(Cluster cluster, ColumnFamilyDefinition columnFamily) : call(public * me.prettyprint.hector.api.Cluster.addColumnFamily(ColumnFamilyDefinition, ..)) && target(cluster) && args(columnFamily, ..){
		System.out
				.println(">>>Security Framework: Intercepting addition of schema: "
						+ columnFamily.getName());
		try {
			columnFamily = DataLayer.processCFAddition(columnFamily);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return proceed(cluster, columnFamily);
	}

	/**
	 * Intercepting data insertion. Both 'insert' makes use of 'addInsertion'
	 */
	Object around(MutatorImpl mutator, Object key, String columnFamilyName,
			HColumn column) : 
				(
					call (public * *.addInsertion(Object, String, HColumn)) 
				) && 
				target(mutator) && 
				args(key, columnFamilyName, column){
		//System.out.println(">>> Intercepted insertion for cfname: "
		//		+ columnFamilyName + ", rowId: " + key + ", column: "
		//		+ column.getName() + " value: " + column.getValue());
		HColumnImpl col = encryptIfNeeded(mutator, key, columnFamilyName,
				(HColumnImpl) column);
		return proceed(mutator, key, columnFamilyName, col);
	}

	/**
	 * execute() query call - Note it is defined for SliceQuery and
	 * RangeSlicesQuery only - Could've used generatal Query interface but other
	 * types of queries (like counter will never be on encrypted data, so
	 * removing the overhead of intercepting them)
	 */
	
	QueryResult around(ThriftColumnQuery columnQuery) : queryExecute() && target(columnQuery) {
		//System.out.println("~~~~ About to call execute() on: "
		//		+ thisJoinPoint.getSignature().getDeclaringTypeName());
		// No need to change the value serializer because underneath data
		// is stored as bytes !
		QueryResult result = proceed(columnQuery);
		HColumnImpl column = (HColumnImpl) result.get();
		Object rowId = columnQuery.key;
		column.ksName = columnQuery.keyspace.getKeyspaceName();
		column.cfName = columnQuery.columnFamilyName;
		column.rowId = rowId;
		return result;
	}

	QueryResult around(ThriftSliceQuery sliceQuery) : queryExecute() && target(sliceQuery) {
		//System.out.println("~~~~ About to call execute() on: "
		//		+ thisJoinPoint.getSignature().getDeclaringTypeName());
		// No need to change the value serializer because underneath data
		// is stored as bytes !
		QueryResult result = proceed(sliceQuery);
		ColumnSlice cSlice = (ColumnSlice) result.get();
		Object rowId = sliceQuery.key;
		List columnList = cSlice.getColumns();
		for (Object column : columnList) {
			HColumnImpl c = (HColumnImpl) column;
			c.ksName = sliceQuery.keyspace.getKeyspaceName();
			c.cfName = sliceQuery.columnFamilyName;
			c.rowId = rowId;
		}
		return result;
	}

	QueryResult around(ThriftRangeSlicesQuery rangeSlicesQuery) : queryExecute() && target(rangeSlicesQuery) {
		//System.out.println("~~~~ About to call execute() on: "
		//		+ thisJoinPoint.getSignature().getDeclaringTypeName());
		// No need to change the valueSerializer because underneath values are
		// stored
		// as bytebuffers.
		QueryResult result = proceed(rangeSlicesQuery);
		List rowList = (((OrderedRows) result.get()).getList());
		for (Object row : rowList) {
			Row r = (Row) row;
			for (Object column : r.getColumnSlice().getColumns()) {
				HColumnImpl c = (HColumnImpl) column;
				c.ksName = rangeSlicesQuery.keyspace.getKeyspaceName();
				c.cfName = rangeSlicesQuery.columnFamilyName;
				c.rowId = r.getKey();
			}
		}
		return result;
	}

	/**
	 * Get value call - time to decrypt (if necessary)
	 */

	Object around(HColumnImpl hectorColumn) : getColumnValue()  && target(hectorColumn) {
		//System.out.println("~~~~ About to call getValue() on columnName = "
		//		+ hectorColumn.getName());
		return proceed(decryptIfNeeded(hectorColumn));
	}

	/**
	 * Adjust "equals" indexed expression
	 */

	RangeSlicesQuery around(ThriftRangeSlicesQuery rangeSlicesQuery,
			Object columnName, Object columnValue) : addEqualsOperandForIndex() && target(rangeSlicesQuery) && args(columnName, columnValue) {
		//System.out
		//		.println("~~~~ About to call addEqualsExpression on columnName : "
		//				+ columnName);
		columnValue = encryptIndexValueIfNeeded(rangeSlicesQuery, columnName,
				columnValue);
		proceed(rangeSlicesQuery, columnName, columnValue);
		return rangeSlicesQuery;
	}

	/**
	 * Adjust <other> indexed expression to throw RuntimeExceptions for
	 * encrypted index column
	 */

	RangeSlicesQuery around(ThriftRangeSlicesQuery rangeSlicesQuery,
			Object columnName, Object columnValue) : addOtherOperandForIndex() && target(rangeSlicesQuery) && args(columnName, columnValue) {
		//System.out.println("~~~~ About to call "
		//		+ thisJoinPoint.getSignature().getName() + " on columnName = "
		//		+ columnName);
		if (DataLayer.isCrypted(rangeSlicesQuery.keyspace.getKeyspaceName(),
				rangeSlicesQuery.columnFamilyName, columnName)) {
			throw new RuntimeException(
					"Unsupported encrypted index operation : \""
							+ thisJoinPoint.getSignature().getName() + "\" "
							+ "in keyspace: "
							+ rangeSlicesQuery.keyspace.getKeyspaceName()
							+ ", in cf: " + rangeSlicesQuery.columnFamilyName
							+ " on column: " + columnName);
		}
		proceed(rangeSlicesQuery, columnName, columnValue);
		return rangeSlicesQuery;
	}

	private HColumnImpl decryptIfNeeded(HColumnImpl hectorColumn) {
		String keyspaceName = hectorColumn.ksName;
		String columnFamilyName = hectorColumn.cfName;
		Object rowId = hectorColumn.rowId;
		Object columnName = hectorColumn.getName();
		Object columnValue = hectorColumn.column.value;// not a ByteBuffer but a HeapByteBuffer here..
		if (DataLayer.isCrypted(keyspaceName, columnFamilyName, columnName)) {
			CryptoAlgorithm crypto = EncryptionLayer.extractCryptoAlgorithm(
					keyspaceName, columnFamilyName, columnName);
			try {
				Serializer ser = SerializerTypeInferer.getSerializer(columnValue);
				byte[] pt = crypto.recover(ser.toBytes(columnValue), KeyManagementLayer
						.getKeys(keyspaceName, columnFamilyName, columnName,
								rowId));
				hectorColumn.column.value = ByteBuffer.wrap(pt);

			} catch (BadPaddingException ex) {
				// TODO: do some escalation cause the value was tampered with !
				ex.printStackTrace();
				throw new RuntimeException(ex);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RuntimeException(ex);
			}
		}
		return hectorColumn;
	}

	private HColumnImpl encryptIfNeeded(MutatorImpl mutator, Object key,
			String columnFamilyName, HColumnImpl origColumn) {
		String keyspaceName = getKeyspaceFromMutator(mutator);
		Object columnName = origColumn.getName();
		if (DataLayer.isCrypted(keyspaceName, columnFamilyName, columnName)) {
			CryptoAlgorithm crypto = EncryptionLayer.extractCryptoAlgorithm(
					keyspaceName, columnFamilyName, columnName);
			try {
				byte[] ct = crypto.process(origColumn.getValueBytes().array(),
						KeyManagementLayer.getKeys(keyspaceName,
								columnFamilyName, columnName, key));
				setByteValueOnColumn(origColumn, ct);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return origColumn;
	}

	/**
	 * Note that encrypted indexes work only for CF with self sharing
	 * granularity.
	 */
	private Object encryptIndexValueIfNeeded(ThriftRangeSlicesQuery query,
			Object columnName, Object originalValue) {
		String keyspaceName = query.keyspace.getKeyspaceName();
		String cfName = query.columnFamilyName;
		if (DataLayer.isCrypted(keyspaceName, cfName, columnName)) {
			CryptoAlgorithm crypto = EncryptionLayer.extractCryptoAlgorithm(
					keyspaceName, cfName, null);
			try {
				Serializer ser = SerializerTypeInferer
						.getSerializer(originalValue);
				byte[] ct = crypto.process(ser.toBytes(originalValue),
						KeyManagementLayer.getKeys(keyspaceName, cfName, null,
								null));
				originalValue = ser.fromBytes(ct);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return originalValue;
	}

	private String getKeyspaceFromMutator(MutatorImpl mutator) {
		Keyspace ksp = null;
		try {
			ksp = mutator.keyspace;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		return ksp.getKeyspaceName();
	}

	private void setByteValueOnColumn(HColumnImpl column, byte[] cipherText) {
		try {
			column.valueSerializer = (Serializer) byteArraySerializer;
			column.setValue(cipherText);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}