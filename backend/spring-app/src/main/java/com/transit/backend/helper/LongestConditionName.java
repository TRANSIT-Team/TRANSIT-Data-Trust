package com.transit.backend.helper;

import io.github.perplexhub.rsql.RSQLJPASupport;
import org.springframework.stereotype.Component;


@Component
public class LongestConditionName {
	
	
	public static int longestConditonInQuery(String query) {
		var parsedQuery = RSQLJPASupport.toComplexMultiValueMap(query);
		
		int longestConditionName = 0;
		for (var parsedQueryItem : parsedQuery.keySet()) {
			var conditions = parsedQuery.get(parsedQueryItem);
			for (var condition : conditions.keySet()) {
				if (condition.replaceAll("=", "").length() > longestConditionName) {
					longestConditionName = condition.replaceAll("=", "").length();
				}
			}
		}
		return longestConditionName;
	}
	
}
