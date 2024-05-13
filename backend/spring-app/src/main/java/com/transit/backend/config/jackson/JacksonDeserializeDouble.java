package com.transit.backend.config.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class JacksonDeserializeDouble extends JsonDeserializer<Double> {
	@Override
	public Double deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		if (p.getText() == null) {
			return Double.NaN;
		}
		if (p.getText().isBlank()) {
			return Double.NaN;
		}
		double d = Double.parseDouble(p.getText().replaceAll("\"", "").replaceAll(",", "."));
		if (d == 0.0) {
			return Double.NaN;
		}
		return d;
	}
}
