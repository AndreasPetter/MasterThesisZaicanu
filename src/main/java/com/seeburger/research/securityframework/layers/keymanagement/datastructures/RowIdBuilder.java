package com.seeburger.research.securityframework.layers.keymanagement.datastructures;

import com.seeburger.research.policyengine.ws.ResourceCoordinates;
import com.seeburger.research.securityframework.configuration.types.GranularityType;

import me.prettyprint.cassandra.serializers.SerializerTypeInferer;
import me.prettyprint.hector.api.Serializer;

public class RowIdBuilder extends IdBuilder {
	
	@Override
	public Id getId(final Object rowId, final Object columnName) {
		return new RowId(rowId);
	}

	@Override
	public GranularityType getGranularity() {
		return GranularityType.ROW;
	}

	@Override
	public ResourceCoordinates getResourceCoordinates(String keypace,
			String cfName, Object rowId, Object columnName) {
		ResourceCoordinates resource = new ResourceCoordinates();
		resource.setKeyspace(keypace);
		resource.setCfName(cfName);
		Serializer ser = SerializerTypeInferer.getSerializer(rowId);
		resource.setRowidSerializerClass(ser.getClass().getName());
		resource.setRowid(ser.toBytes(rowId));
		return resource;
	}

	@Override
	public Id getId(byte[] rowid, String rowSerializerClass, byte[] columnName,
			String columnNameSerializerClass) {
		Object row = IdBuilder.getSerializerFor(rowSerializerClass).fromBytes(rowid);
		return new RowId(row);
	}

}

class RowId extends Id {
	private final Object row;
	private final byte[] bytes;
	
	public RowId(Object row){
		this.row = row;
		bytes = SerializerTypeInferer.getSerializer(row).toBytes(row);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof RowId))
			return false;
		RowId other = (RowId) obj;
		return row.equals(other.row);
	}

	@Override
	public int hashCode() {
		return row.hashCode();
	}

	@Override
	public byte[] getIdBytes() {
		return bytes;
	}
}
