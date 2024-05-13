package com.transit.backend.datalayers.repository;

import com.transit.backend.datalayers.domain.PackageProperty;
import com.transit.backend.datalayers.repository.abstractinterfaces.AbstractRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface PackagePropertyRepository extends AbstractRepository<PackageProperty> {

	/*

	@Query(
			value = "SELECT pp.* FROM PACKAGE_PROPERTIES pp inner join PACKAGES_PACKAGE_PROPERTIES ppp on pp.id=ppp.package_properties_id inner join PACKAGES p on ppp.packageItem_id = p.id where p.id = ?1",
			countQuery = "SELECT pp.* FROM PACKAGE_PROPERTIES pp inner join PACKAGES_PACKAGE_PROPERTIES ppp on pp.id=ppp.package_properties_id inner join PACKAGES p on ppp.packageItem_id = p.id where p.id = ?1",
			nativeQuery = true)
	Page<PackageProperty> findAllByPackageItemIdWithPagination(byte[] packageItemId, Pageable pageable);
	*/
	
	public Optional<PackageProperty> findPackagePropertyByKeyAndCompanyId(String key, UUID companyId);
	
	
}