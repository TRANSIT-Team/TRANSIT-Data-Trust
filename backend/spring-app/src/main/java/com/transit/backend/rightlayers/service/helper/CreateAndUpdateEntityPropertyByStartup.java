//package com.transit.backend.service.rights.helper;
//
//import com.transit.backend.controller.rights.dto.itern.EntityPropertyDTOInternal;
//import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.reflect.FieldUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.context.event.ApplicationReadyEvent;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.context.event.ContextRefreshedEvent;
//import org.springframework.context.event.EventListener;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.reactive.function.client.WebClient;
//
//import java.lang.reflect.Field;
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction.clientRegistrationId;
//
//@Component
//@Slf4j
//public class CreateAndUpdateEntityPropertyByStartup {
//	@Autowired
//	private CheckEntityTypeClass checkEntityTypeClass;
//
//	Set<Class<?>> entityCLasses;
//
//	@Autowired
//	private WebClient client;
//
//	private String userId = "userId";
//
//
//	public String getUserName() {
//		return "backend-transit";
//	}
//
//	@EventListener(ContextRefreshedEvent.class)
//	public void createAndUpdateEntityPropertyByStartup() {
//		log.info("Creating entity property update is starting");
//		this.checkEntityTypeClass = new CheckEntityTypeClass();
//		entityCLasses = checkEntityTypeClass.getEntityClasses();
//		for (var clazz : entityCLasses) {
//			Field[] fields = FieldUtils.getAllFields(clazz);
//			//clazz.getDeclaredFields();
//			//				clazz.getDeclaredFields();
//
//			var clazzName = clazz.getSimpleName();
//			if (!clazzName.startsWith("Q") && !clazzName.equals("classes")) {
//				for (var field : fields) {
//					if (field.getType().isPrimitive()
//							|| field.getType() == UUID.class
//							|| field.getType() == String.class
//							|| field.getType() == OffsetDateTime.class) {
//						var fieldName = field.getName();
//						log.info(fieldName);
//						String query = "name==" + fieldName + " and entityClazz==" + clazzName;
//						ResponseEntity<List<EntityPropertyDTOInternal>> listValue = this.client
//								.get()
//								.uri(uriBuilder ->
//										uriBuilder.path("/entityproperty/")
//												.queryParam("filter", query)
//												.build())
//								.attributes(clientRegistrationId("keycloak-server"))
//								.retrieve()
//								.toEntity(new ParameterizedTypeReference<List<EntityPropertyDTOInternal>>() {
//								})
//								.block();
//						assert listValue != null;
//						var entityList = listValue.getBody();
//						assert entityList != null;
//						if (entityList.isEmpty()) {
//							var entity = new EntityPropertyDTOInternal();
//							entity.setName(fieldName);
//							entity.setEntityClazz(clazzName);
//							ResponseEntity<EntityPropertyDTOInternal> value = this.client
//									.post()
//									.uri(uriBuilder -> uriBuilder.path("/entityproperty/")
//											.queryParam(userId, getUserName())
//											.build())
//									.bodyValue(entity)
//									.attributes(clientRegistrationId("keycloak-server"))
//									.retrieve().
//									toEntity(EntityPropertyDTOInternal.class)
//									.block();
//							assert value != null;
//							if (!value.getStatusCode().equals(HttpStatus.CREATED)) {
//								throw new UnprocessableEntityExeption(value.getStatusCode().getReasonPhrase());
//							}
//						}
//					}
//				}
//			}
//		}
//		log.error("Class fields are all generated.");
//	}
//
//}
