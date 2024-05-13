package com.transit.backend.datalayers.service.mapper;

public interface AbstractMapper<test, testDTO> {
	
	test toEntity(testDTO dto);
	
	testDTO toDto(test entity);
	
}
