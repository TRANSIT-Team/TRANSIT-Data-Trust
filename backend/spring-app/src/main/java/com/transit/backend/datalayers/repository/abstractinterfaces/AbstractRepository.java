package com.transit.backend.datalayers.repository.abstractinterfaces;

import com.querydsl.core.types.Predicate;
import com.transit.backend.datalayers.domain.abstractclasses.AbstractEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import javax.persistence.QueryHint;
import java.util.Optional;
import java.util.UUID;

import static org.hibernate.jpa.QueryHints.HINT_CACHEABLE;

public interface AbstractRepository<test extends AbstractEntity> extends JpaRepository<test, UUID>, JpaSpecificationExecutor<test>, QuerydslPredicateExecutor<test> {
	@QueryHints(value = {@QueryHint(name = HINT_CACHEABLE, value = "true")})
	Optional<test> findByIdAndDeleted(UUID id, boolean deleted);
	
	@QueryHints(value = {@QueryHint(name = HINT_CACHEABLE, value = "true")})
	Optional<test> findOne(Predicate predicate);
	
	//@QueryHints(value = {@QueryHint(name = HINT_CACHEABLE, value = "true")})
	Iterable<test> findAll(Predicate predicate);
	
	//@QueryHints(value = {@QueryHint(name = HINT_CACHEABLE, value = "true")})
	Page<test> findAll(Predicate predicate, Pageable pageable);
}
