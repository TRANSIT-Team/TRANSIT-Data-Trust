package com.transit.backend.security.authmodel;

import org.springframework.lang.Nullable;

import java.util.Arrays;


public enum TransitAuthorities {
	
	
	ADMIN_GLOBAL(0, "adminGlobal"),
	OWNER_COMPANY(1, "ownerCompany"),
	ADMIN_COMPANY(2, "adminCompany"),
	
	CREATOR_ORDER(3, "creatorOrder"),
	PLANNER_ORDER(4, "plannerOrder"),
	SUPPLIER(5, "supplier"),
	MANAGER_WAREHOUSE(6, "managerWarehouse"),
	WORKER_WAREHOUSE(7, "workerWarehouse"),
	REGISTRATION(8, "empty");
	
	public static final String CAN_CHANGE_ROLES = "CAN_CHANGE_ROLES";
	public static final String CAN_CHANGE_ALL_ROLES = "CAN_CHANGE_ALL_ROLES";
	public static final String CAN_CHANGE_COMPANY_ROLES = "CAN_CHANGE_COMPANY_ROLES";
	
	
	private static final TransitAuthorities[] VALUES;
	
	static {
		VALUES = values();
	}
	
	public final String stringValue;
	private final int value;
	
	TransitAuthorities(int value, String stringValue) {
		this.value = value;
		this.stringValue = stringValue;
	}
	
	public static TransitAuthorities valueOf(int statusCode) {
		TransitAuthorities status = resolve(statusCode);
		if (status == null) {
			throw new IllegalArgumentException("No matching constant for [" + statusCode + "]");
		}
		return status;
	}
	
	@Nullable
	public static TransitAuthorities resolve(int statusCode) {
		// Use cached VALUES instead of values() to prevent array allocation.
		for (TransitAuthorities status : VALUES) {
			if (status.value == statusCode) {
				return status;
			}
		}
		return null;
	}
	
	public static TransitAuthorities getAuthorityForString(String role) {
		for (var x : TransitAuthorities.VALUES) {
			if (x.getStringValue().equals(role)) {
				return x;
			}
		}
		return null;
	}
	
	public int value() {
		return this.value;
	}
	
	@Override
	public String toString() {
		return this.stringValue;
	}
	
	public String[] getStringValues() {
		return Arrays.stream(TransitAuthorities.VALUES).filter(auth -> !auth.equals(TransitAuthorities.REGISTRATION)).map(TransitAuthorities::getStringValue).toList().toArray(new String[0]);
	}
	
	public String getStringValue() {
		return this.stringValue;
	}
	
	
}
