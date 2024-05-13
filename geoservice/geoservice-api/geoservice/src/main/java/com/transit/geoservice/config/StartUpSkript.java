package com.transit.geoservice.config;

import com.transit.geoservice.domain.IdCombineMapping;
import com.transit.geoservice.domain.ZipCodesCombinedPolygon;
import com.transit.geoservice.domain.Zipcodesgermanystates;
import com.transit.geoservice.helper.IdListHelper;
import com.transit.geoservice.repository.IdMappingRepository;
import com.transit.geoservice.repository.ZipCodesGermanPolygonsCombinedRepository;
import com.transit.geoservice.repository.ZipcodesgermanypolygonsRepository;
import com.transit.geoservice.repository.ZipcodesgermanystatesRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.id.UUIDGenerator;
import org.locationtech.jts.geom.Geometry;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.paukov.combinatorics.CombinatoricsFactory.createSimpleCombinationGenerator;
import static org.paukov.combinatorics.CombinatoricsFactory.createVector;

@Component
@Slf4j
public class StartUpSkript implements CommandLineRunner {
	
	@Autowired
	private ZipcodesgermanystatesRepository zipcodesgermanystatesRepository;
	
	@Autowired
	private ZipCodesGermanPolygonsCombinedRepository zipCodesGermanPolygonsCombinedRepository;
	
	@Autowired
	private ZipcodesgermanypolygonsRepository zipcodesgermanypolygonsRepository;
	
	@Autowired
	private IdListHelper idListHelper;
	
	@Autowired
	private IdMappingRepository idMappingRepository;
	
	@Override
	public void run(String... args) throws Exception {
		var zipStates = zipcodesgermanystatesRepository.findAll();
		zipStates = new ArrayList<>(new HashSet<>(zipStates));
		if (zipCodesGermanPolygonsCombinedRepository.findById(zipStates.get(0).getZipcode()).isEmpty()) {
			
			List<String> zipcodes = new ArrayList<>();
			zipStates.forEach(zipState -> zipcodes.add(zipState.getZipcode()));
			ICombinatoricsVector<String> vector = createVector(zipcodes);
			
			//de
			int i = zipcodes.size();
			
			
			log.error("Length: " + i);
			Long counter = Long.valueOf(0);
			Generator<String> gen = createSimpleCombinationGenerator(vector, i);
			log.error("Germany");
			fullGermany(gen, i, counter);
			
			var states = new HashSet<String>();
			zipStates.forEach(zip -> states.add(zip.getState()));
			log.error(String.valueOf(states.size()));
			var listStates = new ArrayList<List<String>>();
			List<Zipcodesgermanystates> finalZipStates = zipStates;
			states.forEach(state -> {
				if (!state.isBlank()) {
					log.error(state);
					var tempList = new ArrayList<String>();
					ArrayList<String> finalTempList = tempList;
					finalZipStates.forEach(zip -> {
						if (state.equals(zip.getState())) {
							finalTempList.add(zip.getZipcode());
						}
					});
					tempList = new ArrayList<>(new HashSet<>(finalTempList));
					listStates.add(tempList);
				}
			});
			log.error("States");
			log.error(String.valueOf(listStates.size()));
			fullStates(listStates, counter);
			
			List<Integer> numbers = new ArrayList<>();
			numbers.add(0);
			numbers.add(1);
			numbers.add(2);
			numbers.add(3);
			numbers.add(4);
			numbers.add(5);
			numbers.add(6);
			numbers.add(7);
			numbers.add(8);
			numbers.add(9);
			ICombinatoricsVector<Integer> vectorNumber = createVector(numbers);
			log.error("Combinations");
			for (int j = 1; j <= numbers.size(); j++) {
				log.error(String.valueOf(j));
				Generator<Integer> genNumber = createSimpleCombinationGenerator(vectorNumber, j);
				var vectors = genNumber.generateAllObjects();
				fullNumber(vectors, counter, zipStates);
			}
			
			
			log.error("Ready with Creating Polygons");
		}
		log.error("Ready with StartuP");
	}
	
	private void fullGermany(Generator<String> gen, int i, Long counter) throws InterruptedException {
		
		
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		gen.generateAllObjects().forEach(data -> {
			CompletableFuture<Void> subNumber = CompletableFuture.supplyAsync(() -> {
						String id = idListHelper.getId(data.getVector());
						if (idMappingRepository.findByZips(id).isEmpty()) {
							Optional<Geometry> polygon = zipcodesgermanypolygonsRepository.getCombinedGeometry(data.getVector());
							isNotPresent(id, polygon);
						}
						return null;
					}
			);
			futures.add(subNumber);
		});
		
		
		execute(counter, futures);
		
		
	}
	
	private void fullNumber(List<ICombinatoricsVector<Integer>> gen, Long counter, List<Zipcodesgermanystates> zipstates) throws InterruptedException {
		
		
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		gen.forEach(data -> {
			CompletableFuture<Void> subNumber = CompletableFuture.supplyAsync(() -> {
						List<String> zips = new ArrayList<>();
						List<String> finalZips = zips;
						zipstates.forEach(state -> {
							data.getVector().forEach(startNumber -> {
								if (state.getZipcode().startsWith(String.valueOf(startNumber))) {
									finalZips.add(state.getZipcode());
								}
							});
						});
						zips = new ArrayList<>(new HashSet<>(finalZips));
						String id = idListHelper.getId(zips);
						if (idMappingRepository.findByZips(id).isEmpty()) {
							Optional<Geometry> polygon = zipcodesgermanypolygonsRepository.getCombinedGeometry(zips);
							isNotPresent(id, polygon);
						}
						return null;
					}
			);
			futures.add(subNumber);
		});
		
		
		execute(counter, futures);
		
	}
	
	private void fullStates(List<List<String>> gen, Long counter) throws InterruptedException {
		
		
		List<CompletableFuture<Void>> futures = new ArrayList<>();
		gen.forEach(data -> {
			CompletableFuture<Void> subNumber = CompletableFuture.supplyAsync(() -> {
						String id = idListHelper.getId(data);
						if (idMappingRepository.findByZips(id).isEmpty()) {
							Optional<Geometry> polygon = zipcodesgermanypolygonsRepository.getCombinedGeometry(data);
							isNotPresent(id, polygon);
						}
						return null;
					}
			);
			futures.add(subNumber);
		});
		
		
		execute(counter, futures);
		
	}
	
	
	private static void execute(Long counter, List<CompletableFuture<Void>> futures) throws InterruptedException {
		CompletableFuture<Void> allCompleted = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
		
		try {
			allCompleted.get();
			futures.forEach(entry -> {
				try {
					entry.get();
				} catch (InterruptedException | ExecutionException e) {
					log.error("Some Fail by generating polygon");
				}
			});
		} catch (ExecutionException e) {
			log.error("Some Fail by generating in list");
		}
		log.error(" Counter: " + counter);
		counter = counter + 1;
	}
	
	private void isNotPresent(String id, Optional<Geometry> polygon) {
		if (polygon.isPresent()) {
			var idCombined = new IdCombineMapping();
			idCombined.setZips(id);
			idCombined.setId(UUID.randomUUID());
			idCombined = idMappingRepository.saveAndFlush(idCombined);
			
			var polygonToSave = new ZipCodesCombinedPolygon();
			polygonToSave.setIdString(idCombined.getId().toString());
			polygonToSave.setGeometry(polygon.get());
			zipCodesGermanPolygonsCombinedRepository.saveAndFlush(polygonToSave);
			
		}
	}
	
}
	
	

