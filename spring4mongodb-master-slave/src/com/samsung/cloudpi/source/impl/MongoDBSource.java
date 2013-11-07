package com.samsung.cloudpi.source.impl;

import java.net.UnknownHostException;


import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.samsung.cloudpi.source.bean.Credential;

public class MongoDBSource {
	public Mongo getSource(Credential credential) throws Exception {

		Mongo m = null;
		try {
			m = new Mongo(credential.getHost(), credential.getPort());
			DB db = m.getDB(credential.getDb());
			boolean auth = db.authenticate(credential.getUsername(), credential
					.getPassword().toCharArray());
			if (!auth)
				throw new Exception("authentication failed");
		} catch (UnknownHostException e) {
			throw new Exception("can not connect to mongodb service");
		} catch (MongoException e) {
			throw new Exception("can not connect to mongodb service");
		}
		return m;
	}
}
