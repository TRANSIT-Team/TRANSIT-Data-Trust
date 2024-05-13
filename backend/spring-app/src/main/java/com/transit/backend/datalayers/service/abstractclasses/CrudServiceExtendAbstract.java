package com.transit.backend.datalayers.service.abstractclasses;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.querydsl.core.types.Path;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.domain.Order;
import com.transit.backend.datalayers.domain.QCompanyIDToCompanyOID;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import com.transit.backend.datalayers.repository.CompanyAddressRepository;
import com.transit.backend.datalayers.repository.CompanyIdToCompanyOIDRepository;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import com.transit.backend.datalayers.service.helper.OrderServiceBeanHelper;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.AbstractMapper;
import com.transit.backend.exeptions.exeption.BadRequestException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.ValidationExeption;
import com.transit.backend.helper.QueryRewrite;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.domain.AccessResponseDTO;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.transferentities.FilterExtra;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import io.github.perplexhub.rsql.QuerySupport;
import io.github.perplexhub.rsql.RSQLCustomPredicate;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.criteria.Expression;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class CrudServiceExtendAbstract<test extends AbstractEntity, testDTO> {
	
	private final int[] finalLongestConditionNameArray = {-1};
	@Inject
	Validator validator;
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private PutPatchValidatorProperties<test> putPatchValidatorProperties;
	@Autowired
	private AccessService rightsService;
	@Autowired
	private CompanyIdToCompanyOIDRepository companyIdToCompanyOIDRepository;
	@Autowired
	private OrderServiceBeanHelper orderServiceBeanHelper;
	@Autowired
	private CompanyAddressRepository companyAddressRepository;
	
	public test saveInternal(test entity) {
		return getRepository().saveAndFlush(entity);
	}
	
	public abstract AbstractRepository<test> getRepository();
	
	public test createInternal(test entity) {
		return entity;
	}
	
	public test updateInternal(UUID primaryKey, test entity) {
		return getRepository().findByIdAndDeleted(primaryKey, false).map(test -> {
			if (usePutPatchPropertyFilter()) {
				Optional<AccessResponseDTO> rightsEntry;
				if (getEntityClazz().getSimpleName().equals(Company.class.getSimpleName())) {
					var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
					rightsEntry = rightsService.getAccess(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
				} else {
					rightsEntry = rightsService.getAccess(primaryKey);
				}
				
				putPatchValidatorProperties.validator(getEntityClazz(), rightsEntry, test, entity);
			}
			entity.setId(test.getId());
			entity.setCreateDate(test.getCreateDate());
			entity.setCreatedBy(test.getCreatedBy());
			
			return entity;
			
		}).orElseThrow(() -> new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey));
	}
	
	public abstract boolean usePutPatchPropertyFilter();
	
	public abstract Class<test> getEntityClazz();
	
	public testDTO partialUpdateInternal(UUID primaryKey, JsonMergePatch patch) {
		return partialUpdateSubMethod(primaryKey, patch);
	}
	
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public testDTO partialUpdateSubMethod(UUID primaryKey, JsonMergePatch patch) {
		Optional<test> test = getRepository().findByIdAndDeleted(primaryKey, false);
		if (test.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		} else {
			try {
				testDTO testDTO = getMapper().toDto(test.get());
				JsonNode original = objectMapper.valueToTree(testDTO);
				JsonNode patched = patch.apply(original);
				var response = objectMapper.treeToValue(patched, getEntityDTOClazz());
				var forValidation = getMapper().toEntity(response);
				
				if (forValidation instanceof Order forValidOrder) {
					var oldOrder = (Order) test.get();
					if (oldOrder.getParentOrder() != null) {
						if (forValidOrder.getParentOrder() != null && oldOrder.getParentOrder().getId().equals(forValidOrder.getParentOrder().getId()))
							forValidOrder.setParentOrder(oldOrder.getParentOrder());
					}
					forValidOrder = orderServiceBeanHelper.findFields(forValidOrder, oldOrder);
					
					
				}
				if (usePutPatchPropertyFilter()) {
					Optional<AccessResponseDTO> rightsEntry;
					if (getEntityClazz().getSimpleName().equals(Company.class.getSimpleName())) {
						var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById((UUID) primaryKey), QCompanyIDToCompanyOID.companyIDToCompanyOID);
						rightsEntry = rightsService.getAccess(companyIdToCompanyOIDRepository.findOne(spec).get().getCompanyOID());
					} else {
						rightsEntry = rightsService.getAccess(primaryKey);
					}
					putPatchValidatorProperties.validator(getEntityClazz(), rightsEntry, test.get(), forValidation);
				}
				return getMapper().toDto(forValidation);
				
			} catch (JsonPatchException | JsonProcessingException e) {
				throw new BadRequestException(e.getMessage());
			}
			
		}
	}
	
	public abstract AbstractMapper<test, testDTO> getMapper();
	
	public abstract Class<testDTO> getEntityDTOClazz();
	
	public test checkviolationsInternal(UUID primaryKey, testDTO dto) {
		Set<ConstraintViolation<testDTO>> violations = validator.validate(dto, ValidationGroups.Patch.class);
		
		
		if (violations.isEmpty()) {
			return getMapper().toEntity(dto);
		} else {
			
			Set<String> failures = violations
					.stream()
					.map(contraints -> contraints.getRootBeanClass().getSimpleName() +
							"." + contraints.getPropertyPath() + " " + contraints.getMessage())
					.collect(Collectors.toSet());
			String failure = "";
			for (String fail : failures) {
				failure = failure + fail + ",";
			}
			
			throw new ValidationExeption(failure);
		}
	}
	
	public Page<test> readInternal(FilterExtra pageParams, String query) {
		
		Pageable pageable;
		if (pageParams.isUseOtherParameters()) {
			pageable = new OffsetBasedPageRequest(pageParams.getTake(), pageParams.getSkip(), pageParams.getPageable().getSort(), pageParams.isPage());
		} else {
			pageable = pageParams.getPageable();
		}
		if (pageParams.isNoCompanyAddress()) {
			var values = new StringBuilder();
			var ids = companyAddressRepository.findAllProjectedBy();
			if (!ids.isEmpty()) {
				values.append("id=out=(");
				Set<UUID> nodeIds = new HashSet<>();
				for (var right : ids) {
					nodeIds.add(right.getId().getAddressId());
				}
				for (var node : nodeIds) {
					values.append(node);
					values.append(",");
				}
				
				if (values.toString().endsWith(",")) {
					values.deleteCharAt(values.length() - 1);
				}
				values.append(")");
				//hve to implement more
				if (query.isBlank()) {
					query = values.toString();
				} else {
					query = "(" + query + ") and " + values;
				}
			}
		}
		RSQLCustomPredicate<UUID> customPredicate = new RSQLCustomPredicate<>(new ComparisonOperator("=id=", false), UUID.class, input -> {
			//return input.getCriteriaBuilder().between(input.getPath().as(UUID.class), (UUID) input.getArguments().get(0)., (UUID) input.getArguments().get(1));
			return input.getCriteriaBuilder().like(input.getPath().as(String.class), (Expression<String>) input.getArguments().get(0));
		});
		QuerySupport querySupport = QuerySupport.builder().customPredicates(Arrays.asList(customPredicate)).build();
		
		var spec = RSQLQueryDslSupport.toPredicate(query, getQClazz());
		
		//Filtered Over RightsService
//		boolean createdByMyCompany = false;
//		if (createdByMyCompany) {
////			var temp = getRepository().findAll(spec);
////			Set<UUID> nodeIds = new HashSet<>();
////			temp.forEach(entry -> {
////				var keykloakTemp = this.keycloakRolesManagement.getUsersResource().search(entry.getCreatedBy());
////				if (!keykloakTemp.isEmpty()) {
////					var keykloakUser = keykloakTemp.stream().findFirst().get();
////					var user = userRepository.findByKeycloakId(UUID.fromString(keykloakUser.getId()));
////					if (user.isPresent()) {
////						if (user.get().getCompany().getId().equals(userHelperFunctions.getCompanyId(userHelperFunctions.getUserID()))) {
////							nodeIds.add(entry.getId());
////						}
////					}
////				}
////
////			});
////
////			if (!nodeIds.isEmpty()) {
////				var values = new StringBuilder();
////				if (getEntityClazz().getSimpleName().equals(CompanyAddress.class.getSimpleName())) {
////					values.append("address").append(".");
////				}
////				values.append("id=in=(");
////				for (var node : nodeIds) {
////					values.append(node);
////					values.append(",");
////				}
////
////				if (values.toString().endsWith(",")) {
////					values.deleteCharAt(values.length() - 1);
////				}
////				values.append(")");
////				spec = RSQLQueryDslSupport.toPredicate(values.toString(), getQClazz());
////				return getRepository().findAll(spec, pageable);
////			}
////			return getRepository().findAll(spec, pageable);
////
////
////		} else if (createdByMyCompany && otherMethod) {
//			var usersNames = userRepository.findBy(RSQLQueryDslSupport.toPredicate("company.id==" + userHelperFunctions.getCompanyId(userHelperFunctions.getUserID()), QUser.user), user -> user.project("keycloakEmail").all());
//			if (usersNames.isEmpty()) {
//				return Page.empty();
//			} else {
//				var userNameStringBuilder = new StringBuilder();
//				userNameStringBuilder.append("createdBy=in=(");
//				for (var node : usersNames) {
//					userNameStringBuilder.append(node.getKeycloakEmail());
//					userNameStringBuilder.append(",");
//				}
//
//				if (userNameStringBuilder.toString().endsWith(",")) {
//					userNameStringBuilder.deleteCharAt(userNameStringBuilder.length() - 1);
//				}
//				userNameStringBuilder.append(")");
//				if (query != null && !query.isBlank()) {
//					spec = RSQLQueryDslSupport.toPredicate("(" + query + ");" + userNameStringBuilder, getQClazz());
//				} else {
//					spec = RSQLQueryDslSupport.toPredicate(userNameStringBuilder.toString(), getQClazz());
//				}
//				return getRepository().findAll(spec, pageable);
//
//			}
//
//
//		} else {
//			final int[] test1 = {0};
//			final int testMax = 10;
//			var result = getRepository().findBy(spec, test -> test.stream().skip(10).map(test2 -> {
//				test1[0] = test1[0] + 1;
//				return test2;
//			}).takeWhile(test3 -> test1[0] < testMax)).collect(Collectors.toList());
//
//
//			var rrrr = getRepository().findAll(spec, pageable);
//			var rr = rrrr.stream().skip(10).map(test2 -> {
//				test1[0] = test1[0] + 1;
//				return test2;
//			}).takeWhile(test3 -> test1[0] < testMax);
//
//			new PageImpl<test>(rr.collect(Collectors.toList()), pageable, rrrr.getTotalElements());
//
		
		return getRepository().findAll(spec, pageable);
		
		
	}
	
	public abstract Path<test> getQClazz();
	
	public Optional<test> readOneInternal(UUID primaryKey) {
		var spec = RSQLQueryDslSupport.toPredicate(QueryRewrite.queryById(primaryKey), getQClazz());
		return getRepository().findOne(spec);
	}
	
	public test deleteInternal(UUID primaryKey) {
		
		Optional<test> test = getRepository().findById(primaryKey).filter(testFilter -> !testFilter.isDeleted());
		if (test.isEmpty()) {
			throw new NoSuchElementFoundOrDeleted(getEntityClazz().getSimpleName(), primaryKey);
		} else {
			test.get().setDeleted(true);
			return getRepository().saveAndFlush(test.get());
		}
	}
	
	
}

