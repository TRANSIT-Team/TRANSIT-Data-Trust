package com.transit.graphbased_v2.service.impl;

import com.transit.graphbased_v2.controller.dto.ObjectDTO;
import com.transit.graphbased_v2.controller.dto.ObjectResponseDTO;
import com.transit.graphbased_v2.domain.graph.nodes.ClazzType;
import com.transit.graphbased_v2.domain.graph.nodes.EntityClazz;
import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeClazz;
import com.transit.graphbased_v2.domain.graph.nodes.ObjectClazz;
import com.transit.graphbased_v2.domain.graph.relationships.Assignment;
import com.transit.graphbased_v2.domain.graph.relationships.EntityConnection;
import com.transit.graphbased_v2.domain.graph.relationships.Relationship;
import com.transit.graphbased_v2.domain.graph.relationships.RelationshipConnection;
import com.transit.graphbased_v2.exceptions.*;
import com.transit.graphbased_v2.performacelogging.LogExecutionTime;
import com.transit.graphbased_v2.repository.*;
import com.transit.graphbased_v2.service.ObjectService;
import com.transit.graphbased_v2.service.helper.StringListHelper;
import com.transit.graphbased_v2.service.helper.UpdateRightsRecursive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

import static com.transit.graphbased_v2.common.RightsConstants.*;

@Service
public class ObjectServiceBean implements ObjectService {

    @Autowired
    private Validator validator;

    @Autowired
    private EntityClazzRepository entityClazzRepository;
    @Autowired
    private ObjectClazzRepository objectClazzRepository;
    @Autowired
    private UserAttributeClazzRepository userAttributeClazzRepository;
    @Autowired
    private ObjectAttributeClazzRepository objectAttributeClazzRepository;

    @Autowired
    private EntityConnectionRepository entityConnectionRepository;

    @Autowired
    private RelationshipConnectionRepository relationshipConnectionRepository;


    @Autowired
    private AssigmentRepository assigmentRepository;

    @Autowired
    private RightsConnectionRepository rightsConnectionRepository;

    @Autowired
    private RightsRepository rightsRepository;

    @Autowired
    private UpdateRightsRecursive updateRecursive;

    @LogExecutionTime
    @Override
    public ObjectResponseDTO createObject(ObjectDTO dto) throws BadRequestException {
        Set<ConstraintViolation<ObjectDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Set<String> failures = violations
                    .stream()
                    .map(contraints -> contraints.getRootBeanClass().getSimpleName() +
                            "." + contraints.getPropertyPath() + " " + contraints.getMessage())
                    .collect(Collectors.toSet());
            String failure = "";
            for (String fail : failures) {
                failure = failure + fail + ",";
            }

            throw new ValidationException(failure);
        }
        if (!userAttributeClazzRepository.existsById(dto.getIdentityId())) {
            throw new NodeNotFoundException(dto.getIdentityId());
        }
        if (objectClazzRepository.existsById(dto.getObjectId())) {
            throw new NodeIdExistsException(dto.getObjectId());
        }


        EntityClazz entityClassNode = null;
        if (!entityClazzRepository.existsEntityClazzByEntityClass(dto.getObjectEntityClass())) {
            entityClassNode = new EntityClazz();
            entityClassNode.setId(UUID.randomUUID());
            entityClassNode.setEntityClass(dto.getObjectEntityClass());
            entityClassNode.setType(ClazzType.E);
            entityClassNode.setName(dto.getObjectEntityClass());
            entityClassNode = entityClazzRepository.save(entityClassNode);
        }

        ObjectClazz oNode = new ObjectClazz();
        oNode.setId(dto.getObjectId());
        oNode.setEntityClass(dto.getObjectEntityClass());
        oNode.setType(ClazzType.O);
        oNode.setName(oNode.getEntityClass() + "#" + oNode.getId());
        objectClazzRepository.save(oNode);

        ObjectAttributeClazz oaNode = new ObjectAttributeClazz();
        oaNode.setId(UUID.randomUUID());
        oaNode.setName("OA#" + oaNode.getId() + "Identity#" + dto.getIdentityId() + "#" + oNode.getEntityClass() + "#" + oNode.getId());
        oaNode.setType(ClazzType.OA);
        oaNode.setEntityClass("OA");
        Map<String, String> oaProps = new HashMap<>();
        oaProps.put(READ_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaProps.put(WRITE_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaNode.setProperties(oaProps);
        objectAttributeClazzRepository.save(oaNode);

        var relation = new RelationshipConnection(dto.getIdentityId(), oaNode.getId(), true, true, true);
        relationshipConnectionRepository.createRelationship(relation);

        while (true) {
            try {
                var x = entityClazzRepository.findAllByEntityClass(dto.getObjectEntityClass());
                if (!x.isEmpty()) {
                    entityClassNode = x.get(0);
                }
                assert entityClassNode != null;
                var entityCon = new EntityConnection(entityClassNode.getId(), oNode.getId());
                entityConnectionRepository.createRelationship(entityCon);
                break;
            } catch (Exception ignored) {

            }
        }
        var assignment2 = new Assignment(oNode.getId(), oaNode.getId());
        assigmentRepository.createRelationship(assignment2);


        return new ObjectResponseDTO(oNode.getId(), oNode.getEntityClass(), oNode.getName());
    }


    @Override
    public ObjectResponseDTO updateObject(ObjectDTO dto) throws BadRequestException {
        var oldRights = rightsRepository.getRights(dto.getIdentityId(), dto.getIdentityId(), dto.getObjectId());
        if (oldRights.isEmpty()) {
            throw new NodeNotFoundException("Cannot find old OA node");
        }
        if (!rightsConnectionRepository.getOutgoingRelationships(oldRights.get().getId()).isEmpty()) {
            throw new BadRequestException("This Object you doesnt create.");
        }

        EntityClazz entityClassNode = null;
        if (!entityClazzRepository.existsEntityClazzByEntityClass(dto.getObjectEntityClass())) {
            entityClassNode = new EntityClazz();
            entityClassNode.setId(UUID.randomUUID());
            entityClassNode.setEntityClass(dto.getObjectEntityClass());
            entityClassNode.setType(ClazzType.E);
            entityClassNode.setName(dto.getObjectEntityClass());
            entityClassNode = entityClazzRepository.save(entityClassNode);
        }


        //update OA Node rightsProperties
        var oaNode = objectAttributeClazzRepository.findById(oldRights.get().getId()).get();
        Map<String, String> oaProps = new HashMap<>();

        oaProps.put(READ_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaProps.put(WRITE_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaProps.put(SHARE_READ_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaProps.put(SHARE_WRITE_PROPERTIES, StringListHelper.collectionToString(dto.getProperties()));
        oaNode.setProperties(oaProps);
        objectAttributeClazzRepository.save(oaNode);

        var oldEntityConnection = entityConnectionRepository.getIncomingRelationships(dto.getObjectId()).get(0);
        var oldEntity = entityClazzRepository.findById(oldEntityConnection.getSourceID()).get();
        if (!oldEntity.getEntityClass().equals(dto.getObjectEntityClass())) {

            //O Node entityClass and name by newEntityClass
            var oNode = objectClazzRepository.findById(dto.getObjectId()).get();
            oNode.setEntityClass(dto.getObjectEntityClass());
            oNode.setName(dto.getObjectEntityClass() + '#' + dto.getObjectId());
            objectClazzRepository.save(oNode);

            //Connect to the right E Node (Enity Node)
            entityConnectionRepository.deleteRelationship(oldEntityConnection);
            while (true) {
                try {
                    var x = entityClazzRepository.findAllByEntityClass(dto.getObjectEntityClass());
                    if (!x.isEmpty()) {
                        entityClassNode = x.get(0);
                    }
                    assert entityClassNode != null;
                    var entityCon = new EntityConnection(entityClassNode.getId(), dto.getObjectId());
                    entityConnectionRepository.createRelationship(entityCon);


                    break;
                } catch (Exception ignored) {

                }
            }
        }

        updateRecursive.updateRecursive(oaNode.getId());

        return new ObjectResponseDTO(dto.getObjectId(), dto.getObjectEntityClass(), objectClazzRepository.findById(dto.getObjectId()).get().getName());

    }

    @Override
    public Optional<ObjectResponseDTO> getObject(UUID objectId) {
        return objectClazzRepository.findById(objectId).map(oNode -> new ObjectResponseDTO(oNode.getId(), oNode.getEntityClass(), oNode.getName()));
    }

    @Override
    public boolean deleteObject(UUID objectId, UUID identityId) throws ForbiddenException, BadRequestException {
        var rights = rightsRepository.getRights(identityId, identityId, objectId);
        if (rights.isEmpty()) {
            throw new BadRequestException("You don't have rights to delete this object");
        } else if (!rightsConnectionRepository.getOutgoingRelationships(rights.get().getId()).isEmpty()) {
            throw new BadRequestException("You don't have rights to delete this object");
        }

        var connectedOAnodes = new HashSet<UUID>();
        long sizeList = 0;
        connectedOAnodes.add(rights.get().getId());

        while (sizeList < connectedOAnodes.size()) {
            sizeList = connectedOAnodes.size();
            connectedOAnodes.forEach(id -> {
                        connectedOAnodes.addAll(rightsConnectionRepository.getIncomingRelationships(id).stream().map(Relationship::getSourceID).toList());
                    }
            );
            deleteOaNodes(connectedOAnodes);
            objectClazzRepository.deleteById(objectId);
        }
        return true;
    }

    private void deleteOaNodes(HashSet<UUID> connectedOAnodes) {
        while (!connectedOAnodes.isEmpty()) {
            connectedOAnodes.forEach(id -> {
                if (rightsConnectionRepository.getIncomingRelationships(id).isEmpty()) {
                    relationshipConnectionRepository.getIncomingRelationships(id).forEach(assoc -> relationshipConnectionRepository.deleteRelationship(assoc));
                    assigmentRepository.getIncomingRelationships(id).forEach(assign -> {
                        entityConnectionRepository.getIncomingRelationships(assign.getSourceID()).forEach(assignToEntity -> entityConnectionRepository.deleteRelationship(assignToEntity));
                        assigmentRepository.deleteRelationship(assign);
                    });
                    rightsConnectionRepository.getOutgoingRelationships(id).forEach(assoc -> rightsConnectionRepository.deleteRelationship(assoc));
                    objectAttributeClazzRepository.deleteById(id);
                    connectedOAnodes.remove(id);
                }
            });
        }
    }
}
