package com.transit.backend.datalayers.repository;


import com.transit.backend.datalayers.domain.DefaultSharingRights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.UUID;

public interface DefaultSharingRightsRepository extends JpaRepository<DefaultSharingRights, UUID>, JpaSpecificationExecutor<DefaultSharingRights>, QuerydslPredicateExecutor<DefaultSharingRights> {
}

