package com.transit.geoservice.helper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class IdListHelper {
	
	public String getId(List<String> ids) {
		
		List<String> copiedList = new ArrayList<>();
		copiedList.addAll(ids);
		
		Collections.sort(copiedList);
		StringBuilder builder = new StringBuilder();
		
		builder.append("|");
		
		for (String str : copiedList) {
			builder.append(str).append(";");
		}
		
		if (builder.toString().endsWith(";")) {
			builder.deleteCharAt(builder.length() - 1);
		}
		builder.append(")");
		return builder.toString();
		
	}
	
	public List<String> getZipCodes(String idString) {
		var removedBrackets = idString.replaceAll("|", "").replaceAll("\\)", "");
		String[] zips = removedBrackets.split(";");
		return Arrays.asList(zips);
	}
}
