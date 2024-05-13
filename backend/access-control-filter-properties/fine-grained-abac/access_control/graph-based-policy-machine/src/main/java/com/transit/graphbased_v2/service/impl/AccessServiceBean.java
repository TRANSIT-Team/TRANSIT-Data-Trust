package com.transit.graphbased_v2.service.impl;

import com.transit.graphbased_v2.domain.graph.nodes.ClazzType;
import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeClazz;
import com.transit.graphbased_v2.domain.graph.relationships.Assignment;
import com.transit.graphbased_v2.domain.graph.relationships.Relationship;
import com.transit.graphbased_v2.domain.graph.relationships.RelationshipConnection;
import com.transit.graphbased_v2.domain.graph.relationships.RightsConnection;
import com.transit.graphbased_v2.exceptions.BadRequestException;
import com.transit.graphbased_v2.exceptions.ForbiddenException;
import com.transit.graphbased_v2.exceptions.NodeNotFoundException;
import com.transit.graphbased_v2.performacelogging.LogExecutionTime;
import com.transit.graphbased_v2.repository.*;
import com.transit.graphbased_v2.service.AccessService;
import com.transit.graphbased_v2.service.helper.AccessTransferComponentHelper;
import com.transit.graphbased_v2.service.helper.StringListHelper;
import com.transit.graphbased_v2.service.helper.UpdateRightsRecursive;
import com.transit.graphbased_v2.transferobjects.AccessTransferComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.transit.graphbased_v2.common.RightsConstants.*;

@Service
public class AccessServiceBean implements AccessService {
	
	@Autowired
	private RightsRepository rightsRepository;
	
	@Autowired
	private ObjectClazzRepository objectClazzRepository;
	
	@Autowired
	private ObjectAttributeClazzRepository objectAttributeClazzRepository;
	
	@Autowired
	private RightsConnectionRepository rightsConnectionRepository;
	
	@Autowired
	private EntityConnectionRepository entityConnectionRepository;
	
	@Autowired
	private RelationshipConnectionRepository relationshipConnectionRepository;
	
	
	@Autowired
	private AssigmentRepository assigmentRepository;
	
	@Autowired
	private EntityClazzRepository entityClazzRepository;
	
	@Autowired
	private UpdateRightsRecursive updateRecursive;
	
	@Autowired
	private AccessTransferComponentHelper accessTransferComponentHelper;
	
	@Override
	public boolean updateConnection(Set<String> readProperties, Set<String> writeProperties, Set<String> shareReadProperties, Set<String> shareWriteProperties, UUID oId, UUID identityId, UUID requestedById) throws BadRequestException, ForbiddenException {
		
		if (readProperties == null) {
			readProperties = new HashSet<>();
		}
		if (writeProperties == null) {
			writeProperties = new HashSet<>();
		}
		if (shareReadProperties == null) {
			shareReadProperties = new HashSet<>();
		}
		if (shareWriteProperties == null) {
			shareWriteProperties = new HashSet<>();
		}
		
		if (readProperties.size() < writeProperties.size()) {
			throw new BadRequestException("Cannot have more write Properties as read Properties.");
		}
		
		
		if (readProperties.size() < shareReadProperties.size()) {
			throw new BadRequestException("Cannot have more sharing read Properties as read Properties.");
		}
		
		
		if (writeProperties.size() < shareWriteProperties.size()) {
			throw new BadRequestException("Cannot have more sharing write Properties as write Properties.");
		}
		
		var oldRights = rightsRepository.getRights(identityId, requestedById, oId);
		if (oldRights.isEmpty()) {
			throw new NodeNotFoundException("Cannot find existing access data for this identity");
		}
		
		var oaNode = objectAttributeClazzRepository.findById(oldRights.get().getId()).get();
		Map<String, String> oaProps = new HashMap<>();
		var myRights = getAccess(oId, requestedById, requestedById);
		if (myRights.isEmpty()) {
			throw new ForbiddenException("No Access");
		}
		oaProps.put(READ_PROPERTIES, StringListHelper.collectionToString(validateRights(readProperties, readProperties, myRights.get(), READ_PROPERTIES)));
		oaProps.put(WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(writeProperties, readProperties, myRights.get(), WRITE_PROPERTIES)));
		oaProps.put(SHARE_READ_PROPERTIES, StringListHelper.collectionToString(validateRights(shareReadProperties, readProperties, myRights.get(), SHARE_READ_PROPERTIES)));
		oaProps.put(SHARE_WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(shareWriteProperties, writeProperties, myRights.get(), SHARE_WRITE_PROPERTIES)));
		
		oaNode.setProperties(oaProps);
		objectAttributeClazzRepository.save(oaNode);
		updateRecursive.updateRecursive(oaNode.getId());
		
		return true;
	}
	
	@LogExecutionTime
	@Override
	public Optional<AccessTransferComponent> getAccess(UUID oId, UUID identityId, UUID requestedById) throws NodeNotFoundException {
		
		if (!objectClazzRepository.existsById(oId)) {
			throw new NodeNotFoundException(oId);
		}
		return rightsRepository.getRights(identityId, requestedById, oId).map(entry -> accessTransferComponentHelper.getAccessTransferComponent(entry.getObjectAttributeClazz(), entry.getOId(), entry.getOEntityClazz(), identityId));
		
	}
	
	
	@Override
	public List<AccessTransferComponent> getAccessList(Set<UUID> objectIds, UUID identityId, UUID requestedById) {
		return rightsRepository.getRightsListOld(objectIds, identityId, requestedById).stream().map(entry -> accessTransferComponentHelper.getAccessTransferComponent(entry.getObjectAttributeClazz(), entry.getOId(), entry.getOEntityClazz(), identityId)).collect(Collectors.toList());
	}
	
	@Override
	public List<AccessTransferComponent> getAccessClazz(String entityClazz, UUID requestedById, boolean createdByMyOwn, UUID identityId) {
		
		
		return rightsRepository.getRightsClass(entityClazz, requestedById, createdByMyOwn, identityId).stream().map(entry -> {
			
			if (identityId == null) {
				return accessTransferComponentHelper.getAccessTransferComponent(entry.getObjectAttributeClazz(), entry.getOId(), entry.getOEntityClazz(), requestedById);
			} else {
				return accessTransferComponentHelper.getAccessTransferComponent(entry.getObjectAttributeClazz(), entry.getOId(), entry.getOEntityClazz(), identityId);
			}
		}).collect(Collectors.toList());
	}
	
	@LogExecutionTime
	@Override
	public boolean createConnection(Set<String> readProperties, Set<String> writeProperties, Set<String> shareReadProperties, Set<String> shareWriteProperties, UUID oId, UUID identityId, UUID requestedById) throws BadRequestException, ForbiddenException {
		
		if (readProperties == null) {
			readProperties = new HashSet<>();
		}
		if (writeProperties == null) {
			writeProperties = new HashSet<>();
		}
		if (shareReadProperties == null) {
			shareReadProperties = new HashSet<>();
		}
		if (shareWriteProperties == null) {
			shareWriteProperties = new HashSet<>();
		}
		
		
		if (readProperties.size() < writeProperties.size()) {
			throw new BadRequestException("Cannot have more write Properties as read Properties.");
		}
		
		if (readProperties.size() < shareReadProperties.size()) {
			throw new BadRequestException("Cannot have more sharing read Properties as read Properties.");
		}
		
		if (writeProperties.size() < shareWriteProperties.size()) {
			throw new BadRequestException("Cannot have more sharing write Properties as write Properties.");
		}
		
		if (!objectClazzRepository.existsById(oId)) {
			throw new NodeNotFoundException("Object not exists");
		}
		if (getAccess(oId, identityId, requestedById).isPresent()) {
			throw new BadRequestException("Rights already exists--> Have to update not create.");
		}
		
		
		ObjectAttributeClazz oaNode = new ObjectAttributeClazz();
		oaNode.setId(UUID.randomUUID());
		oaNode.setName("OA#" + oaNode.getId() + "Group#" + identityId + "#" + objectClazzRepository.findById(oId).get().getEntityClass() + "#" + oId);
		oaNode.setType(ClazzType.OA);
		oaNode.setEntityClass("OA");
		Map<String, String> oaProps = new HashMap<>();
		var myRights = getAccess(oId, requestedById, requestedById);
		if (myRights.isEmpty()) {
			throw new ForbiddenException("No Access");
		}
		
		oaProps.put(READ_PROPERTIES, StringListHelper.collectionToString(validateRights(readProperties, readProperties, myRights.get(), READ_PROPERTIES)));
		oaProps.put(WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(writeProperties, readProperties, myRights.get(), WRITE_PROPERTIES)));
		oaProps.put(SHARE_READ_PROPERTIES, StringListHelper.collectionToString(validateRights(shareReadProperties, readProperties, myRights.get(), SHARE_READ_PROPERTIES)));
		oaProps.put(SHARE_WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(shareWriteProperties, writeProperties, myRights.get(), SHARE_WRITE_PROPERTIES)));
		
		
		oaNode.setProperties(oaProps);
		objectAttributeClazzRepository.save(oaNode);
		
		var callingRights = rightsRepository.getRights(requestedById, requestedById, oId);
		
		
		var rightsConnection = new RightsConnection(oaNode.getId(), callingRights.get().getId());
		rightsConnectionRepository.createRelationship(rightsConnection);
		
		var relation1 = new RelationshipConnection(requestedById, oaNode.getId(), false, false, true);
		relationshipConnectionRepository.createRelationship(relation1);
		
		var relation2 = new RelationshipConnection(identityId, oaNode.getId(), false, true, false);
		relationshipConnectionRepository.createRelationship(relation2);
		
		
		var assignment2 = new Assignment(oId, oaNode.getId());
		assigmentRepository.createRelationship(assignment2);
		return true;
	}
	
	
	@Override
	public boolean deleteConnectionRecursive(UUID oId, UUID identityId, UUID requestedById) {
		var rights = rightsRepository.getRights(identityId, requestedById, oId);
		if (rights.isEmpty()) {
			return false;
		}
		var oaId = rights.get().getId();
		
		long sizeList = 0;
		
		var connectedOAnodes = new HashSet<UUID>();
		connectedOAnodes.add(rights.get().getId());
		
		while (sizeList < connectedOAnodes.size()) {
			sizeList = connectedOAnodes.size();
			connectedOAnodes.forEach(id -> {
				connectedOAnodes.addAll(rightsConnectionRepository.getIncomingRelationships(id).stream().map(Relationship::getSourceID).toList());
			});
		}
		deleteOaNodes(connectedOAnodes);
		return true;
	}
	
	public void deleteOaNodes(HashSet<UUID> connectedOAnodes) {
		while (!connectedOAnodes.isEmpty()) {
			connectedOAnodes.forEach(id -> {
				if (rightsConnectionRepository.getIncomingRelationships(id).isEmpty()) {
					relationshipConnectionRepository.getIncomingRelationships(id).forEach(assoc -> relationshipConnectionRepository.deleteRelationship(assoc));
					assigmentRepository.getIncomingRelationships(id).forEach(assign -> assigmentRepository.deleteRelationship(assign));
					rightsConnectionRepository.getOutgoingRelationships(id).forEach(assoc -> rightsConnectionRepository.deleteRelationship(assoc));
					objectAttributeClazzRepository.deleteById(id);
					connectedOAnodes.remove(id);
				}
				
			});
		}
		
		
	}
	
	private Set<String> validateRights(Set<String> properties, Set<String> restrictionProperties, AccessTransferComponent comp, String propertyType) {
		
		//comp has the my rights properties (read,write,share), these are the maximum I can edit otherwise I have to edit the Object-Properties via object-endpoint
		
		
		//properties have the new properties to update
		//validation that writeProperties can just be the readProperties or less
		//validation that shareReadProperties can just be readProperties or less
		//validation that shareWriteProperties can just be writeProperties or less
		
		//the properties validation is based on the restriction by the restrictionProperties
		
		Set<String> filteredProperties = new HashSet<>();
		
		if (propertyType.equals(READ_PROPERTIES)) {
			
			filteredProperties.addAll(properties);
			
			
			filteredProperties.retainAll(comp.getReadProperties());
			
			
			filteredProperties.retainAll(restrictionProperties);
		} else if (propertyType.equals(WRITE_PROPERTIES)) {
			filteredProperties.addAll(properties);
			filteredProperties.retainAll(comp.getWriteProperties());
			filteredProperties.retainAll(restrictionProperties);
		} else if (propertyType.equals(SHARE_READ_PROPERTIES)) {
			filteredProperties.addAll(properties);
			filteredProperties.retainAll(comp.getShareReadProperties());
			filteredProperties.retainAll(restrictionProperties);
		} else if (propertyType.equals(SHARE_WRITE_PROPERTIES)) {
			filteredProperties.addAll(properties);
			filteredProperties.retainAll(comp.getShareWriteProperties());
			filteredProperties.retainAll(restrictionProperties);
		}
		return filteredProperties;
	}
	
	
}
