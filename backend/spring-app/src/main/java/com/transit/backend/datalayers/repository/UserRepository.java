package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.User;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRepository extends AbstractRepository<User> {
	
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	Optional<User> findByKeycloakId(UUID keycloakId);
	
	
	User getByKeycloakId(UUID keycloakId);
	
	List<User> findAllByKeycloakEmail(String keycloakEmail);
	
	List<User> findAllByCompanyId(UUID companyId);
	
	
}