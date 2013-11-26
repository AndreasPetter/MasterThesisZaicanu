package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.configuration.types.GranularityType;

import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.hector.api.Serializer;

public class ColumnIdBuilder extends IdBuilder{
	
	@Override
	public Id getId(final Object rowId, final Object columnName) {
		return new ColumnId(columnName);
	}

	@Override
	public GranularityType getGranularity() {
		return GranularityType.COLUMN;
	}

	@Override
	public ResourceCoordinates getResourceCoordinates(String keypace,
			String cfName, Object rowId, Object columnName) {
		ResourceCoordinates resource = new ResourceCoordinates();
		resource.setKeyspace(keypace);
		resource.setCfName(cfName);
		Serializer ser = SerializerTypeInferer.getSerializer(columnName);
		resource.setColumnNameSerializerClass(ser.getClass().getName());
		resource.setColumnName(ser.toBytes(columnName));
		return resource;
	}

	@Override
	public Id getId(byte[] rowid, String rowSerializerClass, byte[] columnName,
			String columnNameSerializerClass) {
		Object colName = IdBuilder.getSerializerFor(columnNameSerializerClass).fromBytes(columnName);
		return new ColumnId(colName);
	}
}

class ColumnId extends Id{

	private final Object column;
	private final byte[] bytes;
	
	public ColumnId(Object column){
		this.column= column;
		bytes = SerializerTypeInferer.getSerializer(column).toBytes(column);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ColumnId))
			return false;
		ColumnId other = (ColumnId) obj;
		return column.equals(other.column);
	}

	@Override
	public int hashCode() {
		return column.hashCode();
	}

	@Override
	public byte[] getIdBytes() {
		return bytes;
	}
}