package com.transit.backend.rightlayers.service.helper;

import com.transit.backend.config.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class StringListHelper {
	
	
	public static String collectionToString(Collection<String> values) {
		values = new HashSet<>(values);
		return StringUtils.join(values, Constants.DELIMITER);
	}
	
	
	public static List<String> stringToList(String values) {
		if (values.isEmpty() || values.isBlank()) {
			return new ArrayList<>();
		}
		return Arrays.asList(values.split(Constants.DELIMITER));
	}
	
	public static Set<String> stringToSet(String values) {
		if (values.isEmpty() || values.isBlank()) {
			return new HashSet<>();
		}
		return new HashSet<>(Arrays.asList(values.split(Constants.DELIMITER)));
	}
	
	
	public static Map<String, String> removeMapDuplicates(Map<String, String> inputMap) {
		
		Set<String> uniqueKeys = new HashSet<>();
		Map<String, String> resultMap = new HashMap<>();
		
		for (Map.Entry<String, String> entry : inputMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			
			if (!uniqueKeys.contains(key)) {
				uniqueKeys.add(key);
				resultMap.put(key, value);
			}
		}
		
		return resultMap;
	}
	
	
	public static Set<String> removeSetDuplicates(Set<String> inputSet) {
		
		Set<String> resultSet = new HashSet<>(inputSet);
		
		return resultSet;
	}
	
}
