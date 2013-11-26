package com.seeburger.research.securityframework.layers.keymanagement.sharing.owner;

public class ResourceCoordinates {

    protected String cfName;
    protected byte[] columnName;
    protected String columnNameSerializerClass;
    protected String keyspace;
    protected byte[] rowid;
    protected String rowidSerializerClass;
    
    protected String requestorEndpoint;

    
    public String getRequestorEndpoint() {
		return requestorEndpoint;
	}

	public void setRequestorEndpoint(String requestorEndpoint) {
		this.requestorEndpoint = requestorEndpoint;
	}

	public String getCfName() {
        return cfName;
    }

    public void setCfName(String value) {
        this.cfName = value;
    }


    public byte[] getColumnName() {
        return columnName;
    }


    public void setColumnName(byte[] value) {
        this.columnName = ((byte[]) value);
    }


    public String getColumnNameSerializerClass() {
        return columnNameSerializerClass;
    }


    public void setColumnNameSerializerClass(String value) {
        this.columnNameSerializerClass = value;
    }


    public String getKeyspace() {
        return keyspace;
    }


    public void setKeyspace(String value) {
        this.keyspace = value;
    }


    public byte[] getRowid() {
        return rowid;
    }


    public void setRowid(byte[] value) {
        this.rowid = ((byte[]) value);
    }


    public String getRowidSerializerClass() {
        return rowidSerializerClass;
    }

   
    public void setRowidSerializerClass(String value) {
        this.rowidSerializerClass = value;
    }

}
