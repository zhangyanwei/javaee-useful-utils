package com.worescloud.workdesk.storage.mongodb.adaptor;

import com.google.gson.*;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

public class DateTimeTypeConverter implements JsonSerializer<DateTime>, JsonDeserializer<DateTime> {

	// No need for an InstanceCreator since DateTime provides a no-args constructor
	@Override
	public JsonElement serialize(DateTime src, Type srcType, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

	@Override
	public DateTime deserialize(JsonElement json, Type type, JsonDeserializationContext context)
			throws JsonParseException {
		return new DateTime(json.getAsString());
	}

}
