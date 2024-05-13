package com.transit.backend.datalayers.service.impl;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.config.Constants;
import com.transit.backend.datalayers.controller.dto.UserDTO;
import com.transit.backend.datalayers.controller.dto.UserPropertyDTO;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.QUser;
import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.domain.UserProperty;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.CompanyRepository;
import com.transit.backend.datalayers.repository.UserPropertyRepository;
import com.transit.backend.datalayers.repository.UserRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.CompanyService;
import com.transit.backend.datalayers.service.UserService;
import com.transit.backend.datalayers.service.UserUserPropertyService;
import com.transit.backend.datalayers.service.abstractclasses.CrudServiceExtendPropertyAbstract;
import com.transit.backend.datalayers.service.abstractinterfaces.CrudServiceSubRessource;
import com.transit.backend.datalayers.service.helper.DeleteHelper;
import com.transit.backend.datalayers.service.helper.KeycloakRolesManagement;
import com.transit.backend.datalayers.service.mapper.*;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.security.authmodel.CheckRightsMatrix;
import com.transit.backend.security.authmodel.KeycloakServiceManager;
import com.transit.backend.security.authmodel.TransitAuthorities;
import com.transit.backend.security.oauth2.SecurityUtils;
import com.transit.backend.transferentities.FilterExtra;
import com.transit.backend.transferentities.UserIdTransfer;
import com.transit.backend.transferentities.UserRegistrationObject;
import com.transit.backend.transferentities.UserTransferObject;
import org.apache.commons.collections4.CollectionUtils;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.validation.Validator;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.regex.Matcher;


/**
 * Noch zu erledigen
 * ACTION EMAILS
 * und selbe FIrma
 * <p>
 * //Bei Suchen company mit einschränken
 **/
@Service
public class UserServiceBean extends CrudServiceExtendPropertyAbstract<User, UserDTO, UserProperty, UserPropertyDTO> implements UserService {
	private final RealmResource realmResource;
	private final UsersResource usersResource;
	@Inject
	Validator validator;
	@Autowired
	UserTransferMapper userRepresentationMapper;
	@Autowired
	private UserRepository repository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private UserMapper mapper;
	@Autowired
	private UserPropertyRepository propertiesRepository;
	@Autowired
	private UserPropertyMapper userPropertyMapper;
	@Autowired
	private UserUserPropertyService userUserPropertyService;
	@Autowired
	private CompanyRepository companyRepository;
	@Autowired
	private CompanyMapper companyMapper;
	@Autowired
	private CompanyService companyService;
	@Autowired
	private CompanyCompanyAddressServiceBean companyCompanyAddressServiceBean;
	@Autowired
	private CheckRightsMatrix checkRightsMatrix;
	@Autowired
	private KeycloakRolesManagement keycloakRolesManagement;
	
	@Autowired
	private DeleteHelper deleteHelper;
	
	public UserServiceBean(@Autowired KeycloakServiceManager keycloakServiceManager) {
		this.realmResource = keycloakServiceManager.getRealmResource();
		this.usersResource = keycloakServiceManager.getUsersResource();
	}
	
	@Override
	public UserTransferObject createOwnerWithCompany(UserRegistrationObject entity) {
		return createOwnerWithCompanyInternal(entity);
	}
	
	@Override
	public Optional<UserIdTransfer> getUserID() {
		return this.repository.findByKeycloakId(SecurityUtils.extractId(SecurityContextHolder.getContext().getAuthentication())).map(user -> new UserIdTransfer(user.getId()));
		
	}
	
	@Override
	public Optional<UserTransferObject> getSelfUserDTO() {
		var keycloakId = SecurityUtils.extractId(SecurityContextHolder.getContext().getAuthentication());
		var id = this.repository.findByKeycloakId(keycloakId).map(AbstractEntity::getId);
		if (id.isPresent()) {
			return this.readOne(id.get());
		} else {
			return Optional.of(new UserTransferObject(keycloakRolesManagement.getUserRepresentation(keycloakId), null));
		}
		
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public UserTransferObject createOwnerWithCompanyInternal(UserRegistrationObject entity) {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var keycloakId = SecurityUtils.extractId(authentication);
		var userKeycloak = keycloakRolesManagement.getUserRepresentation(keycloakId);
		var realmRolesKeycloak = checkRightsMatrix.getAuthoritiesForString(userKeycloak.getRealmRoles());
		realmRolesKeycloak.add(TransitAuthorities.REGISTRATION);
		checkRoles(new UserTransferObject(entity.getUserRepresentation(), entity.getUser()), realmRolesKeycloak);
		realmRolesKeycloak.remove(TransitAuthorities.REGISTRATION);
		entity.getUserRepresentation().getRealmRoles().add(TransitAuthorities.OWNER_COMPANY.getStringValue());
		entity.getUserRepresentation().setRealmRoles(new ArrayList<>(new HashSet<>(entity.getUserRepresentation().getRealmRoles())));
		entity.setUserRepresentation(keycloakRolesManagement.updateKeycloakUserRepresentation(entity.getUserRepresentation()));
		entity.getUser().setKeycloakEmail(entity.getUserRepresentation().getEmail());
		entity.setUser(super.createInternal(entity.getUser()));
		entity.setUser(super.saveInternal(entity.getUser()));
		entity.getUser().setCreatedBy(entity.getUserRepresentation().getEmail());
		entity.getUser().setLastModifiedBy(entity.getUserRepresentation().getEmail());
		entity.getUser().getUserProperties().forEach(prop -> prop.setCreatedBy(entity.getUserRepresentation().getEmail()));
		entity.getUser().getUserProperties().forEach(prop -> prop.setLastModifiedBy(entity.getUserRepresentation().getEmail()));
		entity.setUser(super.saveInternal(entity.getUser()));
		entity.setCompany(companyService.create(entity.getCompany()));
		entity.getUser().setCompany(entity.getCompany());
		entity.setUser(super.saveInternal(entity.getUser()));
		entity.setCompanyAddresses(entity.getCompanyAddresses().stream().map(cA -> companyCompanyAddressServiceBean.create(entity.getCompany().getId(), cA)).toList());
		//executeActionMails(Constants.CREATE_ACTION_EMAILS, this.usersResource.get(entity.getUserRepresentation().getId()));
		return new UserTransferObject(entity.getUserRepresentation(), entity.getUser());
	}
	
	private void checkRoles(UserTransferObject entity, List<TransitAuthorities> realmRolesKeycloak) {
		boolean canGiveAllRoles = true;
		for (var newRealmRole : entity.getUserRepresentation().getRealmRoles()) {
			if (!checkRightsMatrix.canGiveRole(newRealmRole, realmRolesKeycloak)) {
				canGiveAllRoles = false;
			}
		}
		if (!canGiveAllRoles) {
			throw new UnprocessableEntityExeption("Cannot Create Owner Company with this Roles.");
		}
	}
	
	@Override
	public UserTransferObject create(UserTransferObject entity) {
		if (!this.usersResource.search(entity.getUserRepresentation().getUsername()).isEmpty()) {
			throw new UnprocessableEntityExeption("Username already exists");
		}
		
		
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var keycloakId = SecurityUtils.extractId(authentication);
		var userKeycloak = keycloakRolesManagement.getUserRepresentation(keycloakId);
		var realmRolesKeycloak = checkRightsMatrix.getAuthoritiesForString(userKeycloak.getRealmRoles());
		var company = companyRepository.findByIdAndDeleted(entity.getUser().getCompany().getId(), false);
		if (company.isEmpty()) {
			throw new NoSuchElementFoundException(Company.class.getSimpleName(), entity.getUser().getCompany().getId());
		}
		if (realmRolesKeycloak.isEmpty() && company.get().getCompanyUsers().isEmpty() && entity.getUser().getKeycloakId().equals(keycloakId)) {
			throw new UnprocessableEntityExeption("To Register new User with Company go to /users/registration");
		}
		var authentificatedUser = repository.findByKeycloakId(keycloakId);
		if (authentificatedUser.isPresent()) {
			if (entity.getUser().getCompany().getId().equals(authentificatedUser.get().getCompany().getId())) {
				checkRoles(entity, realmRolesKeycloak);
				entity.getUser().setCompany(findCompany(entity.getUser().getCompany()));
				entity.setUserRepresentation(keycloakRolesManagement.createKeycloakUserRepresentation(entity.getUserRepresentation()));
				entity.getUser().setKeycloakId(UUID.fromString(entity.getUserRepresentation().getId()));
				entity.getUser().setCreateDate(OffsetDateTime.ofInstant(Instant.ofEpochMilli(entity.getUserRepresentation().getCreatedTimestamp()), TimeZone.getDefault().toZoneId()));
				entity.setUser(super.createInternal(entity.getUser()));
				entity.getUser().setCompany(findCompany(entity.getUser().getCompany()));
				
				entity.setUser(super.saveInternal(entity.getUser()));
				
				try {
					executeActionMails(Constants.CREATE_ACTION_EMAILS, this.usersResource.get(entity.getUserRepresentation().getId()));
				} catch (Exception e) {
					
					entity.getUserRepresentation().setEnabled(true);
					this.usersResource.get(entity.getUserRepresentation().getId()).update(entity.getUserRepresentation());
					
				}
				return entity;
				
			} else {
				throw new UnprocessableEntityExeption("This is not your own Company.");
			}
		} else {
			throw new UnprocessableEntityExeption("Cannot find authetificated User with keycloakId.");
		}
		
	}
	
	@Override
	public UserTransferObject update(UUID userId, UserTransferObject entity) {
		
		var userOld = repository.findByIdAndDeleted(userId, false);
		if (userOld.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(User.class.getSimpleName(), userId);
		}
		if (!entity.getUser().getCompany().getId().equals(userOld.get().getCompany().getId())) {
			throw new UnprocessableEntityExeption("Cannot change CompanyId");
		}
		
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var keycloakId = SecurityUtils.extractId(authentication);
		var userKeycloak = keycloakRolesManagement.getUserRepresentation(keycloakId);
		var realmRolesKeycloak = checkRightsMatrix.getAuthoritiesForString(keycloakRolesManagement.getUserRepresentation(keycloakId).getRealmRoles());
		
		var authentificatedUser = repository.findByKeycloakId(keycloakId);
		
		
		if (keycloakId.equals(entity.getUser().getKeycloakId())) {
			if (CollectionUtils.isEqualCollection(userKeycloak.getRealmRoles(), entity.getUserRepresentation().getRealmRoles())) {
				entity.setUserRepresentation(keycloakRolesManagement.updateKeycloakUserRepresentation(entity.getUserRepresentation()));
				updateUserInternal(userId, entity);
				return entity;
			} else {
				checkRoles(entity, realmRolesKeycloak);
				entity.getUser().setCompany(findCompany(entity.getUser().getCompany()));
				entity.setUserRepresentation(keycloakRolesManagement.updateKeycloakUserRepresentation(entity.getUserRepresentation()));
				updateUserInternal(userId, entity);
				return entity;
			}
		} else {
			if (authentificatedUser.isPresent()) {
				if (entity.getUser().getCompany().getId().equals(authentificatedUser.get().getCompany().getId())) {
					checkRoles(entity, realmRolesKeycloak);
					
					if (!entity.getUser().equals(userOld.get())) {
						throw new UnprocessableEntityExeption("Can only update Roles");
					}
					//only update roles
					keycloakRolesManagement.updateOnlyRoles(entity.getUserRepresentation());
					entity.setUserRepresentation(keycloakRolesManagement.getUserRepresentation(UUID.fromString(entity.getUserRepresentation().getId())));
					
					try {
						executeActionMails(Constants.UPDATE_ACTION_EMAILS, this.usersResource.get(entity.getUserRepresentation().getId()));
					} catch (Exception e) {
						
						entity.getUserRepresentation().setEnabled(true);
						this.usersResource.get(entity.getUserRepresentation().getId()).update(entity.getUserRepresentation());
						executeActionMails(Constants.UPDATE_ACTION_EMAILS, this.usersResource.get(entity.getUserRepresentation().getId()));
						
					}
					return entity;
				} else {
					throw new UnprocessableEntityExeption("No Access for this modification.");
				}
			} else {
				throw new UnprocessableEntityExeption("No Access for this modification.");
			}
		}
	}
	
	@Override
	public UserTransferObject partialUpdate(UUID userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		UserTransferObject transferObject;
		transferObject = partialUpdateIntern(userId, patch);
		transferObject.setUser(filterPUTPATCHInternal(transferObject.getUser()));
		return transferObject;
	}
	
	//Rollen noch zu überprüfen
	//json parse pre test oder valiudierung und rollback?
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public UserTransferObject partialUpdateIntern(UUID userId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		var keycloakId = SecurityUtils.extractId(authentication);
		var userKeycloak = keycloakRolesManagement.getUserRepresentation(keycloakId);
		var realmRolesKeycloak = checkRightsMatrix.getAuthoritiesForString(userKeycloak.getRealmRoles());
		
		var authentificatedUser = repository.findByKeycloakId(keycloakId);
		
		
		Optional<User> userOld = repository.findByIdAndDeleted(userId, false);
		
		
		if (userOld.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(User.class.getSimpleName(), userId);
		}
		UserRepresentation userRepresentation = keycloakRolesManagement.getUserRepresentation(userOld.get().getKeycloakId());
		UserDTO userDTO = userRepresentationMapper.toDto(new UserTransferObject(userRepresentation, userOld.get()));
		JsonNode original = objectMapper.valueToTree(userDTO);
		JsonNode patched = patch.apply(original);
		UserDTO userDTOPatched = objectMapper.treeToValue(patched, UserDTO.class);
		super.checkviolationsInternal(userId, super.partialUpdateSavePropertiesInternal(userDTOPatched));
		if (!userDTOPatched.getCompanyId().equals(userOld.get().getCompany().getId())) {
			throw new UnprocessableEntityExeption("Cannot change CompanyId");
		}
		UserTransferObject userPatched = userRepresentationMapper.toEntity(userDTOPatched);
		if (keycloakId.equals(userPatched.getUser().getKeycloakId())) {
			if (CollectionUtils.isEqualCollection(userKeycloak.getRealmRoles(), userPatched.getUserRepresentation().getRealmRoles())) {
				checkRoles(userPatched, realmRolesKeycloak);
				userPatched.setUserRepresentation(keycloakRolesManagement.updateKeycloakUserRepresentation(userPatched.getUserRepresentation()));
				updateUserInternal(userId, userPatched);
				return userPatched;
			} else {
				checkRoles(userPatched, realmRolesKeycloak);
				userPatched.getUser().setCompany(findCompany(userPatched.getUser().getCompany()));
				userPatched.setUserRepresentation(keycloakRolesManagement.updateKeycloakUserRepresentation(userPatched.getUserRepresentation()));
				updateUserInternal(userId, userPatched);
				return userPatched;
			}
		} else {
			if (authentificatedUser.isPresent()) {
				if (userPatched.getUser().getCompany().getId().equals(authentificatedUser.get().getCompany().getId())) {
					if (!userPatched.getUser().equals(userOld.get())) {
						throw new UnprocessableEntityExeption("Can only update Roles");
					}
					checkRoles(userPatched, realmRolesKeycloak);
					keycloakRolesManagement.updateOnlyRoles(userPatched.getUserRepresentation());
					userPatched.setUserRepresentation(keycloakRolesManagement.getUserRepresentation(UUID.fromString(userPatched.getUserRepresentation().getId())));
					try {
						executeActionMails(Constants.UPDATE_ACTION_EMAILS, this.usersResource.get(userPatched.getUserRepresentation().getId()));
						
					} catch (Exception e) {
						userPatched.getUserRepresentation().setEnabled(true);
						this.usersResource.get(userPatched.getUserRepresentation().getId()).update(userPatched.getUserRepresentation());
						executeActionMails(Constants.UPDATE_ACTION_EMAILS, this.usersResource.get(userPatched.getUserRepresentation().getId()));
					}
					return userPatched;
					
				} else {
					throw new UnprocessableEntityExeption("No Access for this modification.");
				}
				
			} else {
				throw new UnprocessableEntityExeption("No Access for this modification.");
			}
		}
		
	}
	
	
	//futher working roles settings
	
	@Override
	public Page<UserTransferObject> read(FilterExtra pageable, String query) {
		
		return super.readInternal(pageable, query).map(u -> {
					try {
						return new UserTransferObject(keycloakRolesManagement.getUserRepresentation(u.getKeycloakId()), u);
					} catch (Exception e) {
						return new UserTransferObject(null, u);
					}
				}
		
		);
		
	}
	
	@Override
	public Optional<UserTransferObject> readOne(UUID primaryKey) {
		return super.readOneInternal(primaryKey).map(u -> new UserTransferObject(keycloakRolesManagement.getUserRepresentation(u.getKeycloakId()), u));
	}
	
	@Override
	public void delete(UUID primaryKey) {
		deleteHelper.deleteUser(primaryKey);

//		var old = super.deleteInternal(primaryKey);
//		super.saveInternal(old);
//		this.usersResource.get(old.getKeycloakId().toString()).logout();
//		var userRepresentation = keycloakRolesManagement.getUserRepresentation(old.getKeycloakId());
//		userRepresentation.setEnabled(false);
//		keycloakRolesManagement.updateKeycloakUserRepresentation(userRepresentation);
	}
	
	private void updateUserInternal(UUID userId, UserTransferObject entity) {
		
		//Update User in DB
		entity.setUser(super.updateInternal(userId, entity.getUser()));
		entity.getUser().setCompany(findCompany(entity.getUser().getCompany()));
		entity.setUser(filterPUTPATCHInternal(super.saveInternal(entity.getUser())));
	}
	
	private Company findCompany(Company companyEntity) {
		return companyRepository.findById(companyEntity.getId()).filter(company -> !company.isDeleted())
				.orElseThrow(() -> new NoSuchElementFoundException(Company.class.getSimpleName(), companyEntity.getId()));
		
	}
	
	public boolean executeActionMails(List<String> mailtypes, UserResource userResource) {
		try {
			userResource.executeActionsEmail(mailtypes);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	public Class<User> getEntityClazz() {
		return User.class;
	}
	
	@Override
	public Class<UserDTO> getEntityDTOClazz() {
		return UserDTO.class;
	}
	
	@Override
	public Path<User> getQClazz() {
		return QUser.user;
	}
	
	@Override
	public boolean usePutPatchPropertyFilter() {
		return true;
	}
	
	@Override
	public CrudServiceSubRessource<UserProperty, UUID, UUID> getPropertySubService() {
		return this.userUserPropertyService;
	}
	
	@Override
	public AbstractRepository<UserProperty> getPropertyRepository() {
		return this.propertiesRepository;
	}
	
	@Override
	public AbstractMapper<UserProperty, UserPropertyDTO> getPropertyMapper() {
		return this.userPropertyMapper;
	}
	
	@Override
	public AbstractRepository<User> getRepository() {
		return this.repository;
	}
	
	@Override
	public AbstractMapper<User, UserDTO> getMapper() {
		return this.mapper;
	}
	
	@Override
	public String getPropertyDeletedString() {
		return "userProperties.deleted==false";
	}
	
	@Override
	public String getQueryRewritedString(Matcher m) {
		return QueryRewrite.queryRewriteUserToUserProperty(m);
	}
	
	
}