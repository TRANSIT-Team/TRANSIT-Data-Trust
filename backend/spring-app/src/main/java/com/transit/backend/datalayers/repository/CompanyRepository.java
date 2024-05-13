package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.Company;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface CompanyRepository extends AbstractRepository<Company> {

}