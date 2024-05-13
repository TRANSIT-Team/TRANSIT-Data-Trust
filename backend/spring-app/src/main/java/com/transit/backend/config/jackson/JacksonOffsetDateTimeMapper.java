package com.transit.backend.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.OffsetDateTime;

/**
 * Add to deserialize wrong DateTimeFormat
 */
@Configuration

public class JacksonOffsetDateTimeMapper extends JsonDeserializer<OffsetDateTime> {
	
	
	@Override
	public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
		if (p.getText() == null) {
			return null;
		}
		return OffsetDateTime.parse(p.getText());
	}
}