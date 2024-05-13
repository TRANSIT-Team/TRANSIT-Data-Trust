package com.transit.backend.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

@Configuration
public class JacksonOffSerDateTimeMapperSerialize extends JsonSerializer<OffsetDateTime> {
	
	
	@Override
	public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
		if (value == null) {
			gen.writeString(String.valueOf(value));
		}
		
		
		gen.writeString(value.truncatedTo(ChronoUnit.MICROS).toString());
		
	}
}