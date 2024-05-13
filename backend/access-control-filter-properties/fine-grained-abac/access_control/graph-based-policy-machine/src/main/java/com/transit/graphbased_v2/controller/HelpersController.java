package com.transit.graphbased_v2.controller;

import com.transit.graphbased_v2.repository.AssigmentRepository;
import com.transit.graphbased_v2.repository.ObjectClazzRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/helpers")
public class HelpersController {
	@Autowired
	private ObjectClazzRepository objectClazzRepository;
	
	@Autowired
	private AssigmentRepository assigmentRepository;
	
	@GetMapping("/entityclass/{id}")
	public ResponseEntity getEntityClass(@PathVariable("id") UUID id) {
		String response = null;
		var node = objectClazzRepository.findById(id);
		if (node.isPresent()) {
			response = node.get().getEntityClass();
		} else {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping("/isshared/{id}")
	public ResponseEntity getIsShared(@PathVariable("id") UUID id) {
		Boolean response = false;
		var node = objectClazzRepository.findById(id);
		if (node.isPresent()) {
			var outgoingEdges = assigmentRepository.getOutgoingRelationships(id);
			if (outgoingEdges.size() > 1) {
				response = true;
			}
		}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
}
