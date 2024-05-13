package com.transit.backend.rightlayers.service.helper;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultListHelper {
	
	
	public static List<String> getListIdAndOrderStatus() {
		var list = new ArrayList<String>();
		list.add("id");
		list.add("orderStatus");
		return list;
	}
	
	
	public static List<String> getListId() {
		var list = new ArrayList<String>();
		list.add("id");
		return list;
	}
	
	
	public static List<String> getEmptyList() {
		var list = new ArrayList<String>();
		return list;
	}
	
}
