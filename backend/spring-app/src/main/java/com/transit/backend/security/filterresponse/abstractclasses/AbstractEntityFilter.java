package com.transit.backend.security.filterresponse.abstractclasses;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.QCompanyIDToCompanyOID;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.helper.UserHelperFunctions;
import com.transit.backend.security.filterresponse.helper.StorageRights;
import com.transit.backend.security.filterresponse.interfaces.EntityFilterHelper;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import io.netty.resolver.DefaultAddressResolverGroup;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.AbstractOAuth2TokenAuthenticationToken;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public abstract class AbstractEntityFilter<test, test2 extends AbstractEntity> implements EntityFilterHelper<test, test2> {
	
	
	private WebClient webClient;
	
	@Autowired
	private UserHelperFunctions userhelper;
	
	
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	
	public AbstractEntityFilter() {
		HttpClient httpClient = HttpClient.create().resolver(DefaultAddressResolverGroup.INSTANCE);
		
		this.webClient = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.exchangeStrategies(ExchangeStrategies.builder().codecs(
						configurer -> configurer.defaultCodecs().maxInMemorySize(-1)
				).build())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}
	
	public abstract test transformToTransfer(test2 entity);
	
	@Override
	public UUID filterEntitiesCompanyId() {
		return userhelper.getCompanyId();
	}
	
	@Override
	public test filterEntities(test entity, UUID companyId, StorageRights storageRights) {
		var tempEntity = transformToEntity(entity);
		test2 finalEntity = tempEntity;
		Optional<AccessResponseDTO> rights = storageRights.getRightsForResponse().get(tempEntity.getId());
		
		if (rights == null) {
			if (tempEntity.getId() != null) {
				var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(tempEntity.getId()), QCompanyIDToCompanyOID.companyIDToCompanyOID);
				var temp = companyIdToCompanyOIDRepository.findOne(spec);
				if (temp.isPresent()) {
					rights = storageRights.getRightsForResponse().get(temp.get().getCompanyOID());
				}
			}
			
		}
		if (rights == null) {
			rights = Optional.empty();
		}
		
		if (rights.isEmpty()) {
			
			//Like PackageClass, which everyone can read
			var isAvailableForAllCompaniesBool = isAvailableForAllCompanies(tempEntity, entity);
			if (isAvailableForAllCompaniesBool) {
				entity = transformToTransfer(tempEntity, entity);
				return entity;
			}
			return null;
		} else {
			var readProperties = rights.get().getObjectProperties().getReadProperties();
			FieldUtils.getAllFieldsList(getClazz()).stream().filter(field -> {
				try {
					filter(finalEntity, readProperties, field);
				} catch (NoSuchFieldException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
				
				return true;
			}).collect(Collectors.toSet());
		}
		entity = transformToTransfer(finalEntity, entity);
		return entity;
	}
	
	public Set<UUID> collectIDs(test entity) {
		test2 temp = transformToEntity(entity);
		Set<UUID> uuids = new HashSet<>();
		if (entity instanceof Company comp) {
			var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(comp.getId()), QCompanyIDToCompanyOID.companyIDToCompanyOID);
			uuids.add(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
		} else {
			uuids.add(temp.getId());
		}
		return uuids;
	}
	
	public abstract test2 transformToEntity(test entity);
	
	public boolean isAvailableForAllCompanies(test2 tempEntity, test entity) {
		var myAccess = Boolean.FALSE;
		final String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
		var servletBuilder = ServletUriComponentsBuilder.fromCurrentRequest();
		var uriComponents = servletBuilder.build();
		var urlAccess = uriComponents.toUri();
		
		
		var uriNew = UriBuilder.fromUri(baseUrl).path(getPathToEntity(entity, tempEntity)).build();
		var requestUri = UriBuilder.fromUri("http://localhost:8080/api/v1").path(getPathToEntity(entity, tempEntity)).build();
		if (!urlAccess.toString().equals(uriNew.toString()) && !uriNew.toString().startsWith(urlAccess.toString())) {
			
			try {
				final var auth = (AbstractOAuth2TokenAuthenticationToken<?>) SecurityContextHolder.getContext().getAuthentication();
				
				myAccess = this.webClient
						.get()
						.uri(uriNew)
						.headers(headers -> headers.setBearerAuth(auth.getToken().getTokenValue()))
						.exchangeToMono(response -> {
									if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
										
										return Mono.just(Boolean.FALSE);
									} else if (response.statusCode().equals(HttpStatus.OK)) {
										
										return Mono.just(Boolean.TRUE);
									}
									
									response.bodyToMono(String.class).flatMap(body -> {
										return Mono.error(new UnprocessableEntityExeption(body));
									});
									return Mono.just(Boolean.FALSE);
								}
						)
						.block();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return myAccess != null && myAccess;
		}
		return false;
	}
	
	public abstract test transformToTransfer(test2 entity, test entityOld);
	
	public abstract Class<test2> getClazz();
	
	private void filter(test2 entity, Set<String> readProperties, Field field) throws NoSuchFieldException, IllegalAccessException {
		Field fieldTemp;
		try {
			fieldTemp = entity.getClass().getDeclaredField(field.getName());
		} catch (NoSuchFieldException ex) {
			try {
				fieldTemp = entity.getClass().getSuperclass().getDeclaredField(field.getName());
			} catch (NoSuchFieldException ex1) {
				fieldTemp = entity.getClass().getSuperclass().getSuperclass().getDeclaredField(field.getName());
			}
		}
		fieldTemp.setAccessible(true);
		Object value = fieldTemp.get(entity);
		
		if (value != null) {
			if (!readProperties.contains(field.getName())) {
				if (!fieldTemp.getType().equals(Boolean.TYPE)) {
					if (fieldTemp.getType().isPrimitive()) {
						if (fieldTemp.getType() == Byte.TYPE) {
							byte b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Short.TYPE) {
							short b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Character.TYPE) {
							char b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Integer.TYPE) {
							int b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Float.TYPE) {
							float b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Long.TYPE) {
							long b = 0;
							fieldTemp.set(entity, b);
						}
						if (fieldTemp.getType() == Double.TYPE) {
							double b = 0;
							fieldTemp.set(entity, b);
						}
					} else {
						fieldTemp.set(entity, null);
					}
				}
			}
		}
	}
	
	public abstract String getPathToEntity(test entity, test2 entity2);
	
	public void getUUIDsToDelete(List<UUID> idsList, List<UUID> idsToRemoveList, StorageRights storageRights) {
		if (!idsList.isEmpty()) {
			for (var id : idsList) {
				Optional<AccessResponseDTO> rights = storageRights.getRightsForResponse().get(id);
				if (rights == null) {
					var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(id), QCompanyIDToCompanyOID.companyIDToCompanyOID);
					var temp = companyIdToCompanyOIDRepository.findOne(spec);
					if (temp.isPresent()) {
						rights = storageRights.getRightsForResponse().get(temp.get().getCompanyOID());
					}
				}
				if (rights == null) {
					rights = Optional.empty();
				}
				
				if (rights.isEmpty()) {
//					(!(rights.get().getCreatorCompany().getCompanyId().equals(companyId)) &&
//							(rights.get().getCanReadCompanies() == null ||
//									!rights.get().getCanReadCompanies().stream().map(CompanyPairsDTO::getGetRightsCompany).toList().contains(companyId)))) {
					idsToRemoveList.add(id);
				}
			}
		}
	}
	
	
}
