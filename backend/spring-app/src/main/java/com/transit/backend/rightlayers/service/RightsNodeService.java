package com.transit.backend.rightlayers.service;


import java.util.UUID;

public interface RightsNodeService {
	
	String readEntityCLass(UUID primaryKey);
	
	
	Boolean nodeMultipleOutgoingEdges(UUID primaryKey);
	
}
