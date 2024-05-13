package com.transit.backend.datalayers.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.transit.backend.datalayers.controller.abstractclasses.abstractmethods.PutPatchValidatorProperties;
import com.transit.backend.datalayers.controller.dto.LocationDTO;
import com.transit.backend.datalayers.domain.Car;
import com.transit.backend.datalayers.domain.Location;
import com.transit.backend.datalayers.domain.QLocation;
import com.transit.backend.datalayers.repository.CarRepository;
import com.transit.backend.datalayers.repository.LocationRepository;
import com.transit.backend.datalayers.service.CarLocationService;
import com.transit.backend.datalayers.service.helper.abstractclasses.OffsetBasedPageRequest;
import com.transit.backend.datalayers.service.mapper.LocationMapper;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundException;
import com.transit.backend.exeptions.exeption.NoSuchElementFoundOrDeleted;
import com.transit.backend.exeptions.exeption.UnprocessableEntityExeption;
import com.transit.backend.helper.verification.ValidationGroups;
import com.transit.backend.rightlayers.service.AccessService;
import com.transit.backend.transferentities.FilterExtra;
import io.github.perplexhub.rsql.RSQLQueryDslSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class CarLocationServiceBean implements CarLocationService {
	
	@Autowired
	LocationMapper mapper;
	@Autowired
	ObjectMapper objectMapper;
	@Inject
	Validator validator;
	@Autowired
	private CarRepository carRepository;
	@Autowired
	private LocationRepository locationRepository;
	@Autowired
	private AccessService rightsService;
	
	@Autowired
	private PutPatchValidatorProperties putPatchValidatorProperties;
	
	@Override
	public Location create(UUID carId, Location entity) {
		
		
		return carRepository
				.findById(carId)
				.map(
						p -> {
							var carLocation = locationRepository.saveAndFlush(entity);
							
							p.getLocations().add(carLocation);
							carRepository.saveAndFlush(p);
							return carLocation;
						})
				.orElseThrow(() -> new NoSuchElementFoundException(Car.class.getSimpleName(), carId));
	}
	
	@Override
	public Location update(UUID carId, UUID locationId, Location entity) {
		return carRepository
				.findByIdAndDeleted(carId, false)
				.map(
						p -> p.getLocations()
								.stream()
								.filter(carLocation -> carLocation.getId().equals(locationId))
								.filter(carLocation -> !carLocation.isDeleted())
								.findAny()
								.map(
										carLocation -> {
											putPatchValidatorProperties.validator(Location.class, rightsService.getAccess(locationId), carLocation, entity);
											entity.setId(locationId);
											entity.setCreateDate(locationRepository.getReferenceById(carLocation.getId()).getCreateDate());
											entity.setCreatedBy(locationRepository.getReferenceById(carLocation.getId()).getCreatedBy());
											return locationRepository.saveAndFlush(entity);
										})
								.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Location.class.getSimpleName(), locationId))
				)
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Car.class.getSimpleName(), carId));
		
	}
	
	@Override
	public Location partialUpdate(UUID carId, UUID locationId, JsonMergePatch patch) {
		return carRepository
				.findByIdAndDeleted(carId, false)
				.map(
						p ->
								p.getLocations()
										.stream()
										.filter(carLocation -> carLocation.getId().equals(locationId))
										.filter(carLocation -> !carLocation.isDeleted())
										.findAny()
										.map(
												carLocation -> {
													try {
														LocationDTO carLocationDTO = mapper.toDto(carLocation);
														JsonNode original = objectMapper.valueToTree(carLocationDTO);
														JsonNode patched = patch.apply(original);
														carLocationDTO = objectMapper.treeToValue(patched, LocationDTO.class);
														
														Set<ConstraintViolation<LocationDTO>> violations = validator.validate(carLocationDTO, ValidationGroups.Patch.class);
														if (violations.isEmpty()) {
															var carLocationPatched = mapper.toEntity(carLocationDTO);
															carLocationPatched.setId(locationId);
															putPatchValidatorProperties.validator(Location.class, rightsService.getAccess(locationId), carLocation, carLocationPatched);
															return locationRepository.saveAndFlush(carLocationPatched);
														} else {
															throw new ConstraintViolationException(
																	new HashSet<>(violations));
														}
													} catch (JsonPatchException | JsonProcessingException e) {
														throw new UnprocessableEntityExeption(e.getMessage());
													}
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Location.class.getSimpleName(), locationId)))
				.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Car.class.getSimpleName(), carId));
		
	}
	
	@Override
	public Collection<Location> read(UUID carId, String query, FilterExtra pageParams) {
		
		
		carRepository.findById(carId).orElseThrow(() -> new NoSuchElementFoundException(Car.class.getSimpleName(), carId));
		if (carRepository.findById(carId).map(Car::getLocations).isEmpty()) {
			return new TreeSet<>();
		}
		var searchString = carRepository.findById(carId).stream().map(Car::getLocations).flatMap(loc -> loc.stream().map(Location::getId)).map(UUID::toString).
				reduce("(", (string, uuidLoc) -> string + uuidLoc + ",");
		if (searchString.endsWith(",")) {
			searchString = searchString.substring(0, searchString.length() - 1);
		}
		
		
		searchString = searchString + ")";
		if (query.trim().isBlank()) {
			query += "id=in=" + searchString;
		} else {
			query = "( " + query + " ) and id=in=" + searchString;
		}
		var spec = RSQLQueryDslSupport.toPredicate(query, QLocation.location);
		Pageable pageable;
		if (pageParams.isUseOtherParameters()) {
			pageable = new OffsetBasedPageRequest(pageParams.getTake(), pageParams.getSkip(), true);
			return new TreeSet<>(locationRepository.findAll(spec, pageable).toList());
		} else {
			return StreamSupport
					.stream(locationRepository.findAll(spec).spliterator(), false)
					.collect(Collectors.toCollection(TreeSet::new));
		}
	}
	
	@Override
	public Optional<Location> readOne(UUID carId, UUID locationId) {
		
		return carRepository.findById(carId).map(
						p ->
								Optional.of(
										p
												.getLocations()
												.stream()
												.filter(carLocation -> carLocation.getId().equals(locationId))
												.findAny()
												.orElseThrow(() -> new NoSuchElementFoundException(Location.class.getSimpleName(), locationId))
								))
				.orElseThrow(() -> new NoSuchElementFoundException(Car.class.getSimpleName(), carId));
		
	}
	
	
	@Override
	public void delete(UUID carId, UUID locationId) {
		carRepository
				.findById(carId)
				.map(
						p ->
								p.getLocations()
										.stream()
										.filter(carLocation -> carLocation.getId().equals(locationId))
										.filter(carLocation -> !carLocation.isDeleted())
										.findAny()
										.map(
												carLocation -> {
													carLocation.setDeleted(true);
													locationRepository.saveAndFlush(carLocation);
													return Optional.empty();
												})
										.orElseThrow(() -> new NoSuchElementFoundOrDeleted(Location.class.getSimpleName(), locationId)))
				.orElseThrow(() -> new NoSuchElementFoundException(Car.class.getSimpleName(), carId));
	}
	
	
}
