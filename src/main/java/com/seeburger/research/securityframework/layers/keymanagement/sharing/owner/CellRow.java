package com.seeburger.research.securityframework.layers.keymanagement.sharing.owner;

import java.util.HashMap;
import java.util.Map;

public class CellRow {
	private byte[] rowId;
	private String rowIdSerializerClass;
	
	// key is the columnName, value is columnSerializerClass
	private Map<byte[], String> cellMap = new HashMap<byte[], String>();
}
