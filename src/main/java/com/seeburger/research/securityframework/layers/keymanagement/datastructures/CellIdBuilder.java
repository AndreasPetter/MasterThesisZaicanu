package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.configuration.types.GranularityType;

import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.hector.api.Serializer;


public class CellIdBuilder extends IdBuilder{
	@Override
	public Id getId(final Object rowId, final Object columnName) {
		return new CellId(rowId, columnName);
	}

	@Override
	public GranularityType getGranularity() {
		return GranularityType.CELL;
	}

	@Override
	public ResourceCoordinates getResourceCoordinates(String keypace,
			String cfName, Object rowId, Object column) {
		ResourceCoordinates resource = new ResourceCoordinates();
		resource.setKeyspace(keypace);
		resource.setCfName(cfName);
		Serializer ser = SerializerTypeInferer.getSerializer(rowId);
		resource.setRowidSerializerClass(ser.getClass().getName());
		resource.setRowid(ser.toBytes(rowId));
		ser = SerializerTypeInferer.getSerializer(column);
		resource.setColumnNameSerializerClass(ser.getClass().getName());
		resource.setColumnName(ser.toBytes(column));
		return resource;
	}

	@Override
	public Id getId(byte[] rowid, String rowSerializerClass, byte[] columnName,
			String columnNameSerializerClass) {
		Object row = IdBuilder.getSerializerFor(rowSerializerClass).fromBytes(rowid);
		Object colName = IdBuilder.getSerializerFor(columnNameSerializerClass).fromBytes(columnName);
		return new CellId(row, colName);
	}
}

class CellId extends Id{
	
	private final Object row;
	private final Object column;
	private final byte[] bytes;
	
	public CellId(Object row, Object column){
		this.row = row;
		this.column = column;
		byte[] rb = SerializerTypeInferer.getSerializer(row).toBytes(row);
		byte[] cb = SerializerTypeInferer.getSerializer(column).toBytes(column);
		bytes = new byte[rb.length+cb.length];
		System.arraycopy(rb, 0, bytes, 0, rb.length);
		System.arraycopy(cb, 0, bytes, rb.length, cb.length);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null || !(obj instanceof CellId))
			return false;
		CellId other = (CellId) obj;
		return (row.equals(other.row))&&(column.equals(other.column));
	}

	@Override
	public int hashCode() {
		return (row.hashCode() << 16) + column.hashCode();
	}

	@Override
	public byte[] getIdBytes() {
		return bytes;
	}
	
}
