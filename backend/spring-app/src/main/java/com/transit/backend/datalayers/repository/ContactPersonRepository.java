package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.ContactPerson;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactPersonRepository extends AbstractRepository<ContactPerson> {

}
