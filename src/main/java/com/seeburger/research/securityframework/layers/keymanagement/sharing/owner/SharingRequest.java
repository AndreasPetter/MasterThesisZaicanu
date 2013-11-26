package com.seeburger.research.securityframework.layers.keymanagement.sharing.owner;

public class SharingRequest {
	
	private byte[] requestId;
	private String keyspace;
	private String cfName;
	// 0 - row, 1 - column, -1 - cell
	private int sharingGranularity;
	
	private byte[] rowid;
	private String rowidSerializerClass;
	
	private byte[] columnName;
	private String columnNameSerializerClass;
	
	private CellRow[] cells;

}
