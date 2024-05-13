package com.transit.backend.datalayers.controller.abstractclasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.UpdateExtraEntries;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerSubResource;
import com.transit.backend.datalayers.controller.assembler.wrapper.abstractclasses.AssemblerWrapperAbstract;
import com.transit.backend.datalayers.controller.dto.abstractclasses.AbstractDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.CompanyAddress;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import com.transit.backend.transferentities.FilterExtra;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;


@Component
public abstract class CrudControllerSubRessourceNToMAbstract<test, testDTO extends AbstractDTO<testDTO>, testRoot, testRootDTO, rootController extends CrudControllerExtend<testRootDTO, UUID>, nestedController extends CrudControllerSubResource<testDTO, UUID, UUID>> {
	
	@Autowired
	GetFilterExpression getFilterExpression;
	@Autowired
	private UpdateExtraEntries<test> updateExtraEntries;
	@Autowired
	private RightsManageService rightsManageService;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private PingService pingService;
	
	public ResponseEntity<testDTO> create(UUID rootId, testDTO dto) {
		pingService.available();
		test entity = getMapper().toEntity(dto);
		entity = getService().create(rootId, entity);
		var packagePropertyDTO = getAssemblerWrapper().toModel(entity, rootId, true);
		if (entity instanceof AbstractEntity entityRights) {
			
			
			rightsManageService.createEntityAndConnectIt(entityRights.getId(), entity.getClass().getSimpleName(), entity.getClass());
		}
		if (entity instanceof CompanyAddress entityRights) {
			
			
			rightsManageService.createEntityAndConnectIt(entityRights.getId().getAddressId(), entity.getClass().getSimpleName(), entity.getClass());
		}
		if (entity instanceof AbstractParentEntity<?> propertyRights) {
			if (propertyRights.getProperties() != null && !propertyRights.getProperties().isEmpty()) {
				propertyRights.getProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
			}
		}
		if (entity instanceof UserTransferObject entityRights) {
			rightsManageService.createEntityAndConnectIt(entityRights.getUser().getId(), User.class.getSimpleName(), User.class);
			if (entityRights.getUser().getProperties() != null && !entityRights.getUser().getProperties().isEmpty()) {
				entityRights.getUser().getProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
			}
		}
		if (entity instanceof Car entityRights) {
			if (entityRights.getLocations() != null && !entityRights.getLocations().isEmpty()) {
				entityRights.getLocations().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
			}
		}
		
		
		return ResponseEntity
				.created(packagePropertyDTO.getRequiredLink(IanaLinkRelations.SELF).toUri())
				.body(packagePropertyDTO);
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public abstract CrudServiceSubRessource<test, UUID, UUID> getService();
	
	public abstract AssemblerWrapperAbstract<test, testDTO, testRoot, testRootDTO, rootController, nestedController> getAssemblerWrapper();
	
	public ResponseEntity<testDTO> update(
			@PathVariable UUID rootId, UUID id, testDTO dto) throws ClassNotFoundException {
		pingService.available();
		test entity = getMapper().toEntity(dto);
		Optional<AccessResponseDTO> rightsEntry = this.rightsService.getAccess(id);
		entity = getService().update(rootId, id, entity);
		updateExtraEntries.updateExtraEntries(entity);
		if (getFilter()) {
			entity = getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(entity)));
		}
		return new ResponseEntity<>(getAssemblerWrapper().toModel(entity, rootId, true), HttpStatus.OK);
	}
	
	public abstract boolean getFilter();
	
	public abstract EntityFilterHelper<test, ?> getFilterHelper();
	
	public ResponseEntity<testDTO> partialUpdate(@PathVariable UUID rootId, @PathVariable UUID id, JsonNode node) throws JsonPatchException, JsonProcessingException {
		pingService.available();
		var patch = JsonMergePatch.fromJson(node);
		test entity = getService().partialUpdate(rootId, id, patch);
		updateExtraEntries.updateExtraEntries(entity);
		if (getFilter()) {
			entity = getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(entity)));
		}
		return new ResponseEntity<>(getAssemblerWrapper().toModel(entity, rootId, true), HttpStatus.OK);
	}
	
	public ResponseEntity<CollectionModel<testDTO>> read(UUID rootId, String query, FilterExtra collectionFilterExtra) {
		pingService.available();
		CollectionModel<testDTO> pages;
		if (getFilter()) {
			String newQuery;
			if (getCLazz().isAssignableFrom(CompanyAddress.class)) {
				newQuery = getFilterExpression.overwriteQueryWithEntityId(query, "address", collectionFilterExtra.isCreatedByMyCompany());
			} else {
				newQuery = getFilterExpression.overwriteQueryWithEntityId(query, collectionFilterExtra.isCreatedByMyCompany());
			}
			
			if (newQuery == null) {
				pages = getAssemblerWrapper().toCollectionModel(new ArrayList<>(), rootId, true);
			} else {
				Collection<test> collection = getService().read(rootId, QueryRewrite.queryRewriteAll(newQuery), collectionFilterExtra);
				
				List<UUID> ids = new ArrayList<>();
				collection.forEach(entity -> ids.addAll(getFilterHelper().collectIDs(entity)));
				var rights = rightsService.getAccessList(new HashSet<>(ids));
				collection = collection.stream().map(entity -> getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rights)).toList();
				pages = getAssemblerWrapper().toCollectionModel(collection, rootId, true);
			}
		} else {
			Collection<test> collection = getService().read(rootId, QueryRewrite.queryRewriteAll(query), collectionFilterExtra);
			pages = getAssemblerWrapper().toCollectionModel(collection, rootId, true);
		}
		return new ResponseEntity<>(pages, HttpStatus.OK);
	}
	
	public abstract Class<test> getCLazz();
	
	public ResponseEntity<testDTO> readOne(UUID rootId, UUID id) {
		pingService.available();
		Optional<test> properties = getService().readOne(rootId, id);
		if (getFilter() && properties.isPresent()) {
			properties = Optional.of(getFilterHelper().filterEntities(properties.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(properties.get()))));
		}
		if (properties.isPresent()) {
			var propertiesDTO = getAssemblerWrapper().toModel(properties.get(), rootId, true);
			
			return new ResponseEntity<>(propertiesDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	
	public ResponseEntity delete(UUID rootId, UUID id) {
		pingService.available();
		getService().delete(rootId, id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
}
