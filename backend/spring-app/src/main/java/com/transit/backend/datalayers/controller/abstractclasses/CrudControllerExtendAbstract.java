package com.transit.backend.datalayers.controller.abstractclasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.UpdateExtraEntries;
import com.transit.backend.datalayers.controller.dto.abstractclasses.BaseTypeDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.QCompanyIDToCompanyOID;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractParentEntity;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.rightlayers.service.RightsNodeService;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.security.preauthaccess.GetFilterExpression;
import com.transit.backend.transferentities.FilterExtra;
import com.transit.backend.transferentities.UserTransferObject;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Slf4j
public abstract class CrudControllerExtendAbstract<test, Key, testDTO extends BaseTypeDTO<testDTO>> {
	private static final boolean checkUpdateSecurity = true;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PagedResourcesAssembler<test> pagedResourcesAssembler;
	@Autowired
	private GetFilterExpression getFilterExpression;
	@Autowired
	private UpdateExtraEntries<test> updateExtraEntries;
	@Autowired
	private RightsManageService rightsManageService;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	@Autowired
	private RightsNodeService rightsNodeService;
	@Autowired
	private PingService pingService;
	
	public ResponseEntity<testDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) testDTO dto) {
		pingService.available();
		test entity = getMapper().toEntity(dto);
		entity = getService().create(entity);
		var testDTO = getAssemblerSupport().toModel(entity);
		if (entity instanceof AbstractEntity entityRights) {
			rightsManageService.createEntityAndConnectIt(entityRights.getId(), entity.getClass().getSimpleName(), entity.getClass());
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
				.created(testDTO.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(testDTO);
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public abstract CrudServiceExtend<test, Key> getService();
	
	public abstract RepresentationModelAssemblerSupport<test, testDTO> getAssemblerSupport();
	
	public ResponseEntity<testDTO> update(Key primaryKey, testDTO dto) throws ClassNotFoundException {
		pingService.available();
		test entity = getMapper().toEntity(dto);
		//Filter das nicht Properties aktualisiert werden, falls eine ENtität getietl wurde, geht dann nur über den dedizierten Properties Endpunkt
		if (checkUpdateSecurity) {
			checkUpdate(primaryKey, entity);
		}
		entity = getService().update(primaryKey, entity);
		updateExtraEntries.updateExtraEntries(entity);
		if (getFilter()) {
			entity = getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(entity)));
		}
		return new ResponseEntity<>(getAssemblerSupport().toModel(entity), HttpStatus.OK);
	}
	
	private void checkUpdate(Key primaryKey, test entity) {
		var resultOldProperties = getService().readOne(primaryKey);
		if (resultOldProperties.isEmpty()) {
			throw new NoSuchElementFoundException(getClass().getSimpleName(), (UUID) primaryKey);
		}
		
		if (entity instanceof AbstractParentEntity<?> propertiesParent) {
			Optional<AccessResponseDTO> rightsEntry = Optional.empty();
			if (entity.getClass().getSimpleName().equals(Company.class.getSimpleName())) {
				var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
				rightsEntry = this.rightsService.getAccess(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
			} else {
				rightsEntry = this.rightsService.getAccess((UUID) primaryKey);
			}
			var resultOldTemp = getService().readOne(primaryKey);
			
			AbstractParentEntity<?> resultOld;
			if (resultOldTemp.isEmpty()) {
				throw new NoSuchElementFoundException(getClass().getSimpleName(), (UUID) primaryKey);
			}
			resultOld = (AbstractParentEntity<?>) resultOldTemp.get();
			boolean isChared = true;
			try {
				isChared = rightsNodeService.nodeMultipleOutgoingEdges((UUID) primaryKey);
			} catch (Exception ex) {
				var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
				isChared = rightsNodeService.nodeMultipleOutgoingEdges(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
			}
			if (rightsEntry.isPresent() && isChared
			) {
				String failureText = "Cannot change here properties because of security";
				var properties = propertiesParent.getProperties();
				if (resultOld.getProperties() != null && (properties.isEmpty())) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if ((resultOld.getProperties() == null || resultOld.getProperties().isEmpty()) && properties != null) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if (resultOld.getProperties() != null && properties != null && properties.size() != resultOld.getProperties().size()) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if (properties != null && resultOld.getProperties() != null) {
					for (var prop : resultOld.getProperties()) {
						var propNewOpt = properties.stream().filter(propNew -> propNew.getKey().equals(prop.getKey())).findFirst();
						if (propNewOpt.isEmpty()) {
							throw new UnprocessableEntityExeption(failureText);
						} else {
							if (!prop.getValue().equals(propNewOpt.get().getValue()) || !prop.getType().equals(propNewOpt.get().getType())) {
								throw new UnprocessableEntityExeption(failureText);
							}
						}
					}
				}
			}
		}
		if (entity instanceof UserTransferObject propertiesParent) {
			var rightsEntry = this.rightsService.getAccess((UUID) primaryKey);
			var resultOldTemp = getService().readOne(primaryKey);
			UserTransferObject resultOld;
			if (resultOldTemp.isEmpty()) {
				throw new NoSuchElementFoundException(getClass().getSimpleName(), (UUID) primaryKey);
			}
			resultOld = (UserTransferObject) resultOldTemp.get();
			if (rightsEntry.isPresent() && rightsNodeService.nodeMultipleOutgoingEdges((UUID) primaryKey)
			) {
				String failureText = "Cannot change here properties because of security";
				var properties = propertiesParent.getUser().getProperties();
				//Remove ==null because this is possible by field Access
				if (resultOld.getUser().getProperties() != null && (properties.isEmpty())) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if ((resultOld.getUser().getProperties() == null || resultOld.getUser().getProperties().isEmpty()) && properties != null) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if (resultOld.getUser().getProperties() != null && properties != null && properties.size() != resultOld.getUser().getProperties().size()) {
					throw new UnprocessableEntityExeption(failureText);
				}
				if (properties != null && resultOld.getUser().getProperties() != null) {
					for (var prop : resultOld.getUser().getProperties()) {
						var propNewOpt = properties.stream().filter(propNew -> propNew.getKey().equals(prop.getKey())).findFirst();
						if (propNewOpt.isEmpty()) {
							throw new UnprocessableEntityExeption(failureText);
						} else {
							if (!prop.getValue().equals(propNewOpt.get().getValue()) || !prop.getType().equals(propNewOpt.get().getType())) {
								throw new UnprocessableEntityExeption(failureText);
							}
						}
					}
				}
			}
			
		}
	}
	
	public abstract boolean getFilter();
	
	public abstract EntityFilterHelper<test, ?> getFilterHelper();
	
	public ResponseEntity<testDTO> partialUpdate(Key primaryKey, JsonNode node) throws JsonPatchException, JsonProcessingException {
		pingService.available();
		//Prüfe davor
		if (checkUpdateSecurity) {
			checkPatch(primaryKey, node);
		}
		if (node.get("packagesPrice") != null) {
			((ObjectNode) node).put("packagesPrice", node.get("packagesPrice").asText().replaceAll("\"", "").replaceAll(",", "."));
		}
		if (node.get("price") != null) {
			((ObjectNode) node).put("price", node.get("price").asText().replaceAll("\"", "").replaceAll(",", "."));
		}
		if (node.get("outsourceCost") != null) {
			((ObjectNode) node).put("outsourceCost", node.get("outsourceCost").asText().replaceAll("\"", "").replaceAll(",", "."));
		}
		if (node.get("orderAltPrice") != null) {
			((ObjectNode) node).put("orderAltPrice", node.get("orderAltPrice").asText().replaceAll("\"", "").replaceAll(",", "."));
		}
		if (node.get("packagePrice") != null) {
			((ObjectNode) node).put("packagePrice", node.get("packagePrice").asText().replaceAll("\"", "").replaceAll(",", "."));
		}
		
		var patch = JsonMergePatch.fromJson(node);
		
		test entity = getService().partialUpdate(primaryKey, patch);
		updateExtraEntries.updateExtraEntries(entity);
		if (getFilter()) {
			entity = getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(entity)));
		}
		
		return new ResponseEntity<>(getAssemblerSupport().toModel(entity), HttpStatus.OK);
	}
	
	private void checkPatch(Key primaryKey, JsonNode node) {
		Optional<AccessResponseDTO> rightsEntry = Optional.empty();
		
		try {
			rightsEntry = this.rightsService.getAccess((UUID) primaryKey);
		} catch (Exception ex) {
			var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
			rightsEntry = this.rightsService.getAccess(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
		}
		boolean isChared;
		try {
			isChared = rightsNodeService.nodeMultipleOutgoingEdges((UUID) primaryKey);
		} catch (Exception ex) {
			var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
			isChared = rightsNodeService.nodeMultipleOutgoingEdges(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
		}
		if (rightsEntry.isPresent() && isChared) {
			if (node.has("carProperties") ||
					node.has("companyProperties") ||
					node.has("orderProperties") ||
					node.has("orderTypeProperties") ||
					node.has("packagePackageProperties") ||
					node.has("paymentProperties") ||
					node.has("paymentStatusProperties") ||
					node.has("userProperties") ||
					node.has("warehouseProperties")
			) {
				String failureText = "Cannot change here properties because of security";
				throw new UnprocessableEntityExeption(failureText);
			}
		}
		
	}
	
	public ResponseEntity<PagedModel<testDTO>> read(FilterExtra pageable, String query, boolean createdByMyCompany) {
		pingService.available();
		var random = new Random().nextInt();
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		
		PagedModel<testDTO> pages;
		if (getFilter()) {
			var newQuery = getFilterExpression.overwriteQueryWithEntityId(query, createdByMyCompany);
			if (newQuery == null) {
				pages = (PagedModel<testDTO>) pagedResourcesAssembler.toEmptyModel(Page.empty(), getClazz());
			} else {
				
				Page<test> page = getService().read(pageable, QueryRewrite.queryRewriteAll(newQuery));
				
				List<UUID> ids = new ArrayList<>();
				page.forEach(entity -> ids.addAll(getFilterHelper().collectIDs(entity)));
				
				var rights = rightsService.getAccessList(new HashSet<>(ids));
				
				page = page.map(entity -> getFilterHelper().filterEntities(entity, getFilterHelper().filterEntitiesCompanyId(), rights));
				
				if (page.hasContent()) {
					pages = pagedResourcesAssembler.toModel(page, getAssemblerSupport());
					
				} else {
					pages = (PagedModel<testDTO>) pagedResourcesAssembler.toEmptyModel(page, getClazz());
					
				}
			}
		} else {
			Page<test> page = getService().read(pageable, QueryRewrite.queryRewriteAll(query));
			if (page.hasContent()) {
				pages = pagedResourcesAssembler.toModel(page, getAssemblerSupport());
			} else {
				pages = (PagedModel<testDTO>) pagedResourcesAssembler.toEmptyModel(page, getClazz());
			}
		}
		//Response
		return new ResponseEntity<>(pages, HttpStatus.OK);
		
	}
	
	public abstract Class<testDTO> getClazz();
	
	
	//Update Possible when not shared
	
	public ResponseEntity<testDTO> readOne(Key primaryKey) {
		pingService.available();
		Optional<test> test = getService().readOne(primaryKey);
		if (getFilter() && test.isPresent()) {
			test = Optional.of(getFilterHelper().filterEntities(test.get(), getFilterHelper().filterEntitiesCompanyId(), rightsService.getAccessList(getFilterHelper().collectIDs(test.get()))));
		}
		
		if (test.isPresent()) {
			var testDTO = getAssemblerSupport().toModel(test.get());
			return new ResponseEntity<>(testDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	public ResponseEntity delete(Key primaryKey) {
		pingService.available();
		getService().delete(primaryKey);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
}
