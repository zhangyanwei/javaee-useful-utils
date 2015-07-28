package com.worescloud.workdesk.storage.mongodb.sort;

import com.mongodb.BasicDBObject;
import com.worescloud.workdesk.storage.Sort;
import org.bson.conversions.Bson;

import java.util.List;

public final class MongoDBSortConverter {

	public static Bson convert(List<Sort> sorts) {
		BasicDBObject basicDBObject = new BasicDBObject();
		sorts.stream()
				.forEach(sort -> basicDBObject.put(sort.getFieldName(), sort.isDesc() ? -1 : 1));

		return basicDBObject;
	}

}