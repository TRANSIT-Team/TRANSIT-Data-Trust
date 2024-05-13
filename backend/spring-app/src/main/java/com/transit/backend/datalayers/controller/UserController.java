package com.transit.backend.datalayers.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonpatch.JsonPatchException;
import com.transit.backend.datalayers.controller.abstractclasses.CrudControllerExtendAbstract;
import com.transit.backend.datalayers.controller.abstractinterfaces.CrudControllerExtend;
import com.transit.backend.datalayers.controller.assembler.UserAssembler;
import com.transit.backend.datalayers.controller.assembler.UserIdAssembler;
import com.transit.backend.datalayers.controller.dto.CompanyPropertyDTO;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserIdDTO;
import com.transit.backend.datalayers.controller.dto.registration.UserDTORegistration;
import com.transit.backend.datalayers.domain.CompanyDeliveryArea;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.service.CompanyCompanyAddressService;
import com.transit.backend.datalayers.service.CompanyDeliveryAreaService;
import com.transit.backend.datalayers.service.CompanyService;
import com.transit.backend.datalayers.service.UserService;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceExtend;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.datalayers.service.mapper.UserRegistrationMapper;
import com.transit.backend.datalayers.service.mapper.UserTransferMapper;
import com.transit.backend.helper.GetFilterExtra;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.PingService;
import com.transit.backend.rightlayers.service.RightsManageService;
import com.transit.backend.security.filterresponse.implementations.UserFilter;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import com.transit.backend.transferentities.UserRegistrationObject;
import com.transit.backend.transferentities.UserTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController extends CrudControllerExtendAbstract<UserTransferObject, UUID, UserDTO> implements CrudControllerExtend<UserDTO, UUID> {
	
	@Autowired
	private UserFilter userFilter;
	@Autowired
	private UserService service;
	@Autowired
	private UserTransferMapper mapper;
	@Autowired
	private UserAssembler userAssembler;
	@Autowired
	private PagedResourcesAssembler<UserTransferObject> pagedResourcesAssembler;
	@Autowired
	private UserRegistrationMapper userRegistrationMapper;
	@Autowired
	private UserIdAssembler userIdAssembler;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyCompanyAddressService companyAddressService;
	@Autowired
	private CompanyDeliveryAreaService companyDeliveryAreaService;
	@Autowired
	private RightsManageService rightsManageService;
	@Autowired
	private PingService pingService;
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<UserDTO> create(@RequestBody @Validated(ValidationGroups.Post.class) UserDTO dto) {
		return super.create(dto);
	}
	
	@Override
	public AbstractMapper<UserTransferObject, UserDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public CrudServiceExtend<UserTransferObject, UUID> getService() {
		return this.service;
	}
	
	@Override
	public RepresentationModelAssemblerSupport<UserTransferObject, UserDTO> getAssemblerSupport() {
		return this.userAssembler;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<UserDTO> update(@PathVariable("id") UUID primaryKey, @RequestBody @Validated(ValidationGroups.Put.class) UserDTO dto) throws ClassNotFoundException {
		return super.update(primaryKey, dto);
	}
	
	@Override
	public boolean getFilter() {
		return true;
	}
	
	@Override
	public EntityFilterHelper<UserTransferObject, User> getFilterHelper() {
		return this.userFilter;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<UserDTO> partialUpdate(@PathVariable("id") UUID primaryKey, @RequestBody JsonNode patch) throws JsonPatchException, JsonProcessingException {
		return super.partialUpdate(primaryKey, patch);
	}
	
	@Override
	public Class<UserDTO> getClazz() {
		return UserDTO.class;
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<UserDTO> readOne(@PathVariable("id") UUID primaryKey) {
		return super.readOne(primaryKey);
		
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity delete(@PathVariable("id") UUID primaryKey) {
		return super.delete(primaryKey);
	}
	
	@GetMapping("/getId/{keycloakId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<UserIdDTO> getUserIdFromAuthenticatedUser(@PathVariable("keycloakId") UUID primaryKey) {
		pingService.available();
		var id = this.service.getUserID();
		if (id.isPresent()) {
			var idDTO = this.userIdAssembler.toModel(id.get());
			return new ResponseEntity<>(idDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@GetMapping("/getSelf/{keycloakId}")
	@PreAuthorize(
			"hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).CREATOR_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).PLANNER_ORDER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).SUPPLIER.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).MANAGER_WAREHOUSE.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).WORKER_WAREHOUSE.stringValue" +
					")"
	)
	public ResponseEntity<UserDTO> getUserDTOFromAuthenticatedUser(@PathVariable("keycloakId") UUID primaryKey) {
		pingService.available();
		var user = this.service.getSelfUserDTO();
		if (user.isPresent()) {
			var userDTO = getAssemblerSupport().toModel(user.get());
			return new ResponseEntity<>(userDTO, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/registration")
	@PreAuthorize(
			"@securityRegistrationService.hasAnyAuthority()"
	)
	public ResponseEntity<UserDTO> register(@RequestBody @Validated(ValidationGroups.Post.class) UserDTORegistration dto) {
		pingService.available();
		CompanyPropertyDTO companyProperty = new CompanyPropertyDTO();
		companyProperty.setKey("default");
		companyProperty.setValue("default");
		companyProperty.setType("default");
		dto.getCompany().getCompanyProperties().add(companyProperty);
		UserRegistrationObject entity = userRegistrationMapper.toEntity(dto);
		UserTransferObject responseObject = this.service.createOwnerWithCompany(entity);
		var companyId = responseObject.getUser().getCompany().getId();
		rightsManageService.registerCompany(companyId);
		
		rightsManageService.createEntityAndConnectIt(responseObject.getUser().getId(), responseObject.getUser().getClass().getSimpleName(), responseObject.getUser().getClass());
		//	generateEntryRight.generateEntry(responseObject.getUser().getId(), responseObject.getUser().getClass().getSimpleName());
		if (responseObject.getUser().getUserProperties() != null && !responseObject.getUser().getUserProperties().isEmpty()) {
			//responseObject.getUser().getUserProperties().forEach(prop -> generateEntryRight.generateEntry(prop.getId(), prop.getClass().getSimpleName()));
			responseObject.getUser().getUserProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
		}
		var userCompany = companyService.readOne(responseObject.getUser().getCompany().getId()).get();
		//generateEntryRight.generateEntry(userCompany.getId(), userCompany.getClass().getSimpleName());
		rightsManageService.createEntityAndConnectIt(userCompany.getId(), userCompany.getClass().getSimpleName(), userCompany.getClass());
		if (userCompany.getCompanyProperties() != null && !userCompany.getCompanyProperties().isEmpty()) {
			//	userCompany.getCompanyProperties().forEach(prop -> generateEntryRight.generateEntry(prop.getId(), prop.getClass().getSimpleName()));
			userCompany.getCompanyProperties().forEach(prop -> rightsManageService.createEntityAndConnectIt(prop.getId(), prop.getClass().getSimpleName(), prop.getClass()));
		}
		var companyAddresses = companyAddressService.read(userCompany.getId(), "deleted==false", GetFilterExtra.getEmptyFilterExtra());
		
		if (companyAddresses != null && !companyAddresses.isEmpty()) {
			//companyAddresses.forEach(address -> generateEntryRight.generateEntry(address.getId().getAddressId(), address.getClass().getSimpleName()));
			companyAddresses.forEach(address -> rightsManageService.createEntityAndConnectIt(address.getId().getAddressId(), address.getAddress().getClass().getSimpleName(), address.getAddress().getClass()));
		}
		var deliveryArea = companyDeliveryAreaService.read(userCompany.getId(), "deleted==false", GetFilterExtra.getEmptyFilterExtra());
		//	deliveryArea.forEach(address -> generateEntryRight.generateEntry(address.getId(), CompanyDeliveryArea.class.getSimpleName()));
		deliveryArea.forEach(address -> rightsManageService.createEntityAndConnectIt(address.getId(), CompanyDeliveryArea.class.getSimpleName(), CompanyDeliveryArea.class));
		
		
		var userDTO = getAssemblerSupport().toModel(responseObject);
		
		
		return ResponseEntity
				.created(userDTO.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
				.body(userDTO);
	}
	
	@Override
	@PreAuthorize(
			"@securityEntityService.hasAnyAuthority(" +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_GLOBAL.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).OWNER_COMPANY.stringValue," +
					"T(com.transit.backend.security.authmodel.TransitAuthorities).ADMIN_COMPANY.stringValue" +
					")"
	)
	public ResponseEntity<PagedModel<UserDTO>> read(Pageable pageable, @RequestParam(name = "filter", defaultValue = "deleted==false;userProperties.deleted==false") String query,
	                                                @RequestParam(name = "createdByMyCompany", defaultValue = "false") boolean createdByMyCompany, @RequestParam(name = "skip", defaultValue = "-1") int skip,
	                                                @RequestParam(name = "take", defaultValue = "0") int take,
	                                                @RequestParam(name = "extraFilterParameters", defaultValue = "") String extraFilterParameters) {
		return super.read(GetFilterExtra.getPageableExtras(pageable, skip, take, extraFilterParameters), query, createdByMyCompany);
	}
	
	
}