package com.transit.graphbased_v2.service.helper;

import com.transit.graphbased_v2.domain.graph.nodes.ClazzType;
import com.transit.graphbased_v2.domain.graph.nodes.ObjectAttributeExtendedClazz;
import com.transit.graphbased_v2.performacelogging.LogExecutionTime;
import com.transit.graphbased_v2.repository.ObjectAttributeClazzRepository;
import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.transit.graphbased_v2.common.RightsConstants.READ_PROPERTIES;
import static com.transit.graphbased_v2.common.RightsConstants.WRITE_PROPERTIES;

@Component
public class ParseRightsResult {
	
	@Autowired
	private ObjectAttributeClazzRepository objectAttributeClazzRepository;
	
	@LogExecutionTime
	public List<ObjectAttributeExtendedClazz> parseResult(Result result, boolean mergeAll, boolean mergeByOid) {
		List<ObjectAttributeExtendedClazz> results = new ArrayList<>();
		Set<UUID> objectIds = new HashSet<>();
		while (result.hasNext()) {
			var nextRecord = result.next();
			var r = parseResult(nextRecord, true);
			if (results.isEmpty()) {
				results.add(r);
				objectIds.add(r.getOId());
			} else if (mergeAll) {
				var temp = results.get(0);
				overwriteProperties(r, temp);
				results.remove(0);
				results.add(temp);
				objectIds.add(r.getOId());
			} else if (mergeByOid) {
				if (objectIds.contains(r.getOId())) {
					var temp = results.stream().filter(e -> e.getOId().equals(r.getOId())).findFirst().get();
					overwriteProperties(r, temp);
					results.replaceAll(e -> {
						if (e.getOId().equals(temp.getOId())) {
							return temp;
						}
						return e;
					});
					
				} else {
					results.add(r);
					objectIds.add(r.getOId());
				}
			} else {
				results.add(r);
			}
		}
		return results;
	}
	
	@LogExecutionTime
	public ObjectAttributeExtendedClazz parseResult(Record record, boolean dataFromRecord) {
		ObjectAttributeExtendedClazz oa = new ObjectAttributeExtendedClazz();
		var temp = record.get(0);
		
		// get values from record
		if (dataFromRecord) {
			String idString = temp.get("id").asString();
			UUID id = UUID.fromString(idString);
			String name = temp.get("name").asString();
			String entityClass = temp.get("entityClass").asString();
			String properties = temp.get("properties").asString();
			
			oa.setId(id);
			oa.setName(name);
			oa.setType(ClazzType.valueOf(entityClass));
			oa.setProperties(ObjectAttributeExtendedClazz.getPropertiesFromString(properties));
			oa.setOId(UUID.fromString(record.get(1).asString()));
			oa.setOEntityClazz(record.get(2).asString());
			
			
		} else {
			
			var oaNormal = objectAttributeClazzRepository.findById(UUID.fromString(temp.get("id").asString())).get();
			oa.setId(oaNormal.getId());
			oa.setName(oaNormal.getName());
			oa.setType(oaNormal.getType());
			oa.setProperties(oaNormal.getProperties());
			oa.setOId(UUID.fromString(record.get(1).asString()));
		}
		
		return oa;
	}
	
	private void overwriteProperties(ObjectAttributeExtendedClazz r, ObjectAttributeExtendedClazz temp) {
		var readPropertiesTemp = StringListHelper.stringToSet(temp.getProperties().get(READ_PROPERTIES));
		readPropertiesTemp.addAll(StringListHelper.stringToSet(r.getProperties().get(READ_PROPERTIES)));
		temp.updateProperty(READ_PROPERTIES, StringListHelper.collectionToString(readPropertiesTemp));
		var writePropertiesTemp = StringListHelper.stringToSet(temp.getProperties().get(WRITE_PROPERTIES));
		writePropertiesTemp.addAll(StringListHelper.stringToSet(r.getProperties().get(WRITE_PROPERTIES)));
		temp.updateProperty(WRITE_PROPERTIES, StringListHelper.collectionToString(writePropertiesTemp));
	}
}

