package com.transit.backend.rightlayers.domain;


import javax.lang.model.type.UnknownTypeException;
import java.io.Serializable;

public enum ClazzType implements Serializable {
	OA("OA"),
	UA("UA"),
	O("O"),
	
	E("E");
	
	private final String label;
	
	ClazzType(String label) {
		this.label = label;
	}
	
	/**
	 * Given a string, return the matching NodeType. If the type is null or not one of the types listed above,
	 * null will be returned
	 *
	 * @param type The String type to convert to a NodeType.
	 * @return the equivalent NodeType of the given String, or null if an invalid type or null is passed.
	 */
	public static ClazzType toNodeType(String type) throws UnknownTypeException {
		if (type == null) {
			throw new UnknownTypeException(null, null);
		}
		return switch (type.toUpperCase()) {
			case "OA" -> ClazzType.OA;
			case "UA" -> ClazzType.UA;
			case "O" -> ClazzType.O;
			case "E" -> ClazzType.E;
			default -> throw new UnknownTypeException(null, type);
		};
	}
	
	public String toString() {
		return label;
	}
}