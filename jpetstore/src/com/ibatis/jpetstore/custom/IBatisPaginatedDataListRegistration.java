package com.ibatis.jpetstore.custom;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.ibatis.sqlmap.engine.mapping.statement.PaginatedDataList;

import de.javakaffee.kryoserializers.ReferenceFieldSerializerReflectionFactorySupport;
import de.javakaffee.web.msm.serializer.kryo.KryoCustomization;

public class IBatisPaginatedDataListRegistration implements KryoCustomization {

	@Override
	public void customize(Kryo kryo) {
		Serializer serializer = new ReferenceFieldSerializerReflectionFactorySupport(
				kryo, PaginatedDataList.class);
		kryo.register(PaginatedDataList.class, serializer);
	}

}