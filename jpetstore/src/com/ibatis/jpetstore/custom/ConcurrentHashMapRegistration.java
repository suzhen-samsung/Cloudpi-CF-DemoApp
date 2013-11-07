package com.ibatis.jpetstore.custom;

import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serialize.MapSerializer;
import de.javakaffee.web.msm.serializer.kryo.KryoCustomization;

public class ConcurrentHashMapRegistration implements KryoCustomization {

	@Override
	public void customize(Kryo kryo) {
		kryo.register(ConcurrentHashMap.class, new MapSerializer(kryo));
	}

}