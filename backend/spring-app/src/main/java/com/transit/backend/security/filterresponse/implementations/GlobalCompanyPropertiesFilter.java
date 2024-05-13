package com.transit.backend.security.filterresponse.implementations;

import com.transit.backend.datalayers.domain.GlobalCompanyProperties;
import com.transit.backend.security.filterresponse.abstractclasses.AbstractEntityFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import org.springframework.stereotype.Component;

@Component
public class GlobalCompanyPropertiesFilter extends AbstractEntityFilter<GlobalCompanyProperties, GlobalCompanyProperties> implements EntityFilterHelper<GlobalCompanyProperties, GlobalCompanyProperties> {
	@Override
	public GlobalCompanyProperties transformToTransfer(GlobalCompanyProperties entity) {
		return entity;
	}
	
	@Override
	public GlobalCompanyProperties transformToEntity(GlobalCompanyProperties entity) {
		return entity;
	}
	
	@Override
	public GlobalCompanyProperties transformToTransfer(GlobalCompanyProperties entity, GlobalCompanyProperties entityOld) {
		return entity;
	}
	
	@Override
	public Class<GlobalCompanyProperties> getClazz() {
		return GlobalCompanyProperties.class;
	}
	
	@Override
	public String getPathToEntity(GlobalCompanyProperties entity, GlobalCompanyProperties entity2) {
		return "/globalcompanyproperties/" + entity.getId();
	}
	
	
}
