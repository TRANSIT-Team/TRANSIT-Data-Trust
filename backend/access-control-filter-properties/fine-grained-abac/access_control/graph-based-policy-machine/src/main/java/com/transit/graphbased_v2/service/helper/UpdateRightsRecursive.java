package com.transit.graphbased_v2.service.helper;

import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeClazz;
import com.transit.graphbased_v2.repository.ObjectAttributeClazzRepository;
import com.transit.graphbased_v2.repository.RightsConnectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.transit.graphbased_v2.common.RightsConstants.*;

@Component
public class UpdateRightsRecursive {

    @Autowired
    private RightsConnectionRepository rightsConnectionRepository;

    @Autowired
    private ObjectAttributeClazzRepository objectAttributeClazzRepository;

    public boolean updateRecursive(UUID startOANode) {
        var parent = objectAttributeClazzRepository.findById(startOANode).get();

        rightsConnectionRepository.getIncomingRelationships(parent.getId()).forEach(childId -> {
            var child = objectAttributeClazzRepository.findById(childId.getSourceID()).get();

            child.updateProperty(READ_PROPERTIES, StringListHelper.collectionToString(validateRights(StringListHelper.stringToSet(child.getProperties().get(READ_PROPERTIES)), parent, READ_PROPERTIES)));
            child.updateProperty(WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(StringListHelper.stringToSet(child.getProperties().get(WRITE_PROPERTIES)), parent, WRITE_PROPERTIES)));
            child.updateProperty(SHARE_READ_PROPERTIES, StringListHelper.collectionToString(validateRights(StringListHelper.stringToSet(child.getProperties().get(SHARE_READ_PROPERTIES)), parent, SHARE_READ_PROPERTIES)));
            child.updateProperty(SHARE_WRITE_PROPERTIES, StringListHelper.collectionToString(validateRights(StringListHelper.stringToSet(child.getProperties().get(SHARE_WRITE_PROPERTIES)), parent, SHARE_WRITE_PROPERTIES)));

            objectAttributeClazzRepository.save(child);
            rightsConnectionRepository.getIncomingRelationships(child.getId()).forEach(childChildId -> updateRecursive(childChildId.getSourceID()));
        });

        return true;
    }


    private Set<String> validateRights(Set<String> properties, ObjectAttributeClazz parent, String propertyType) {


        var parentReadProperties = StringListHelper.stringToSet(parent.getProperties().get(READ_PROPERTIES));
        var parentWriteProperties = StringListHelper.stringToSet(parent.getProperties().get(WRITE_PROPERTIES));
        var parentSharedReadProperties = StringListHelper.stringToSet(parent.getProperties().get(SHARE_READ_PROPERTIES));
        var parentSharedWriteProperties = StringListHelper.stringToSet(parent.getProperties().get(SHARE_WRITE_PROPERTIES));


        //check that the child can only has the properties that the parent has
        //if properties are decreased, that decreased the properties of the child too

        Set<String> filteredProperties = new HashSet<>();

        if (propertyType.equals(READ_PROPERTIES)) {
            filteredProperties.addAll(properties);
            filteredProperties.retainAll(StringListHelper.stringToSet(parent.getProperties().get(READ_PROPERTIES)));

        } else if (propertyType.equals(WRITE_PROPERTIES)) {
            filteredProperties.addAll(properties);
            filteredProperties.retainAll(StringListHelper.stringToSet(parent.getProperties().get(WRITE_PROPERTIES)));

        } else if (propertyType.equals(SHARE_READ_PROPERTIES)) {
            filteredProperties.addAll(properties);
            filteredProperties.retainAll(StringListHelper.stringToSet(parent.getProperties().get(SHARE_READ_PROPERTIES)));

        } else if (propertyType.equals(SHARE_WRITE_PROPERTIES)) {
            filteredProperties.addAll(properties);
            filteredProperties.retainAll(StringListHelper.stringToSet(parent.getProperties().get(SHARE_WRITE_PROPERTIES)));

        }
        return filteredProperties;
    }

}
