package com.transit.backend.datalayers.controller.abstractclasses.abstractmethods;

import com.transit.backend.exeptions.exeption.ForbiddenException;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class PutPatchValidatorProperties<test> {
	
	public void validator(Class<test> clazz, Optional<AccessResponseDTO> rightsEntry, test oldEntity, test newEntity) {
		transformWriteProperties(rightsEntry, newEntity, oldEntity, clazz);
		
	}
	
	private void transformWriteProperties(Optional<AccessResponseDTO> rightsEntry, test entity, test oldEntity, Class<?> clazz) {
		var writeProperties = rightsEntry.get().getObjectProperties().getWriteProperties();
		
		
		FieldUtils.getAllFieldsList(clazz).stream().filter(field -> {
			try {
				checkPutPatch(entity, oldEntity, writeProperties, field);
			} catch (NoSuchFieldException | IllegalAccessException e) {
				throw new RuntimeException(e);
			}
			
			return true;
		}).collect(Collectors.toSet());
	}
	
	private void checkPutPatch(test entity, test oldEntity, Set<String> writeProperties, Field field) throws NoSuchFieldException, IllegalAccessException {
		Field fieldTemp;
		Field fieldTempOld;
		try {
			fieldTemp = entity.getClass().getDeclaredField(field.getName());
			fieldTempOld = oldEntity.getClass().getDeclaredField(field.getName());
		} catch (NoSuchFieldException ex) {
			try {
				fieldTemp = entity.getClass().getSuperclass().getDeclaredField(field.getName());
				fieldTempOld = oldEntity.getClass().getSuperclass().getDeclaredField(field.getName());
			} catch (NoSuchFieldException ex1) {
				fieldTemp = entity.getClass().getSuperclass().getSuperclass().getDeclaredField(field.getName());
				fieldTempOld = oldEntity.getClass().getSuperclass().getSuperclass().getDeclaredField(field.getName());
			}
		}
		fieldTemp.setAccessible(true);
		Object value = fieldTemp.get(entity);
		
		fieldTempOld.setAccessible(true);
		Object valueOld = fieldTempOld.get(oldEntity);
		
		
		if (value == null) {
			if (!writeProperties.contains(field.getName())) {
				fieldTemp.set(entity, valueOld);
			}
		} else {
			if (!value.equals(valueOld)) {
				if (!writeProperties.contains(field.getName())) {
					if (valueOld instanceof OffsetDateTime valueOldDate) {
						if (value instanceof OffsetDateTime valueDate) {
							OffsetDateTime offsetDateTransform = valueDate.toInstant().atOffset(valueOldDate.getOffset());
							if (offsetDateTransform.equals(valueOldDate)) {
								fieldTemp.set(entity, valueOldDate);
							} else {
								throw new ForbiddenException("No Access on field: " + field.getName());
							}
						}
					} else if (fieldTemp.getName().equals("messageCounter")) {
						fieldTemp.setLong(entity, (long) valueOld);
					} else {
						throw new ForbiddenException("No Access on field: " + field.getName());
					}
				}
			}
		}
	}
	
}
