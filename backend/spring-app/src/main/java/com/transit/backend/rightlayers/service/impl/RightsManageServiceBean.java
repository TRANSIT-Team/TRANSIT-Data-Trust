package com.transit.backend.rightlayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.config.mail.SendMail;
import com.transit.backend.datalayers.domain.Address;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.CompanyIDToCompanyOID;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.AddressRepository;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.datalayers.repository.OrderRepository;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.rightlayers.controller.dto.RIghtsDtoCoreProperties;
import com.transit.backend.rightlayers.controller.dto.RightsDtoCore;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.domain.OAPropertiesDTO;
import com.transit.backend.rightlayers.service.*;
import com.transit.backend.rightlayers.service.helper.CanChangeAddressRights;
import com.transit.backend.rightlayers.service.helper.MethodXAddress;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.transit.backend.config.RightsConstants.DOMAIN_PACKAGE;

@Service
@Slf4j
public class RightsManageServiceBean implements RightsManageService {
	
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	private RightsNodeService rightsNodeService;
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	@Autowired
	private UserHelperFunctions userHelperFunctions;
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private SendMail sendMail;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private CanChangeAddressRights canChangeAddressRights;
	
	@Autowired
	private MethodXAddress methodXAddress;
	
	@Autowired
	private IdentityService identityService;
	
	@Autowired
	private ObjectService objectService;
	
	@Autowired
	private AccessService accessService;
	
	@Override
	public void registerCompany(UUID companyUUID) {
		identityService.createIdentity(companyUUID);
	}
	
	@Override
	@Transactional
	//Error Handling have to be added
	public void createEntityAndConnectIt(UUID targetId, String typeClazz, Class<?> clazz) {
		var userId = this.userHelperFunctions.getUserID();
		this.createEntityAndConnectIt(targetId, typeClazz, clazz, userId);
	}
	
	@Override
	@Transactional
	public void createEntityAndConnectIt(UUID targetId, String typeClazz, Class<?> clazz, UUID userID) {
		var companyId = this.userHelperFunctions.getCompanyId(userID);
		//Other id fromObjekt COmpany and UA Company Have to be stored
		if (typeClazz.equals(Company.class.getSimpleName()) && targetId.equals(companyId)) {
			var newConnect = new CompanyIDToCompanyOID(companyId, UUID.randomUUID());
			companyIdToCompanyOIDRepository.save(newConnect);
			targetId = newConnect.getCompanyOID();
		}
		List<String> properties;
		var propertiesArray = FieldUtils.getAllFields(clazz);
		properties = Arrays.stream(propertiesArray).map(field -> field.getName()).toList();
		
		
		objectService.createObject(targetId, typeClazz, companyId, new HashSet<>(properties));
	}
	
	@Override
	@Transactional
	public void connectEntityToNewCompany(UUID id, UUID companyID, UUID subOrderId, List<String> readProperties, List<String> writeProperties) throws ClassNotFoundException, MessagingException, InterruptedException {
		connectEntityToNewCompany(id, companyID, subOrderId, readProperties, writeProperties, true);
	}
	
	@Transactional
	@Override
	public void connectEntityToNewCompany(UUID id, UUID companyID, UUID subOrderId, List<String> readProperties, List<String> writeProperties, boolean outsideClass) throws ClassNotFoundException, MessagingException, InterruptedException {
		var typeClazz = rightsNodeService.readEntityCLass(id);
		if (outsideClass) {
			if (typeClazz.equals(Address.class.getSimpleName())) {
				if (subOrderId == null) {
					throw new UnprocessableEntityExeption("Do not know for which Order the Address have to be change");
				} else {
					var orderToAddress = orderRepository.findById(subOrderId);
					if (orderToAddress.isEmpty()) {
						throw new UnprocessableEntityExeption("Cannot found Order with Id " + subOrderId + " under the Address rights Entry");
					}
					//Rights will be combined inside method
					AtomicBoolean canChange = new AtomicBoolean(true);
					canChangeAddressRights.canChangeAddressRights(addressRepository.findById(id).get(), orderToAddress.get(), canChange);
					if (!canChange.get()) {
						var newAddress = methodXAddress.methodXAddress(id, subOrderId);
						id = newAddress.getId();
					}
				}
			}
		}
		var rightsHave = accessService.getAccess(id);
		//fields always accesible
		var propertiesArray = FieldUtils.getAllFields(AbstractEntity.class);
		var properties = Arrays.stream(propertiesArray)
				//.filter(value -> !value.getName().startsWith("$$"))
				.map(Field::getName).toList();
		var clazz = Class.forName(DOMAIN_PACKAGE + typeClazz);
		readProperties.addAll(properties);
		if (!writeProperties.isEmpty()) {
			writeProperties.addAll(properties);
		}
		propertiesArray = FieldUtils.getAllFields(clazz);
		properties = new ArrayList<>();
		var propertiestemp = Arrays.stream(propertiesArray)
				.toList();
		for (var field : propertiestemp) {
			if (!field.getType().isPrimitive()
					&& !(field.getType() == UUID.class)
					&& !(field.getType() == String.class)
					&& !(field.getType() == OffsetDateTime.class)) {
				
				properties.add(field.getName());
			}
		}
		readProperties.addAll(properties);
		accessService.createConnection(new HashSet<>(readProperties), new HashSet<>(writeProperties), id, companyID);
		if (clazz.equals(Order.class)) {
			sendMail.sendMailForQuestion(id);
		}
		
	}
	
	@Override
	public void updateEntityConnection(UUID id, UUID companyId, UUID orderId, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException, ClassNotFoundException, MessagingException, InterruptedException {
		var typeClazz = rightsNodeService.readEntityCLass(id);
		var rightsOld = accessService.getAccess(id, companyId);
		if (rightsOld.isEmpty()) {
			throw new UnprocessableEntityExeption("Failed to get old Rights entity");
		}
		
		
		var rightsDtoCoreOld = new RightsDtoCore();
		rightsDtoCoreOld.setCompanyId(companyId);
		rightsDtoCoreOld.setEntityId(id);
		rightsDtoCoreOld.setOrderId(orderId);
		rightsDtoCoreOld.setProperties(new RIghtsDtoCoreProperties(new ArrayList<>(rightsOld.get().getObjectProperties().getReadProperties())
				, new ArrayList<>(rightsOld.get().getObjectProperties().getWriteProperties())));
		JsonNode original = objectMapper.valueToTree(rightsDtoCoreOld);
		JsonNode patched = patch.apply(original);
		rightsDtoCoreOld = objectMapper.treeToValue(patched, RightsDtoCore.class);
		
		updateInternal(rightsDtoCoreOld, typeClazz, rightsOld, id, companyId);
		
		
	}
	
	@Override
	@Transactional
	public void updateEntityConnectionByPUT(UUID id, UUID companyId, UUID orderId, List<String> readProperties, List<String> writeProperties) throws ClassNotFoundException, MessagingException, InterruptedException {
		updateEntityConnectionByPUT(id, companyId, orderId, readProperties, writeProperties, null);
	}
	
	@Override
	@Transactional
	public void updateEntityConnectionByPUT(UUID id, UUID companyId, UUID orderId, List<String> readProperties, List<String> writeProperties, UUID parentCompanyId) throws ClassNotFoundException, MessagingException, InterruptedException {
		String typeClazz = rightsNodeService.readEntityCLass(id);
		Optional<AccessResponseDTO> rightsOld;
		if (parentCompanyId != null) {
			rightsOld = accessService.getAccess(id, companyId, parentCompanyId);
		} else {
			rightsOld = accessService.getAccess(id, companyId);
		}
		if (rightsOld.isEmpty()) {
			throw new UnprocessableEntityExeption("Failed to get old Rights entity");
		}
		var rightsDtoCore = new RightsDtoCore();
		rightsDtoCore.setCompanyId(companyId);
		rightsDtoCore.setEntityId(id);
		rightsDtoCore.setOrderId(orderId);
		var rightsToProperties = new RIghtsDtoCoreProperties();
		rightsToProperties.setReadProperties(readProperties);
		rightsToProperties.setWriteProperties(writeProperties);
		rightsDtoCore.setProperties(rightsToProperties);
		
		updateInternal(rightsDtoCore, typeClazz, rightsOld, id, companyId, parentCompanyId);
	}
	
	private void updateInternal(RightsDtoCore rightsDtoCore, String typeClazz, Optional<AccessResponseDTO> rightsOld, UUID id, UUID companyId) throws ClassNotFoundException, MessagingException, InterruptedException {
		updateInternal(rightsDtoCore, typeClazz, rightsOld, id, companyId, null);
	}
	
	private void updateInternal(RightsDtoCore rightsDtoCore, String typeClazz, Optional<AccessResponseDTO> rightsOld, UUID id, UUID companyId, UUID parenCompanyId) throws ClassNotFoundException, MessagingException, InterruptedException {
		//Test if Rights are shared more then once with me, with other order
		//OrderId have to send every time
		if (typeClazz.equals(Address.class.getSimpleName())) {
			if (rightsDtoCore.getOrderId() == null) {
				throw new UnprocessableEntityExeption("Do not know for which Order the Address have to be change");
			} else {
				var orderToAddress = orderRepository.findById(rightsDtoCore.getOrderId());
				if (orderToAddress.isEmpty()) {
					throw new UnprocessableEntityExeption("Cannot found Order with Id " + rightsDtoCore.getOrderId() + " under the Address rights Entry");
				}
				//Rights will be combined inside method
				AtomicBoolean canChange = new AtomicBoolean(true);
				canChangeAddressRights.canChangeAddressRights(addressRepository.findById(rightsDtoCore.getEntityId()).get(), orderToAddress.get(), canChange);
				if (!canChange.get()) {
					var newAddress = methodXAddress.methodXAddress(rightsDtoCore.getEntityId(), rightsDtoCore.getOrderId());
					rightsDtoCore.setEntityId(newAddress.getId());
					id = newAddress.getId();
				}
			}
		}
		
		var propertiesArray = FieldUtils.getAllFields(AbstractEntity.class);
		var properties = Arrays.stream(propertiesArray)
				.map(Field::getName).toList();
		var clazz = Class.forName(DOMAIN_PACKAGE + typeClazz);
		
		if (!rightsDtoCore.getProperties().getReadProperties().isEmpty()) {
			rightsDtoCore.getProperties().getReadProperties().addAll(properties);
		}
		if (!rightsDtoCore.getProperties().getWriteProperties().isEmpty()) {
			rightsDtoCore.getProperties().getWriteProperties().addAll(properties);
		}
		//add dependent entities
		propertiesArray = FieldUtils.getAllFields(clazz);
		properties = new ArrayList<>();
		var propertiestemp = Arrays.stream(propertiesArray)
				.toList();
		for (var field : propertiestemp) {
			if (!field.getType().isPrimitive()
					&& !(field.getType() == UUID.class)
					&& !(field.getType() == String.class)
					&& !(field.getType() == OffsetDateTime.class)) {
				
				properties.add(field.getName());
			}
		}
		if (!rightsDtoCore.getProperties().getReadProperties().isEmpty()) {
			rightsDtoCore.getProperties().getReadProperties().addAll(properties);
		}
		rightsOld.get().setObjectProperties(new OAPropertiesDTO(new HashSet<>(rightsDtoCore.getProperties().getReadProperties()),
				new HashSet<>(rightsDtoCore.getProperties().getWriteProperties())));
		
		try {
			if (parenCompanyId != null) {
				accessService.updateConnection(rightsOld.get().getObjectProperties().getReadProperties(),
						rightsOld.get().getObjectProperties().getWriteProperties(),
						id,
						companyId,
						parenCompanyId);
			} else {
				accessService.updateConnection(rightsOld.get().getObjectProperties().getReadProperties(),
						rightsOld.get().getObjectProperties().getWriteProperties(),
						id,
						companyId);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		log.error("result");
	}
	
	
}
